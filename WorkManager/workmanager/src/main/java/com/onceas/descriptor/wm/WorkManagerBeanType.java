//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.5-b16-fcs 
// 	See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 	Any modifications to this file will be lost upon recompilation of the source schema. 
// 	Generated on: 2009.11.26 �� 04:48:36 CST 
//


package com.onceas.descriptor.wm;


/**
 * Java content class for anonymous complex type.
 * 	<p>The following schema fragment specifies the expected 	content contained within this java content object. 	(defined at file:/E:/wm_1_0.xsd line 38)
 * <p>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.ios.ac.cn/onceas}name"/>
 *         &lt;choice minOccurs="0">
 *           &lt;element ref="{http://www.ios.ac.cn/onceas}fair-share-request-class" minOccurs="0"/>
 *           &lt;element ref="{http://www.ios.ac.cn/onceas}context-request-class" minOccurs="0"/>
 *           &lt;element ref="{http://www.ios.ac.cn/onceas}response-time-request-class" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.ios.ac.cn/onceas}max-threads-constraint" minOccurs="0"/>
 *         &lt;element ref="{http://www.ios.ac.cn/onceas}min-threads-constraint" minOccurs="0"/>
 *         &lt;element ref="{http://www.ios.ac.cn/onceas}capacity" minOccurs="0"/>
 *         &lt;element ref="{http://www.ios.ac.cn/onceas}work-manager-shutdown-trigger" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
public interface WorkManagerBeanType {


    /**
     * Gets the value of the responseTimeRequestClass property.
     * 
     * @return
     *     possible object is
     *     {@link com.onceas.descriptor.wm.ResponseTimeRequestClassType}
     *     {@link com.onceas.descriptor.wm.ResponseTimeRequestClass}
     */
    com.onceas.descriptor.wm.ResponseTimeRequestClassType getResponseTimeRequestClass();

    /**
     * Sets the value of the responseTimeRequestClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link com.onceas.descriptor.wm.ResponseTimeRequestClassType}
     *     {@link com.onceas.descriptor.wm.ResponseTimeRequestClass}
     */
    void setResponseTimeRequestClass(com.onceas.descriptor.wm.ResponseTimeRequestClassType value);

    /**
     * Gets the value of the capacity property.
     * 
     * @return
     *     possible object is
     *     {@link CapacityType}
     *     {@link Capacity}
     */
    CapacityType getCapacity();

    /**
     * Sets the value of the capacity property.
     * 
     * @param value
     *     allowed object is
     *     {@link CapacityType}
     *     {@link Capacity}
     */
    void setCapacity(CapacityType value);

    /**
     * Gets the value of the contextRequestClass property.
     * 
     * @return
     *     possible object is
     *     {@link com.onceas.descriptor.wm.ContextRequestClassType}
     *     {@link ContextRequestClass}
     */
    com.onceas.descriptor.wm.ContextRequestClassType getContextRequestClass();

    /**
     * Sets the value of the contextRequestClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link com.onceas.descriptor.wm.ContextRequestClassType}
     *     {@link ContextRequestClass}
     */
    void setContextRequestClass(com.onceas.descriptor.wm.ContextRequestClassType value);

    /**
     * Gets the value of the minThreadsConstraint property.
     * 
     * @return
     *     possible object is
     *     {@link com.onceas.descriptor.wm.MinThreadsConstraint}
     *     {@link com.onceas.descriptor.wm.MinThreadsConstraintType}
     */
    com.onceas.descriptor.wm.MinThreadsConstraintType getMinThreadsConstraint();

    /**
     * Sets the value of the minThreadsConstraint property.
     * 
     * @param value
     *     allowed object is
     *     {@link com.onceas.descriptor.wm.MinThreadsConstraint}
     *     {@link com.onceas.descriptor.wm.MinThreadsConstraintType}
     */
    void setMinThreadsConstraint(com.onceas.descriptor.wm.MinThreadsConstraintType value);

    /**
     * Gets the value of the workManagerShutdownTrigger property.
     * 
     * @return
     *     possible object is
     *     {@link com.onceas.descriptor.wm.WorkManagerShutdownTrigger}
     *     {@link com.onceas.descriptor.wm.WorkManagerShutdownTriggerType}
     */
    com.onceas.descriptor.wm.WorkManagerShutdownTriggerType getWorkManagerShutdownTrigger();

    /**
     * Sets the value of the workManagerShutdownTrigger property.
     * 
     * @param value
     *     allowed object is
     *     {@link com.onceas.descriptor.wm.WorkManagerShutdownTrigger}
     *     {@link com.onceas.descriptor.wm.WorkManagerShutdownTriggerType}
     */
    void setWorkManagerShutdownTrigger(com.onceas.descriptor.wm.WorkManagerShutdownTriggerType value);

    /**
     * Gets the value of the maxThreadsConstraint property.
     * 
     * @return
     *     possible object is
     *     {@link com.onceas.descriptor.wm.MaxThreadsConstraint}
     *     {@link com.onceas.descriptor.wm.MaxThreadsConstraintType}
     */
    com.onceas.descriptor.wm.MaxThreadsConstraintType getMaxThreadsConstraint();

    /**
     * Sets the value of the maxThreadsConstraint property.
     * 
     * @param value
     *     allowed object is
     *     {@link com.onceas.descriptor.wm.MaxThreadsConstraint}
     *     {@link com.onceas.descriptor.wm.MaxThreadsConstraintType}
     */
    void setMaxThreadsConstraint(com.onceas.descriptor.wm.MaxThreadsConstraintType value);

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link TagStringValueType}
     *     {@link Name}
     */
    TagStringValueType getName();

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link TagStringValueType}
     *     {@link Name}
     */
    void setName(TagStringValueType value);

    /**
     * Gets the value of the fairShareRequestClass property.
     * 
     * @return
     *     possible object is
     *     {@link com.onceas.descriptor.wm.FairShareRequestClassType}
     *     {@link com.onceas.descriptor.wm.FairShareRequestClass}
     */
    com.onceas.descriptor.wm.FairShareRequestClassType getFairShareRequestClass();

    /**
     * Sets the value of the fairShareRequestClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link com.onceas.descriptor.wm.FairShareRequestClassType}
     *     {@link com.onceas.descriptor.wm.FairShareRequestClass}
     */
    void setFairShareRequestClass(com.onceas.descriptor.wm.FairShareRequestClassType value);

}
