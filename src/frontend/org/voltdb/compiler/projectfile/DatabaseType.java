//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.07.03 at 09:31:16 AM EDT 
//


package org.voltdb.compiler.projectfile;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for databaseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="databaseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="project" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="users" type="{}usersType" minOccurs="0"/>
 *         &lt;element name="groups" type="{}groupsType" minOccurs="0"/>
 *         &lt;element name="schemas" type="{}schemasType"/>
 *         &lt;element name="procedures" type="{}proceduresType"/>
 *         &lt;element name="partitions" type="{}partitionsType" minOccurs="0"/>
 *         &lt;element name="verticalpartitions" type="{}verticalpartitionsType" minOccurs="0"/>
 *         &lt;element name="classdependencies" type="{}classdependenciesType" minOccurs="0"/>
 *         &lt;element name="exports" type="{}exportsType" minOccurs="0"/>
 *         &lt;element name="snapshot" type="{}snapshotType" minOccurs="0"/>
 *       &lt;/all>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "databaseType", propOrder = {

})
public class DatabaseType {

    protected String project;
    protected UsersType users;
    protected GroupsType groups;
    @XmlElement(required = true)
    protected SchemasType schemas;
    @XmlElement(required = true)
    protected ProceduresType procedures;
    protected PartitionsType partitions;
    protected VerticalpartitionsType verticalpartitions;
    protected ClassdependenciesType classdependencies;
    protected ExportsType exports;
    protected SnapshotType snapshot;
    @XmlAttribute(required = true)
    protected String name;

    /**
     * Gets the value of the project property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProject() {
        return project;
    }

    /**
     * Sets the value of the project property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProject(String value) {
        this.project = value;
    }

    /**
     * Gets the value of the users property.
     * 
     * @return
     *     possible object is
     *     {@link UsersType }
     *     
     */
    public UsersType getUsers() {
        return users;
    }

    /**
     * Sets the value of the users property.
     * 
     * @param value
     *     allowed object is
     *     {@link UsersType }
     *     
     */
    public void setUsers(UsersType value) {
        this.users = value;
    }

    /**
     * Gets the value of the groups property.
     * 
     * @return
     *     possible object is
     *     {@link GroupsType }
     *     
     */
    public GroupsType getGroups() {
        return groups;
    }

    /**
     * Sets the value of the groups property.
     * 
     * @param value
     *     allowed object is
     *     {@link GroupsType }
     *     
     */
    public void setGroups(GroupsType value) {
        this.groups = value;
    }

    /**
     * Gets the value of the schemas property.
     * 
     * @return
     *     possible object is
     *     {@link SchemasType }
     *     
     */
    public SchemasType getSchemas() {
        return schemas;
    }

    /**
     * Sets the value of the schemas property.
     * 
     * @param value
     *     allowed object is
     *     {@link SchemasType }
     *     
     */
    public void setSchemas(SchemasType value) {
        this.schemas = value;
    }

    /**
     * Gets the value of the procedures property.
     * 
     * @return
     *     possible object is
     *     {@link ProceduresType }
     *     
     */
    public ProceduresType getProcedures() {
        return procedures;
    }

    /**
     * Sets the value of the procedures property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProceduresType }
     *     
     */
    public void setProcedures(ProceduresType value) {
        this.procedures = value;
    }

    /**
     * Gets the value of the partitions property.
     * 
     * @return
     *     possible object is
     *     {@link PartitionsType }
     *     
     */
    public PartitionsType getPartitions() {
        return partitions;
    }

    /**
     * Sets the value of the partitions property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartitionsType }
     *     
     */
    public void setPartitions(PartitionsType value) {
        this.partitions = value;
    }

    /**
     * Gets the value of the verticalpartitions property.
     * 
     * @return
     *     possible object is
     *     {@link VerticalpartitionsType }
     *     
     */
    public VerticalpartitionsType getVerticalpartitions() {
        return verticalpartitions;
    }

    /**
     * Sets the value of the verticalpartitions property.
     * 
     * @param value
     *     allowed object is
     *     {@link VerticalpartitionsType }
     *     
     */
    public void setVerticalpartitions(VerticalpartitionsType value) {
        this.verticalpartitions = value;
    }

    /**
     * Gets the value of the classdependencies property.
     * 
     * @return
     *     possible object is
     *     {@link ClassdependenciesType }
     *     
     */
    public ClassdependenciesType getClassdependencies() {
        return classdependencies;
    }

    /**
     * Sets the value of the classdependencies property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClassdependenciesType }
     *     
     */
    public void setClassdependencies(ClassdependenciesType value) {
        this.classdependencies = value;
    }

    /**
     * Gets the value of the exports property.
     * 
     * @return
     *     possible object is
     *     {@link ExportsType }
     *     
     */
    public ExportsType getExports() {
        return exports;
    }

    /**
     * Sets the value of the exports property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportsType }
     *     
     */
    public void setExports(ExportsType value) {
        this.exports = value;
    }

    /**
     * Gets the value of the snapshot property.
     * 
     * @return
     *     possible object is
     *     {@link SnapshotType }
     *     
     */
    public SnapshotType getSnapshot() {
        return snapshot;
    }

    /**
     * Sets the value of the snapshot property.
     * 
     * @param value
     *     allowed object is
     *     {@link SnapshotType }
     *     
     */
    public void setSnapshot(SnapshotType value) {
        this.snapshot = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
