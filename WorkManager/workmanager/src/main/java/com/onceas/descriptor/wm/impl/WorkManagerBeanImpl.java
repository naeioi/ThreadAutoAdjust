//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.5-b16-fcs 
// 	See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 	Any modifications to this file will be lost upon recompilation of the source schema. 
// 	Generated on: 2009.11.26 �� 04:48:36 CST 
//


package com.onceas.descriptor.wm.impl;

public class WorkManagerBeanImpl
    extends com.onceas.descriptor.wm.impl.WorkManagerBeanTypeImpl
    implements com.onceas.descriptor.wm.WorkManagerBean, com.sun.xml.bind.RIElement, com.sun.xml.bind.JAXBObject, com.onceas.descriptor.wm.impl.runtime.UnmarshallableObject, com.onceas.descriptor.wm.impl.runtime.XMLSerializable, com.onceas.descriptor.wm.impl.runtime.ValidatableObject
{

    public final static Class version = (JAXBVersion.class);
    private static com.sun.msv.grammar.Grammar schemaFragment;

    private final static Class PRIMARY_INTERFACE_CLASS() {
        return (com.onceas.descriptor.wm.WorkManagerBean.class);
    }

    public String ____jaxb_ri____getNamespaceURI() {
        return "http://www.ios.ac.cn/onceas";
    }

    public String ____jaxb_ri____getLocalName() {
        return "work-manager-bean";
    }

    public com.onceas.descriptor.wm.impl.runtime.UnmarshallingEventHandler createUnmarshaller(com.onceas.descriptor.wm.impl.runtime.UnmarshallingContext context) {
        return new Unmarshaller(context);
    }

    public void serializeBody(com.onceas.descriptor.wm.impl.runtime.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
        context.startElement("http://www.ios.ac.cn/onceas", "work-manager-bean");
        super.serializeURIs(context);
        context.endNamespaceDecls();
        super.serializeAttributes(context);
        context.endAttributes();
        super.serializeBody(context);
        context.endElement();
    }

    public void serializeAttributes(com.onceas.descriptor.wm.impl.runtime.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
    }

    public void serializeURIs(com.onceas.descriptor.wm.impl.runtime.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
    }

    public Class getPrimaryInterface() {
        return (com.onceas.descriptor.wm.WorkManagerBean.class);
    }

    public com.sun.msv.verifier.DocumentDeclaration createRawValidator() {
        if (schemaFragment == null) {
            schemaFragment = com.sun.xml.bind.validator.SchemaDeserializer.deserialize((
 "\u00ac\u00ed\u0000\u0005sr\u0000\'com.sun.msv.grammar.trex.ElementPattern\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0001L\u0000"
+"\tnameClasst\u0000\u001fLcom/sun/msv/grammar/NameClass;xr\u0000\u001ecom.sun.msv."
+"grammar.ElementExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0002Z\u0000\u001aignoreUndeclaredAttributesL\u0000"
+"\fcontentModelt\u0000 Lcom/sun/msv/grammar/Expression;xr\u0000\u001ecom.sun."
+"msv.grammar.Expression\u00f8\u0018\u0082\u00e8N5~O\u0002\u0000\u0002L\u0000\u0013epsilonReducibilityt\u0000\u0013Lj"
+"ava/lang/Boolean;L\u0000\u000bexpandedExpq\u0000~\u0000\u0003xppp\u0000sr\u0000\u001fcom.sun.msv.gra"
+"mmar.SequenceExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000\u001dcom.sun.msv.grammar.BinaryExp"
+"\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0002L\u0000\u0004exp1q\u0000~\u0000\u0003L\u0000\u0004exp2q\u0000~\u0000\u0003xq\u0000~\u0000\u0004ppsq\u0000~\u0000\u0007ppsq\u0000~\u0000\u0007pps"
+"q\u0000~\u0000\u0007ppsq\u0000~\u0000\u0007ppsq\u0000~\u0000\u0007ppsr\u0000\u001dcom.sun.msv.grammar.ChoiceExp\u0000\u0000\u0000\u0000"
+"\u0000\u0000\u0000\u0001\u0002\u0000\u0000xq\u0000~\u0000\bppsq\u0000~\u0000\u0000pp\u0000sq\u0000~\u0000\u000fppsr\u0000 com.sun.msv.grammar.OneO"
+"rMoreExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000\u001ccom.sun.msv.grammar.UnaryExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002"
+"\u0000\u0001L\u0000\u0003expq\u0000~\u0000\u0003xq\u0000~\u0000\u0004sr\u0000\u0011java.lang.Boolean\u00cd r\u0080\u00d5\u009c\u00fa\u00ee\u0002\u0000\u0001Z\u0000\u0005valuex"
+"p\u0000psr\u0000 com.sun.msv.grammar.AttributeExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0002L\u0000\u0003expq\u0000~\u0000"
+"\u0003L\u0000\tnameClassq\u0000~\u0000\u0001xq\u0000~\u0000\u0004q\u0000~\u0000\u0017psr\u00002com.sun.msv.grammar.Expres"
+"sion$AnyStringExpression\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xq\u0000~\u0000\u0004sq\u0000~\u0000\u0016\u0001psr\u0000 com.sun"
+".msv.grammar.AnyNameClass\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000\u001dcom.sun.msv.grammar."
+"NameClass\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xpsr\u00000com.sun.msv.grammar.Expression$Eps"
+"ilonExpression\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xq\u0000~\u0000\u0004q\u0000~\u0000\u001cpsr\u0000#com.sun.msv.grammar"
+".SimpleNameClass\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0002L\u0000\tlocalNamet\u0000\u0012Ljava/lang/String;"
+"L\u0000\fnamespaceURIq\u0000~\u0000#xq\u0000~\u0000\u001et\u0000\u001dcom.onceas.descriptor.wm.Namet\u0000"
+"+http://java.sun.com/jaxb/xjc/dummy-elementssq\u0000~\u0000\u0000pp\u0000sq\u0000~\u0000\u0007p"
+"psq\u0000~\u0000\u0000pp\u0000sq\u0000~\u0000\u000fppsq\u0000~\u0000\u0013q\u0000~\u0000\u0017psq\u0000~\u0000\u0018q\u0000~\u0000\u0017pq\u0000~\u0000\u001bq\u0000~\u0000\u001fq\u0000~\u0000!sq\u0000"
+"~\u0000\"t\u0000+com.onceas.descriptor.wm.TagStringValueTypeq\u0000~\u0000&sq\u0000~\u0000\u000f"
+"ppsq\u0000~\u0000\u0018q\u0000~\u0000\u0017psr\u0000\u001bcom.sun.msv.grammar.DataExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0003L\u0000\u0002d"
+"tt\u0000\u001fLorg/relaxng/datatype/Datatype;L\u0000\u0006exceptq\u0000~\u0000\u0003L\u0000\u0004namet\u0000\u001dL"
+"com/sun/msv/util/StringPair;xq\u0000~\u0000\u0004ppsr\u0000\"com.sun.msv.datatype"
+".xsd.QnameType\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000*com.sun.msv.datatype.xsd.Builti"
+"nAtomicType\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000%com.sun.msv.datatype.xsd.ConcreteT"
+"ype\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000\'com.sun.msv.datatype.xsd.XSDatatypeImpl\u0000\u0000\u0000"
+"\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0003L\u0000\fnamespaceUriq\u0000~\u0000#L\u0000\btypeNameq\u0000~\u0000#L\u0000\nwhiteSpacet\u0000."
+"Lcom/sun/msv/datatype/xsd/WhiteSpaceProcessor;xpt\u0000 http://ww"
+"w.w3.org/2001/XMLSchemat\u0000\u0005QNamesr\u00005com.sun.msv.datatype.xsd."
+"WhiteSpaceProcessor$Collapse\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000,com.sun.msv.datat"
+"ype.xsd.WhiteSpaceProcessor\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xpsr\u00000com.sun.msv.gram"
+"mar.Expression$NullSetExpression\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xq\u0000~\u0000\u0004ppsr\u0000\u001bcom.s"
+"un.msv.util.StringPair\u00d0t\u001ejB\u008f\u008d\u00a0\u0002\u0000\u0002L\u0000\tlocalNameq\u0000~\u0000#L\u0000\fnamespa"
+"ceURIq\u0000~\u0000#xpq\u0000~\u0000<q\u0000~\u0000;sq\u0000~\u0000\"t\u0000\u0004typet\u0000)http://www.w3.org/2001"
+"/XMLSchema-instanceq\u0000~\u0000!sq\u0000~\u0000\"t\u0000\u0004namet\u0000\u001bhttp://www.ios.ac.cn"
+"/onceassq\u0000~\u0000\u000fppsq\u0000~\u0000\u000fppsq\u0000~\u0000\u000fppsq\u0000~\u0000\u000fppsq\u0000~\u0000\u000fppsq\u0000~\u0000\u000fq\u0000~\u0000\u0017ps"
+"q\u0000~\u0000\u0000q\u0000~\u0000\u0017p\u0000sq\u0000~\u0000\u000fppsq\u0000~\u0000\u0013q\u0000~\u0000\u0017psq\u0000~\u0000\u0018q\u0000~\u0000\u0017pq\u0000~\u0000\u001bq\u0000~\u0000\u001fq\u0000~\u0000!s"
+"q\u0000~\u0000\"t\u0000.com.onceas.descriptor.wm.FairShareRequestClassq\u0000~\u0000&s"
+"q\u0000~\u0000\u0000q\u0000~\u0000\u0017p\u0000sq\u0000~\u0000\u0007ppsq\u0000~\u0000\u0000pp\u0000sq\u0000~\u0000\u000fppsq\u0000~\u0000\u0013q\u0000~\u0000\u0017psq\u0000~\u0000\u0018q\u0000~\u0000\u0017"
+"pq\u0000~\u0000\u001bq\u0000~\u0000\u001fq\u0000~\u0000!sq\u0000~\u0000\"t\u00002com.onceas.descriptor.wm.FairShareR"
+"equestClassTypeq\u0000~\u0000&sq\u0000~\u0000\u000fppsq\u0000~\u0000\u0018q\u0000~\u0000\u0017pq\u0000~\u00004q\u0000~\u0000Dq\u0000~\u0000!sq\u0000~\u0000"
+"\"t\u0000\u0018fair-share-request-classq\u0000~\u0000Iq\u0000~\u0000!sq\u0000~\u0000\u0000pp\u0000sq\u0000~\u0000\u000fppsq\u0000~\u0000"
+"\u0013q\u0000~\u0000\u0017psq\u0000~\u0000\u0018q\u0000~\u0000\u0017pq\u0000~\u0000\u001bq\u0000~\u0000\u001fq\u0000~\u0000!sq\u0000~\u0000\"t\u0000,com.onceas.descri"
+"ptor.wm.ContextRequestClassq\u0000~\u0000&sq\u0000~\u0000\u0000pp\u0000sq\u0000~\u0000\u0007ppsq\u0000~\u0000\u0000pp\u0000sq"
+"\u0000~\u0000\u000fppsq\u0000~\u0000\u0013q\u0000~\u0000\u0017psq\u0000~\u0000\u0018q\u0000~\u0000\u0017pq\u0000~\u0000\u001bq\u0000~\u0000\u001fq\u0000~\u0000!sq\u0000~\u0000\"t\u00000com.on"
+"ceas.descriptor.wm.ContextRequestClassTypeq\u0000~\u0000&sq\u0000~\u0000\u000fppsq\u0000~\u0000"
+"\u0018q\u0000~\u0000\u0017pq\u0000~\u00004q\u0000~\u0000Dq\u0000~\u0000!sq\u0000~\u0000\"t\u0000\u0015context-request-classq\u0000~\u0000Isq\u0000"
+"~\u0000\u0000pp\u0000sq\u0000~\u0000\u000fppsq\u0000~\u0000\u0013q\u0000~\u0000\u0017psq\u0000~\u0000\u0018q\u0000~\u0000\u0017pq\u0000~\u0000\u001bq\u0000~\u0000\u001fq\u0000~\u0000!sq\u0000~\u0000\"t"
+"\u00001com.onceas.descriptor.wm.ResponseTimeRequestClassq\u0000~\u0000&sq\u0000~"
+"\u0000\u0000pp\u0000sq\u0000~\u0000\u0007ppsq\u0000~\u0000\u0000pp\u0000sq\u0000~\u0000\u000fppsq\u0000~\u0000\u0013q\u0000~\u0000\u0017psq\u0000~\u0000\u0018q\u0000~\u0000\u0017pq\u0000~\u0000\u001bq"
+"\u0000~\u0000\u001fq\u0000~\u0000!sq\u0000~\u0000\"t\u00005com.onceas.descriptor.wm.ResponseTimeReque"
+"stClassTypeq\u0000~\u0000&sq\u0000~\u0000\u000fppsq\u0000~\u0000\u0018q\u0000~\u0000\u0017pq\u0000~\u00004q\u0000~\u0000Dq\u0000~\u0000!sq\u0000~\u0000\"t\u0000\u001b"
+"response-time-request-classq\u0000~\u0000Isq\u0000~\u0000\u000fppsq\u0000~\u0000\u000fq\u0000~\u0000\u0017psq\u0000~\u0000\u0000q\u0000"
+"~\u0000\u0017p\u0000sq\u0000~\u0000\u000fppsq\u0000~\u0000\u0013q\u0000~\u0000\u0017psq\u0000~\u0000\u0018q\u0000~\u0000\u0017pq\u0000~\u0000\u001bq\u0000~\u0000\u001fq\u0000~\u0000!sq\u0000~\u0000\"t\u0000"
+"-com.onceas.descriptor.wm.MaxThreadsConstraintq\u0000~\u0000&sq\u0000~\u0000\u0000q\u0000~"
+"\u0000\u0017p\u0000sq\u0000~\u0000\u0007ppsq\u0000~\u0000\u0000pp\u0000sq\u0000~\u0000\u000fppsq\u0000~\u0000\u0013q\u0000~\u0000\u0017psq\u0000~\u0000\u0018q\u0000~\u0000\u0017pq\u0000~\u0000\u001bq\u0000"
+"~\u0000\u001fq\u0000~\u0000!sq\u0000~\u0000\"t\u00001com.onceas.descriptor.wm.MaxThreadsConstrai"
+"ntTypeq\u0000~\u0000&sq\u0000~\u0000\u000fppsq\u0000~\u0000\u0018q\u0000~\u0000\u0017pq\u0000~\u00004q\u0000~\u0000Dq\u0000~\u0000!sq\u0000~\u0000\"t\u0000\u0016max-t"
+"hreads-constraintq\u0000~\u0000Iq\u0000~\u0000!sq\u0000~\u0000\u000fppsq\u0000~\u0000\u000fq\u0000~\u0000\u0017psq\u0000~\u0000\u0000q\u0000~\u0000\u0017p\u0000"
+"sq\u0000~\u0000\u000fppsq\u0000~\u0000\u0013q\u0000~\u0000\u0017psq\u0000~\u0000\u0018q\u0000~\u0000\u0017pq\u0000~\u0000\u001bq\u0000~\u0000\u001fq\u0000~\u0000!sq\u0000~\u0000\"t\u0000-com."
+"onceas.descriptor.wm.MinThreadsConstraintq\u0000~\u0000&sq\u0000~\u0000\u0000q\u0000~\u0000\u0017p\u0000s"
+"q\u0000~\u0000\u0007ppsq\u0000~\u0000\u0000pp\u0000sq\u0000~\u0000\u000fppsq\u0000~\u0000\u0013q\u0000~\u0000\u0017psq\u0000~\u0000\u0018q\u0000~\u0000\u0017pq\u0000~\u0000\u001bq\u0000~\u0000\u001fq\u0000"
+"~\u0000!sq\u0000~\u0000\"t\u00001com.onceas.descriptor.wm.MinThreadsConstraintTyp"
+"eq\u0000~\u0000&sq\u0000~\u0000\u000fppsq\u0000~\u0000\u0018q\u0000~\u0000\u0017pq\u0000~\u00004q\u0000~\u0000Dq\u0000~\u0000!sq\u0000~\u0000\"t\u0000\u0016min-thread"
+"s-constraintq\u0000~\u0000Iq\u0000~\u0000!sq\u0000~\u0000\u000fppsq\u0000~\u0000\u000fq\u0000~\u0000\u0017psq\u0000~\u0000\u0000q\u0000~\u0000\u0017p\u0000sq\u0000~\u0000"
+"\u000fppsq\u0000~\u0000\u0013q\u0000~\u0000\u0017psq\u0000~\u0000\u0018q\u0000~\u0000\u0017pq\u0000~\u0000\u001bq\u0000~\u0000\u001fq\u0000~\u0000!sq\u0000~\u0000\"t\u0000!com.oncea"
+"s.descriptor.wm.Capacityq\u0000~\u0000&sq\u0000~\u0000\u0000q\u0000~\u0000\u0017p\u0000sq\u0000~\u0000\u0007ppsq\u0000~\u0000\u0000pp\u0000s"
+"q\u0000~\u0000\u000fppsq\u0000~\u0000\u0013q\u0000~\u0000\u0017psq\u0000~\u0000\u0018q\u0000~\u0000\u0017pq\u0000~\u0000\u001bq\u0000~\u0000\u001fq\u0000~\u0000!sq\u0000~\u0000\"t\u0000%com.o"
+"nceas.descriptor.wm.CapacityTypeq\u0000~\u0000&sq\u0000~\u0000\u000fppsq\u0000~\u0000\u0018q\u0000~\u0000\u0017pq\u0000~"
+"\u00004q\u0000~\u0000Dq\u0000~\u0000!sq\u0000~\u0000\"t\u0000\bcapacityq\u0000~\u0000Iq\u0000~\u0000!sq\u0000~\u0000\u000fppsq\u0000~\u0000\u000fq\u0000~\u0000\u0017ps"
+"q\u0000~\u0000\u0000q\u0000~\u0000\u0017p\u0000sq\u0000~\u0000\u000fppsq\u0000~\u0000\u0013q\u0000~\u0000\u0017psq\u0000~\u0000\u0018q\u0000~\u0000\u0017pq\u0000~\u0000\u001bq\u0000~\u0000\u001fq\u0000~\u0000!s"
+"q\u0000~\u0000\"t\u00003com.onceas.descriptor.wm.WorkManagerShutdownTriggerq"
+"\u0000~\u0000&sq\u0000~\u0000\u0000q\u0000~\u0000\u0017p\u0000sq\u0000~\u0000\u0007ppsq\u0000~\u0000\u0000pp\u0000sq\u0000~\u0000\u000fppsq\u0000~\u0000\u0013q\u0000~\u0000\u0017psq\u0000~\u0000\u0018"
+"q\u0000~\u0000\u0017pq\u0000~\u0000\u001bq\u0000~\u0000\u001fq\u0000~\u0000!sq\u0000~\u0000\"t\u00007com.onceas.descriptor.wm.WorkM"
+"anagerShutdownTriggerTypeq\u0000~\u0000&sq\u0000~\u0000\u000fppsq\u0000~\u0000\u0018q\u0000~\u0000\u0017pq\u0000~\u00004q\u0000~\u0000D"
+"q\u0000~\u0000!sq\u0000~\u0000\"t\u0000\u001dwork-manager-shutdown-triggerq\u0000~\u0000Iq\u0000~\u0000!sq\u0000~\u0000\u000fp"
+"psq\u0000~\u0000\u0018q\u0000~\u0000\u0017pq\u0000~\u00004q\u0000~\u0000Dq\u0000~\u0000!sq\u0000~\u0000\"t\u0000\u0011work-manager-beanq\u0000~\u0000Is"
+"r\u0000\"com.sun.msv.grammar.ExpressionPool\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0001L\u0000\bexpTablet"
+"\u0000/Lcom/sun/msv/grammar/ExpressionPool$ClosedHash;xpsr\u0000-com.s"
+"un.msv.grammar.ExpressionPool$ClosedHash\u00d7j\u00d0N\u00ef\u00e8\u00ed\u001c\u0003\u0000\u0003I\u0000\u0005countB"
+"\u0000\rstreamVersionL\u0000\u0006parentt\u0000$Lcom/sun/msv/grammar/ExpressionPo"
+"ol;xp\u0000\u0000\u0000F\u0001pq\u0000~\u0000\u00b7q\u0000~\u0000\u00a3q\u0000~\u0000\u008fq\u0000~\u0000{q\u0000~\u0000iq\u0000~\u0000Wq\u0000~\u0000(q\u0000~\u0000\u00cbq\u0000~\u0000\nq\u0000~\u0000"
+"Jq\u0000~\u0000Mq\u0000~\u0000Kq\u0000~\u0000\rq\u0000~\u0000\u00beq\u0000~\u0000\u00aaq\u0000~\u0000\u0096q\u0000~\u0000\u0082q\u0000~\u0000pq\u0000~\u0000^q\u0000~\u0000/q\u0000~\u0000\u000bq\u0000~\u0000"
+"\u00d2q\u0000~\u0000\u00d6q\u0000~\u0000\u00afq\u0000~\u0000\u009bq\u0000~\u0000\u0087q\u0000~\u0000Oq\u0000~\u0000\u0010q\u0000~\u0000\u009aq\u0000~\u0000\u0086q\u0000~\u0000Nq\u0000~\u0000\u00aeq\u0000~\u0000\u00c3q\u0000~\u0000"
+"\u00c2q\u0000~\u0000\tq\u0000~\u0000\u000eq\u0000~\u0000\u00baq\u0000~\u0000\u00b2q\u0000~\u0000\u00a6q\u0000~\u0000\u009eq\u0000~\u0000\u0092q\u0000~\u0000\u008aq\u0000~\u0000~q\u0000~\u0000vq\u0000~\u0000lq\u0000~\u0000"
+"dq\u0000~\u0000Zq\u0000~\u0000Rq\u0000~\u0000+q\u0000~\u0000\u0015q\u0000~\u0000\u00c6q\u0000~\u0000\u00ceq\u0000~\u0000\u00b9q\u0000~\u0000\u00b1q\u0000~\u0000\u00a5q\u0000~\u0000\u009dq\u0000~\u0000\u0091q\u0000~\u0000"
+"\u0089q\u0000~\u0000}q\u0000~\u0000uq\u0000~\u0000kq\u0000~\u0000cq\u0000~\u0000Yq\u0000~\u0000Qq\u0000~\u0000*q\u0000~\u0000\u0012q\u0000~\u0000\fq\u0000~\u0000Lq\u0000~\u0000\u00c5q\u0000~\u0000"
+"\u00cdx"));
        }
        return new com.sun.msv.verifier.regexp.REDocumentDeclaration(schemaFragment);
    }

    public class Unmarshaller
        extends com.onceas.descriptor.wm.impl.runtime.AbstractUnmarshallingEventHandlerImpl
    {


        public Unmarshaller(com.onceas.descriptor.wm.impl.runtime.UnmarshallingContext context) {
            super(context, "----");
        }

        protected Unmarshaller(com.onceas.descriptor.wm.impl.runtime.UnmarshallingContext context, int startState) {
            this(context);
            state = startState;
        }

        public Object owner() {
            return WorkManagerBeanImpl.this;
        }

        public void enterElement(String ___uri, String ___local, String ___qname, org.xml.sax.Attributes __atts)
            throws org.xml.sax.SAXException
        {
            int attIdx;
            outer:
            while (true) {
                switch (state) {
                    case  0 :
                        if (("work-manager-bean" == ___local)&&("http://www.ios.ac.cn/onceas" == ___uri)) {
                            context.pushAttributes(__atts, false);
                            state = 1;
                            return ;
                        }
                        break;
                    case  3 :
                        revertToParentFromEnterElement(___uri, ___local, ___qname, __atts);
                        return ;
                    case  1 :
                        if (("name" == ___local)&&("http://www.ios.ac.cn/onceas" == ___uri)) {
                            spawnHandlerFromEnterElement((((com.onceas.descriptor.wm.impl.WorkManagerBeanTypeImpl)WorkManagerBeanImpl.this).new Unmarshaller(context)), 2, ___uri, ___local, ___qname, __atts);
                            return ;
                        }
                        if (("name" == ___local)&&("http://www.ios.ac.cn/onceas" == ___uri)) {
                            spawnHandlerFromEnterElement((((com.onceas.descriptor.wm.impl.WorkManagerBeanTypeImpl)WorkManagerBeanImpl.this).new Unmarshaller(context)), 2, ___uri, ___local, ___qname, __atts);
                            return ;
                        }
                        break;
                }
                super.enterElement(___uri, ___local, ___qname, __atts);
                break;
            }
        }

        public void leaveElement(String ___uri, String ___local, String ___qname)
            throws org.xml.sax.SAXException
        {
            int attIdx;
            outer:
            while (true) {
                switch (state) {
                    case  2 :
                        if (("work-manager-bean" == ___local)&&("http://www.ios.ac.cn/onceas" == ___uri)) {
                            context.popAttributes();
                            state = 3;
                            return ;
                        }
                        break;
                    case  3 :
                        revertToParentFromLeaveElement(___uri, ___local, ___qname);
                        return ;
                }
                super.leaveElement(___uri, ___local, ___qname);
                break;
            }
        }

        public void enterAttribute(String ___uri, String ___local, String ___qname)
            throws org.xml.sax.SAXException
        {
            int attIdx;
            outer:
            while (true) {
                switch (state) {
                    case  3 :
                        revertToParentFromEnterAttribute(___uri, ___local, ___qname);
                        return ;
                }
                super.enterAttribute(___uri, ___local, ___qname);
                break;
            }
        }

        public void leaveAttribute(String ___uri, String ___local, String ___qname)
            throws org.xml.sax.SAXException
        {
            int attIdx;
            outer:
            while (true) {
                switch (state) {
                    case  3 :
                        revertToParentFromLeaveAttribute(___uri, ___local, ___qname);
                        return ;
                }
                super.leaveAttribute(___uri, ___local, ___qname);
                break;
            }
        }

        public void handleText(final String value)
            throws org.xml.sax.SAXException
        {
            int attIdx;
            outer:
            while (true) {
                try {
                    switch (state) {
                        case  3 :
                            revertToParentFromText(value);
                            return ;
                    }
                } catch (RuntimeException e) {
                    handleUnexpectedTextException(value, e);
                }
                break;
            }
        }

    }

}
