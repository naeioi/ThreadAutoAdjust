//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.5-b16-fcs 
// 	See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 	Any modifications to this file will be lost upon recompilation of the source schema. 
// 	Generated on: 2009.11.26 �� 04:48:36 CST 
//


package com.onceas.descriptor.wm.impl;

public class MaxThreadsConstraintTypeImpl implements com.onceas.descriptor.wm.MaxThreadsConstraintType, com.sun.xml.bind.JAXBObject, com.onceas.descriptor.wm.impl.runtime.UnmarshallableObject, com.onceas.descriptor.wm.impl.runtime.XMLSerializable, com.onceas.descriptor.wm.impl.runtime.ValidatableObject
{

    protected com.onceas.descriptor.wm.TagStringValueType _Name;
    protected com.onceas.descriptor.wm.TagStringValueType _Count;
    public final static Class version = (JAXBVersion.class);
    private static com.sun.msv.grammar.Grammar schemaFragment;

    private final static Class PRIMARY_INTERFACE_CLASS() {
        return (com.onceas.descriptor.wm.MaxThreadsConstraintType.class);
    }

    public com.onceas.descriptor.wm.TagStringValueType getName() {
        return _Name;
    }

    public void setName(com.onceas.descriptor.wm.TagStringValueType value) {
        _Name = value;
    }

    public com.onceas.descriptor.wm.TagStringValueType getCount() {
        return _Count;
    }

    public void setCount(com.onceas.descriptor.wm.TagStringValueType value) {
        _Count = value;
    }

    public com.onceas.descriptor.wm.impl.runtime.UnmarshallingEventHandler createUnmarshaller(com.onceas.descriptor.wm.impl.runtime.UnmarshallingContext context) {
        return new Unmarshaller(context);
    }

    public void serializeBody(com.onceas.descriptor.wm.impl.runtime.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
        if (_Name instanceof javax.xml.bind.Element) {
            context.childAsBody(((com.sun.xml.bind.JAXBObject) _Name), "Name");
        } else {
            context.startElement("http://www.ios.ac.cn/onceas", "name");
            context.childAsURIs(((com.sun.xml.bind.JAXBObject) _Name), "Name");
            context.endNamespaceDecls();
            context.childAsAttributes(((com.sun.xml.bind.JAXBObject) _Name), "Name");
            context.endAttributes();
            context.childAsBody(((com.sun.xml.bind.JAXBObject) _Name), "Name");
            context.endElement();
        }
        if (_Count instanceof javax.xml.bind.Element) {
            context.childAsBody(((com.sun.xml.bind.JAXBObject) _Count), "Count");
        } else {
            context.startElement("http://www.ios.ac.cn/onceas", "count");
            context.childAsURIs(((com.sun.xml.bind.JAXBObject) _Count), "Count");
            context.endNamespaceDecls();
            context.childAsAttributes(((com.sun.xml.bind.JAXBObject) _Count), "Count");
            context.endAttributes();
            context.childAsBody(((com.sun.xml.bind.JAXBObject) _Count), "Count");
            context.endElement();
        }
    }

    public void serializeAttributes(com.onceas.descriptor.wm.impl.runtime.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
        if (_Name instanceof javax.xml.bind.Element) {
            context.childAsAttributes(((com.sun.xml.bind.JAXBObject) _Name), "Name");
        }
        if (_Count instanceof javax.xml.bind.Element) {
            context.childAsAttributes(((com.sun.xml.bind.JAXBObject) _Count), "Count");
        }
    }

    public void serializeURIs(com.onceas.descriptor.wm.impl.runtime.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
        if (_Name instanceof javax.xml.bind.Element) {
            context.childAsURIs(((com.sun.xml.bind.JAXBObject) _Name), "Name");
        }
        if (_Count instanceof javax.xml.bind.Element) {
            context.childAsURIs(((com.sun.xml.bind.JAXBObject) _Count), "Count");
        }
    }

    public Class getPrimaryInterface() {
        return (com.onceas.descriptor.wm.MaxThreadsConstraintType.class);
    }

    public com.sun.msv.verifier.DocumentDeclaration createRawValidator() {
        if (schemaFragment == null) {
            schemaFragment = com.sun.xml.bind.validator.SchemaDeserializer.deserialize((
 "\u00ac\u00ed\u0000\u0005sr\u0000\u001fcom.sun.msv.grammar.SequenceExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000\u001dcom.su"
+"n.msv.grammar.BinaryExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0002L\u0000\u0004exp1t\u0000 Lcom/sun/msv/gra"
+"mmar/Expression;L\u0000\u0004exp2q\u0000~\u0000\u0002xr\u0000\u001ecom.sun.msv.grammar.Expressi"
+"on\u00f8\u0018\u0082\u00e8N5~O\u0002\u0000\u0002L\u0000\u0013epsilonReducibilityt\u0000\u0013Ljava/lang/Boolean;L\u0000\u000b"
+"expandedExpq\u0000~\u0000\u0002xpppsr\u0000\u001dcom.sun.msv.grammar.ChoiceExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000"
+"\u0001\u0002\u0000\u0000xq\u0000~\u0000\u0001ppsr\u0000\'com.sun.msv.grammar.trex.ElementPattern\u0000\u0000\u0000\u0000\u0000"
+"\u0000\u0000\u0001\u0002\u0000\u0001L\u0000\tnameClasst\u0000\u001fLcom/sun/msv/grammar/NameClass;xr\u0000\u001ecom."
+"sun.msv.grammar.ElementExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0002Z\u0000\u001aignoreUndeclaredAttr"
+"ibutesL\u0000\fcontentModelq\u0000~\u0000\u0002xq\u0000~\u0000\u0003pp\u0000sq\u0000~\u0000\u0006ppsr\u0000 com.sun.msv.g"
+"rammar.OneOrMoreExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000\u001ccom.sun.msv.grammar.UnaryE"
+"xp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0001L\u0000\u0003expq\u0000~\u0000\u0002xq\u0000~\u0000\u0003sr\u0000\u0011java.lang.Boolean\u00cd r\u0080\u00d5\u009c\u00fa\u00ee\u0002"
+"\u0000\u0001Z\u0000\u0005valuexp\u0000psr\u0000 com.sun.msv.grammar.AttributeExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000"
+"\u0002L\u0000\u0003expq\u0000~\u0000\u0002L\u0000\tnameClassq\u0000~\u0000\txq\u0000~\u0000\u0003q\u0000~\u0000\u0011psr\u00002com.sun.msv.gra"
+"mmar.Expression$AnyStringExpression\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xq\u0000~\u0000\u0003sq\u0000~\u0000\u0010\u0001p"
+"sr\u0000 com.sun.msv.grammar.AnyNameClass\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000\u001dcom.sun.m"
+"sv.grammar.NameClass\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xpsr\u00000com.sun.msv.grammar.Exp"
+"ression$EpsilonExpression\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xq\u0000~\u0000\u0003q\u0000~\u0000\u0016psr\u0000#com.sun."
+"msv.grammar.SimpleNameClass\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0002L\u0000\tlocalNamet\u0000\u0012Ljava/l"
+"ang/String;L\u0000\fnamespaceURIq\u0000~\u0000\u001dxq\u0000~\u0000\u0018t\u0000\u001dcom.onceas.descripto"
+"r.wm.Namet\u0000+http://java.sun.com/jaxb/xjc/dummy-elementssq\u0000~\u0000"
+"\bpp\u0000sq\u0000~\u0000\u0000ppsq\u0000~\u0000\bpp\u0000sq\u0000~\u0000\u0006ppsq\u0000~\u0000\rq\u0000~\u0000\u0011psq\u0000~\u0000\u0012q\u0000~\u0000\u0011pq\u0000~\u0000\u0015q\u0000"
+"~\u0000\u0019q\u0000~\u0000\u001bsq\u0000~\u0000\u001ct\u0000+com.onceas.descriptor.wm.TagStringValueType"
+"q\u0000~\u0000 sq\u0000~\u0000\u0006ppsq\u0000~\u0000\u0012q\u0000~\u0000\u0011psr\u0000\u001bcom.sun.msv.grammar.DataExp\u0000\u0000\u0000\u0000"
+"\u0000\u0000\u0000\u0001\u0002\u0000\u0003L\u0000\u0002dtt\u0000\u001fLorg/relaxng/datatype/Datatype;L\u0000\u0006exceptq\u0000~\u0000\u0002"
+"L\u0000\u0004namet\u0000\u001dLcom/sun/msv/util/StringPair;xq\u0000~\u0000\u0003ppsr\u0000\"com.sun.m"
+"sv.datatype.xsd.QnameType\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000*com.sun.msv.datatype"
+".xsd.BuiltinAtomicType\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000%com.sun.msv.datatype.xs"
+"d.ConcreteType\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000\'com.sun.msv.datatype.xsd.XSData"
+"typeImpl\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0003L\u0000\fnamespaceUriq\u0000~\u0000\u001dL\u0000\btypeNameq\u0000~\u0000\u001dL\u0000\nwh"
+"iteSpacet\u0000.Lcom/sun/msv/datatype/xsd/WhiteSpaceProcessor;xpt"
+"\u0000 http://www.w3.org/2001/XMLSchemat\u0000\u0005QNamesr\u00005com.sun.msv.da"
+"tatype.xsd.WhiteSpaceProcessor$Collapse\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000,com.su"
+"n.msv.datatype.xsd.WhiteSpaceProcessor\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xpsr\u00000com.s"
+"un.msv.grammar.Expression$NullSetExpression\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xq\u0000~\u0000\u0003"
+"ppsr\u0000\u001bcom.sun.msv.util.StringPair\u00d0t\u001ejB\u008f\u008d\u00a0\u0002\u0000\u0002L\u0000\tlocalNameq\u0000~\u0000"
+"\u001dL\u0000\fnamespaceURIq\u0000~\u0000\u001dxpq\u0000~\u00006q\u0000~\u00005sq\u0000~\u0000\u001ct\u0000\u0004typet\u0000)http://www."
+"w3.org/2001/XMLSchema-instanceq\u0000~\u0000\u001bsq\u0000~\u0000\u001ct\u0000\u0004namet\u0000\u001bhttp://ww"
+"w.ios.ac.cn/onceassq\u0000~\u0000\u0006ppsq\u0000~\u0000\bpp\u0000sq\u0000~\u0000\u0006ppsq\u0000~\u0000\rq\u0000~\u0000\u0011psq\u0000~\u0000"
+"\u0012q\u0000~\u0000\u0011pq\u0000~\u0000\u0015q\u0000~\u0000\u0019q\u0000~\u0000\u001bsq\u0000~\u0000\u001ct\u0000\u001ecom.onceas.descriptor.wm.Coun"
+"tq\u0000~\u0000 sq\u0000~\u0000\bpp\u0000sq\u0000~\u0000\u0000ppsq\u0000~\u0000\bpp\u0000sq\u0000~\u0000\u0006ppsq\u0000~\u0000\rq\u0000~\u0000\u0011psq\u0000~\u0000\u0012q\u0000"
+"~\u0000\u0011pq\u0000~\u0000\u0015q\u0000~\u0000\u0019q\u0000~\u0000\u001bsq\u0000~\u0000\u001cq\u0000~\u0000(q\u0000~\u0000 sq\u0000~\u0000\u0006ppsq\u0000~\u0000\u0012q\u0000~\u0000\u0011pq\u0000~\u0000."
+"q\u0000~\u0000>q\u0000~\u0000\u001bsq\u0000~\u0000\u001ct\u0000\u0005countq\u0000~\u0000Csr\u0000\"com.sun.msv.grammar.Express"
+"ionPool\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0001L\u0000\bexpTablet\u0000/Lcom/sun/msv/grammar/Express"
+"ionPool$ClosedHash;xpsr\u0000-com.sun.msv.grammar.ExpressionPool$"
+"ClosedHash\u00d7j\u00d0N\u00ef\u00e8\u00ed\u001c\u0003\u0000\u0003I\u0000\u0005countB\u0000\rstreamVersionL\u0000\u0006parentt\u0000$Lco"
+"m/sun/msv/grammar/ExpressionPool;xp\u0000\u0000\u0000\u000f\u0001pq\u0000~\u0000\u0005q\u0000~\u0000\fq\u0000~\u0000$q\u0000~\u0000"
+"Fq\u0000~\u0000Nq\u0000~\u0000\u000fq\u0000~\u0000%q\u0000~\u0000Gq\u0000~\u0000Oq\u0000~\u0000)q\u0000~\u0000Rq\u0000~\u0000\u0007q\u0000~\u0000Dq\u0000~\u0000\"q\u0000~\u0000Lx"));
        }
        return new com.sun.msv.verifier.regexp.REDocumentDeclaration(schemaFragment);
    }

    public class Unmarshaller
        extends com.onceas.descriptor.wm.impl.runtime.AbstractUnmarshallingEventHandlerImpl
    {


        public Unmarshaller(com.onceas.descriptor.wm.impl.runtime.UnmarshallingContext context) {
            super(context, "-------");
        }

        protected Unmarshaller(com.onceas.descriptor.wm.impl.runtime.UnmarshallingContext context, int startState) {
            this(context);
            state = startState;
        }

        public Object owner() {
            return MaxThreadsConstraintTypeImpl.this;
        }

        public void enterElement(String ___uri, String ___local, String ___qname, org.xml.sax.Attributes __atts)
            throws org.xml.sax.SAXException
        {
            int attIdx;
            outer:
            while (true) {
                switch (state) {
                    case  6 :
                        revertToParentFromEnterElement(___uri, ___local, ___qname, __atts);
                        return ;
                    case  0 :
                        if (("name" == ___local)&&("http://www.ios.ac.cn/onceas" == ___uri)) {
                            _Name = ((NameImpl) spawnChildFromEnterElement((NameImpl.class), 3, ___uri, ___local, ___qname, __atts));
                            return ;
                        }
                        if (("name" == ___local)&&("http://www.ios.ac.cn/onceas" == ___uri)) {
                            context.pushAttributes(__atts, true);
                            state = 1;
                            return ;
                        }
                        break;
                    case  3 :
                        if (("count" == ___local)&&("http://www.ios.ac.cn/onceas" == ___uri)) {
                            _Count = ((CountImpl) spawnChildFromEnterElement((CountImpl.class), 6, ___uri, ___local, ___qname, __atts));
                            return ;
                        }
                        if (("count" == ___local)&&("http://www.ios.ac.cn/onceas" == ___uri)) {
                            context.pushAttributes(__atts, true);
                            state = 4;
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
                    case  6 :
                        revertToParentFromLeaveElement(___uri, ___local, ___qname);
                        return ;
                    case  2 :
                        if (("name" == ___local)&&("http://www.ios.ac.cn/onceas" == ___uri)) {
                            context.popAttributes();
                            state = 3;
                            return ;
                        }
                        break;
                    case  5 :
                        if (("count" == ___local)&&("http://www.ios.ac.cn/onceas" == ___uri)) {
                            context.popAttributes();
                            state = 6;
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
                    case  6 :
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
                    case  6 :
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
                        case  1 :
                            _Name = ((TagStringValueTypeImpl) spawnChildFromText((TagStringValueTypeImpl.class), 2, value));
                            return ;
                        case  6 :
                            revertToParentFromText(value);
                            return ;
                        case  4 :
                            _Count = ((TagStringValueTypeImpl) spawnChildFromText((TagStringValueTypeImpl.class), 5, value));
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
