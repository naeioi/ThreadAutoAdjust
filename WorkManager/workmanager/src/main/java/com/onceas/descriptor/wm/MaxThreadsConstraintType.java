//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.5-b16-fcs 
// 	See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 	Any modifications to this file will be lost upon recompilation of the source schema. 
// 	Generated on: 2009.11.26 �� 04:48:36 CST 
//


package com.onceas.descriptor.wm;


/**
 * Java content class for anonymous complex type.
 * 	<p>The following schema fragment specifies the expected 	content contained within this java content object. 	(defined at file:/E:/wm_1_0.xsd line 81)
 * <p>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.ios.ac.cn/onceas}name"/>
 *         &lt;element ref="{http://www.ios.ac.cn/onceas}count"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
public interface MaxThreadsConstraintType {


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
     * Gets the value of the count property.
     * 
     * @return
     *     possible object is
     *     {@link TagStringValueType}
     *     {@link Count}
     */
    TagStringValueType getCount();

    /**
     * Sets the value of the count property.
     * 
     * @param value
     *     allowed object is
     *     {@link TagStringValueType}
     *     {@link Count}
     */
    void setCount(TagStringValueType value);

}
