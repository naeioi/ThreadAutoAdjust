//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.5-b16-fcs 
// 	See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 	Any modifications to this file will be lost upon recompilation of the source schema. 
// 	Generated on: 2009.11.26 �� 04:48:36 CST 
//


package com.onceas.descriptor.wm.impl;

public class CapacityImpl
    extends com.onceas.descriptor.wm.impl.CapacityTypeImpl
    implements com.onceas.descriptor.wm.Capacity, com.sun.xml.bind.RIElement, com.sun.xml.bind.JAXBObject, com.onceas.descriptor.wm.impl.runtime.UnmarshallableObject, com.onceas.descriptor.wm.impl.runtime.XMLSerializable, com.onceas.descriptor.wm.impl.runtime.ValidatableObject
{

    public final static Class version = (JAXBVersion.class);
    private static com.sun.msv.grammar.Grammar schemaFragment;

    private final static Class PRIMARY_INTERFACE_CLASS() {
        return (com.onceas.descriptor.wm.Capacity.class);
    }

    public String ____jaxb_ri____getNamespaceURI() {
        return "http://www.ios.ac.cn/onceas";
    }

    public String ____jaxb_ri____getLocalName() {
        return "capacity";
    }

    public com.onceas.descriptor.wm.impl.runtime.UnmarshallingEventHandler createUnmarshaller(com.onceas.descriptor.wm.impl.runtime.UnmarshallingContext context) {
        return new Unmarshaller(context);
    }

    public void serializeBody(com.onceas.descriptor.wm.impl.runtime.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
        context.startElement("http://www.ios.ac.cn/onceas", "capacity");
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
        return (com.onceas.descriptor.wm.Capacity.class);
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
+"\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0002L\u0000\u0004exp1q\u0000~\u0000\u0003L\u0000\u0004exp2q\u0000~\u0000\u0003xq\u0000~\u0000\u0004ppsq\u0000~\u0000\u0007ppsr\u0000\u001dcom.s"
+"un.msv.grammar.ChoiceExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xq\u0000~\u0000\bppsq\u0000~\u0000\u0000pp\u0000sq\u0000~\u0000\u000bpp"
+"sr\u0000 com.sun.msv.grammar.OneOrMoreExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000\u001ccom.sun.m"
+"sv.grammar.UnaryExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0001L\u0000\u0003expq\u0000~\u0000\u0003xq\u0000~\u0000\u0004sr\u0000\u0011java.lang"
+".Boolean\u00cd r\u0080\u00d5\u009c\u00fa\u00ee\u0002\u0000\u0001Z\u0000\u0005valuexp\u0000psr\u0000 com.sun.msv.grammar.Attri"
+"buteExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0002L\u0000\u0003expq\u0000~\u0000\u0003L\u0000\tnameClassq\u0000~\u0000\u0001xq\u0000~\u0000\u0004q\u0000~\u0000\u0013psr"
+"\u00002com.sun.msv.grammar.Expression$AnyStringExpression\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001"
+"\u0002\u0000\u0000xq\u0000~\u0000\u0004sq\u0000~\u0000\u0012\u0001psr\u0000 com.sun.msv.grammar.AnyNameClass\u0000\u0000\u0000\u0000\u0000\u0000\u0000"
+"\u0001\u0002\u0000\u0000xr\u0000\u001dcom.sun.msv.grammar.NameClass\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xpsr\u00000com.su"
+"n.msv.grammar.Expression$EpsilonExpression\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xq\u0000~\u0000\u0004q"
+"\u0000~\u0000\u0018psr\u0000#com.sun.msv.grammar.SimpleNameClass\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0002L\u0000\tlo"
+"calNamet\u0000\u0012Ljava/lang/String;L\u0000\fnamespaceURIq\u0000~\u0000\u001fxq\u0000~\u0000\u001at\u0000\u001dcom"
+".onceas.descriptor.wm.Namet\u0000+http://java.sun.com/jaxb/xjc/du"
+"mmy-elementssq\u0000~\u0000\u0000pp\u0000sq\u0000~\u0000\u0007ppsq\u0000~\u0000\u0000pp\u0000sq\u0000~\u0000\u000bppsq\u0000~\u0000\u000fq\u0000~\u0000\u0013psq"
+"\u0000~\u0000\u0014q\u0000~\u0000\u0013pq\u0000~\u0000\u0017q\u0000~\u0000\u001bq\u0000~\u0000\u001dsq\u0000~\u0000\u001et\u0000+com.onceas.descriptor.wm.T"
+"agStringValueTypeq\u0000~\u0000\"sq\u0000~\u0000\u000bppsq\u0000~\u0000\u0014q\u0000~\u0000\u0013psr\u0000\u001bcom.sun.msv.gr"
+"ammar.DataExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0003L\u0000\u0002dtt\u0000\u001fLorg/relaxng/datatype/Dataty"
+"pe;L\u0000\u0006exceptq\u0000~\u0000\u0003L\u0000\u0004namet\u0000\u001dLcom/sun/msv/util/StringPair;xq\u0000~"
+"\u0000\u0004ppsr\u0000\"com.sun.msv.datatype.xsd.QnameType\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000*com"
+".sun.msv.datatype.xsd.BuiltinAtomicType\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000%com.su"
+"n.msv.datatype.xsd.ConcreteType\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000\'com.sun.msv.da"
+"tatype.xsd.XSDatatypeImpl\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0003L\u0000\fnamespaceUriq\u0000~\u0000\u001fL\u0000\bt"
+"ypeNameq\u0000~\u0000\u001fL\u0000\nwhiteSpacet\u0000.Lcom/sun/msv/datatype/xsd/WhiteS"
+"paceProcessor;xpt\u0000 http://www.w3.org/2001/XMLSchemat\u0000\u0005QNames"
+"r\u00005com.sun.msv.datatype.xsd.WhiteSpaceProcessor$Collapse\u0000\u0000\u0000\u0000"
+"\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000,com.sun.msv.datatype.xsd.WhiteSpaceProcessor\u0000\u0000\u0000\u0000\u0000"
+"\u0000\u0000\u0001\u0002\u0000\u0000xpsr\u00000com.sun.msv.grammar.Expression$NullSetExpression"
+"\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xq\u0000~\u0000\u0004ppsr\u0000\u001bcom.sun.msv.util.StringPair\u00d0t\u001ejB\u008f\u008d\u00a0\u0002\u0000"
+"\u0002L\u0000\tlocalNameq\u0000~\u0000\u001fL\u0000\fnamespaceURIq\u0000~\u0000\u001fxpq\u0000~\u00008q\u0000~\u00007sq\u0000~\u0000\u001et\u0000\u0004t"
+"ypet\u0000)http://www.w3.org/2001/XMLSchema-instanceq\u0000~\u0000\u001dsq\u0000~\u0000\u001et\u0000"
+"\u0004namet\u0000\u001bhttp://www.ios.ac.cn/onceassq\u0000~\u0000\u000bppsq\u0000~\u0000\u0000pp\u0000sq\u0000~\u0000\u000bpp"
+"sq\u0000~\u0000\u000fq\u0000~\u0000\u0013psq\u0000~\u0000\u0014q\u0000~\u0000\u0013pq\u0000~\u0000\u0017q\u0000~\u0000\u001bq\u0000~\u0000\u001dsq\u0000~\u0000\u001et\u0000\u001ecom.onceas.d"
+"escriptor.wm.Countq\u0000~\u0000\"sq\u0000~\u0000\u0000pp\u0000sq\u0000~\u0000\u0007ppsq\u0000~\u0000\u0000pp\u0000sq\u0000~\u0000\u000bppsq\u0000"
+"~\u0000\u000fq\u0000~\u0000\u0013psq\u0000~\u0000\u0014q\u0000~\u0000\u0013pq\u0000~\u0000\u0017q\u0000~\u0000\u001bq\u0000~\u0000\u001dsq\u0000~\u0000\u001eq\u0000~\u0000*q\u0000~\u0000\"sq\u0000~\u0000\u000bpp"
+"sq\u0000~\u0000\u0014q\u0000~\u0000\u0013pq\u0000~\u00000q\u0000~\u0000@q\u0000~\u0000\u001dsq\u0000~\u0000\u001et\u0000\u0005countq\u0000~\u0000Esq\u0000~\u0000\u000bppsq\u0000~\u0000\u0014"
+"q\u0000~\u0000\u0013pq\u0000~\u00000q\u0000~\u0000@q\u0000~\u0000\u001dsq\u0000~\u0000\u001et\u0000\bcapacityq\u0000~\u0000Esr\u0000\"com.sun.msv.g"
+"rammar.ExpressionPool\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0001L\u0000\bexpTablet\u0000/Lcom/sun/msv/g"
+"rammar/ExpressionPool$ClosedHash;xpsr\u0000-com.sun.msv.grammar.E"
+"xpressionPool$ClosedHash\u00d7j\u00d0N\u00ef\u00e8\u00ed\u001c\u0003\u0000\u0003I\u0000\u0005countB\u0000\rstreamVersionL"
+"\u0000\u0006parentt\u0000$Lcom/sun/msv/grammar/ExpressionPool;xp\u0000\u0000\u0000\u0011\u0001pq\u0000~\u0000\n"
+"q\u0000~\u0000\u000eq\u0000~\u0000&q\u0000~\u0000Hq\u0000~\u0000Pq\u0000~\u0000\u0011q\u0000~\u0000\'q\u0000~\u0000Iq\u0000~\u0000Qq\u0000~\u0000+q\u0000~\u0000Tq\u0000~\u0000Xq\u0000~\u0000\t"
+"q\u0000~\u0000\fq\u0000~\u0000Fq\u0000~\u0000$q\u0000~\u0000Nx"));
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
            return CapacityImpl.this;
        }

        public void enterElement(String ___uri, String ___local, String ___qname, org.xml.sax.Attributes __atts)
            throws org.xml.sax.SAXException
        {
            int attIdx;
            outer:
            while (true) {
                switch (state) {
                    case  3 :
                        revertToParentFromEnterElement(___uri, ___local, ___qname, __atts);
                        return ;
                    case  1 :
                        if (("name" == ___local)&&("http://www.ios.ac.cn/onceas" == ___uri)) {
                            spawnHandlerFromEnterElement((((com.onceas.descriptor.wm.impl.CapacityTypeImpl)CapacityImpl.this).new Unmarshaller(context)), 2, ___uri, ___local, ___qname, __atts);
                            return ;
                        }
                        if (("name" == ___local)&&("http://www.ios.ac.cn/onceas" == ___uri)) {
                            spawnHandlerFromEnterElement((((com.onceas.descriptor.wm.impl.CapacityTypeImpl)CapacityImpl.this).new Unmarshaller(context)), 2, ___uri, ___local, ___qname, __atts);
                            return ;
                        }
                        break;
                    case  0 :
                        if (("capacity" == ___local)&&("http://www.ios.ac.cn/onceas" == ___uri)) {
                            context.pushAttributes(__atts, false);
                            state = 1;
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
                    case  3 :
                        revertToParentFromLeaveElement(___uri, ___local, ___qname);
                        return ;
                    case  2 :
                        if (("capacity" == ___local)&&("http://www.ios.ac.cn/onceas" == ___uri)) {
                            context.popAttributes();
                            state = 3;
                            return ;
                        }
                        break;
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