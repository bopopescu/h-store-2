package edu.brown.optimizer.optimizations;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.voltdb.expressions.AbstractExpression;
import org.voltdb.planner.PlanAssembler;
import org.voltdb.planner.PlanColumn;
import org.voltdb.plannodes.AbstractPlanNode;
import org.voltdb.plannodes.AbstractScanPlanNode;
import org.voltdb.plannodes.DistinctPlanNode;
import org.voltdb.plannodes.HashAggregatePlanNode;
import org.voltdb.plannodes.ReceivePlanNode;
import org.voltdb.plannodes.SendPlanNode;
import org.voltdb.types.ExpressionType;
import org.voltdb.utils.Pair;

import edu.brown.expressions.ExpressionUtil;
import edu.brown.logging.LoggerUtil.LoggerBoolean;
import edu.brown.optimizer.PlanOptimizerState;
import edu.brown.plannodes.PlanNodeUtil;
import edu.brown.utils.CollectionUtil;

public class AggregatePushdownOptimization extends AbstractOptimization {
    private static final Logger LOG = Logger.getLogger(AggregatePushdownOptimization.class);
    private static final LoggerBoolean debug = new LoggerBoolean(LOG.isDebugEnabled());
    private static final LoggerBoolean trace = new LoggerBoolean(LOG.isTraceEnabled());

    public AggregatePushdownOptimization(PlanOptimizerState state) {
        super(state);
    }

    @Override
    public Pair<Boolean, AbstractPlanNode> optimize(AbstractPlanNode rootNode) {
        Collection<HashAggregatePlanNode> nodes = PlanNodeUtil.getPlanNodes(rootNode, HashAggregatePlanNode.class);
        if (nodes.size() != 1)
            return Pair.of(false, rootNode);
        final HashAggregatePlanNode node = CollectionUtil.first(nodes);
        
        // Skip single-partition query plans
        if (PlanNodeUtil.isDistributedQuery(rootNode) == false) {
            if (debug.get()) LOG.debug("SKIP - Not a distributed query plan");
            return (Pair.of(false, rootNode));
        }
        // Right now, Can't do averages
        for (ExpressionType et: node.getAggregateTypes()) {
            if (et.equals(ExpressionType.AGGREGATE_AVG)) {
                if (debug.get()) LOG.debug("SKIP - Right now can't optimize AVG()");
                return (Pair.of(false, rootNode));
            }
        }
        //if (debug.get()) LOG.debug("Trying to apply Aggregate pushdown optimization!");
        if (debug.get()) LOG.debug("AbstractPlanNode rootNode: "+ rootNode);
        if (debug.get()) LOG.debug( "<HashAggregatePlanNode>:" + nodes + " <HashAggregatePlanNode>[first]: "+ node);
        
        //for (ExpressionType et:node.getAggregateTypes()) if (debug.get()) LOG.debug("aggregate type:" + et);
        
        // Get the AbstractScanPlanNode that is directly below us
        Collection<AbstractScanPlanNode> scans = PlanNodeUtil.getPlanNodes(node, AbstractScanPlanNode.class);
        if (debug.get()) LOG.debug("<ScanPlanNodes>: "+ scans);
        // XXX:why we need this ???
        if (scans.size() != 1) {
            if (debug.get()) LOG.debug("SKIP - Multiple scans!");
            return (Pair.of(false, rootNode));
        }
        // XXX: when there will be more than one scan_node ???
        AbstractScanPlanNode scan_node = CollectionUtil.first(scans);
        if (debug.get()) LOG.debug("<ScanPlanNodes>[first]: "+ scan_node);
        assert (scan_node != null);
        
        // Skip if we're already directly after the scan (meaning no network traffic) ??? why we need this
        if (scan_node.getParent(0).equals(node)) {
            if (debug.get())
                LOG.debug("SKIP - Aggregate does not need to be distributed");
            return (Pair.of(false, rootNode));
        }
        
        // Check if this is count(distinct) query
        // If it is then we can only pushdown the DISTINCT
        AbstractPlanNode clone_node = null;
        if (node.getAggregateTypes().get(0) == ExpressionType.AGGREGATE_COUNT) {
            for (AbstractPlanNode child : node.getChildren()) {
                if (child.getClass().equals(DistinctPlanNode.class)) {
                    try {
                        clone_node = (AbstractPlanNode) child.clone(false, true);
                    } catch (CloneNotSupportedException ex) {
                        throw new RuntimeException(ex);
                    }
                    state.markDirty(clone_node);
                    break;
                }
            } // FOR
        }
        
        // Note that we don't want actually move the existing aggregate. We just
        // want to clone it and then
        // attach it down below the SEND/RECIEVE so that we calculate the
        // aggregate in parallel
        if (clone_node == null) {
            try {
                clone_node = (HashAggregatePlanNode) node.clone(false, true);
            } catch (CloneNotSupportedException ex) {
                throw new RuntimeException(ex);
            }
            
            state.markDirty(clone_node);
            HashAggregatePlanNode clone_agg = (HashAggregatePlanNode) clone_node;
            if (debug.get()) LOG.debug("make dirty clone_node:" + clone_node);
            if (debug.get()) LOG.debug("clone_agg:" + clone_agg);
            // Set original AggregateNode to contain sum
            if (clone_agg.getAggregateTypes().size() > 0) {
                List<ExpressionType> exp_types = node.getAggregateTypes();
                exp_types.clear();
                
                for (int i=0; i < clone_agg.getAggregateTypes().size(); i++) {
                    ExpressionType origType = clone_agg.getAggregateTypes().get(i);
                    switch (origType) {
                        case AGGREGATE_COUNT:
                        case AGGREGATE_COUNT_STAR:
                        case AGGREGATE_SUM:
                            exp_types.add(ExpressionType.AGGREGATE_SUM);
                            break;
                        case AGGREGATE_MAX:
                        case AGGREGATE_MIN:
                            exp_types.add(origType);
                            break;
                        case AGGREGATE_AVG:
                            // AVG will be dealt later
                            //exp_types.add(origType);
                            break;
                        default:
                            throw new RuntimeException("Unexpected ExpressionType " + origType);
                    } // SWITCH
                }
            }
            
            // IMPORTANT: If we have GROUP BY columns, then we need to make sure
            // that those columns are always passed up the query tree at the pushed
            // down node, even if the final answer doesn't need it
            if (node.getGroupByColumnGuids().isEmpty() == false) {
                // XXX: this should really be checked... I think it should be node other than clone_agg !!!
                for (Integer guid : node.getGroupByColumnGuids()) {
                    if (clone_agg.getOutputColumnGUIDs().contains(guid) == false) {
                        clone_agg.getOutputColumnGUIDs().add(guid);
                    }
                } // FOR
            }

            assert (clone_agg.getGroupByColumnOffsets().size() == node.getGroupByColumnOffsets().size());
            assert (clone_agg.getGroupByColumnNames().size() == node.getGroupByColumnNames().size());
            assert (clone_agg.getGroupByColumnGuids().size() == node.getGroupByColumnGuids().size()) : clone_agg.getGroupByColumnGuids().size() + " not equal " + node.getGroupByColumnGuids().size();
            assert (clone_agg.getAggregateTypes().size() == node.getAggregateTypes().size());
            assert (clone_agg.getAggregateColumnGuids().size() == node.getAggregateColumnGuids().size());
            assert (clone_agg.getAggregateColumnNames().size() == node.getAggregateColumnNames().size());
            assert (clone_agg.getAggregateOutputColumns().size() == node.getAggregateOutputColumns().size());
        }
        assert (clone_node != null);
        
        // XXX:Fixme:Down below part can not deal with Join table aggregate !!!...
        
        // But this means we have to also update the RECEIVE to only expect the
        // columns that the AggregateNode will be sending along
        ReceivePlanNode recv_node = null;
        if (clone_node instanceof DistinctPlanNode) {
            recv_node = (ReceivePlanNode) node.getChild(0).getChild(0);
        } else {
            recv_node = (ReceivePlanNode) node.getChild(0);
        }
        recv_node.getOutputColumnGUIDs().clear();
        recv_node.getOutputColumnGUIDs().addAll(clone_node.getOutputColumnGUIDs());
        state.markDirty(recv_node);

        assert (recv_node.getChild(0) instanceof SendPlanNode);
        SendPlanNode send_node = (SendPlanNode) recv_node.getChild(0);
        send_node.getOutputColumnGUIDs().clear();
        send_node.getOutputColumnGUIDs().addAll(clone_node.getOutputColumnGUIDs());
        send_node.addIntermediary(clone_node);
        state.markDirty(send_node);

        // 2011-12-08: We now need to correct the aggregate columns for the
        // original plan node
        if ((clone_node instanceof DistinctPlanNode) == false) {
            node.getAggregateColumnGuids().clear();
            for (Integer aggOutput : clone_node.getOutputColumnGUIDs()) {
                PlanColumn planCol = state.plannerContext.get(aggOutput);
                assert (planCol != null);
                AbstractExpression exp = planCol.getExpression();
                assert (exp != null);
                Collection<String> refTables = ExpressionUtil.getReferencedTableNames(exp);
                assert (refTables != null);
                if (refTables.size() == 1 && refTables.contains(PlanAssembler.AGGREGATE_TEMP_TABLE)) {
                    node.getAggregateColumnGuids().add(planCol.guid());
                }
            } // FOR
        }

        if (debug.get()) {
            //LOG.debug("Successfully applied optimization! Eat that John Hugg!");
            if (trace.get())
                LOG.trace("\n" + PlanNodeUtil.debug(rootNode));
        }

        return Pair.of(true, rootNode);
    }

}
