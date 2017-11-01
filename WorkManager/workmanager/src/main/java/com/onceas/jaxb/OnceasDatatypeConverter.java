package com.onceas.jaxb;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;

import javax.xml.bind.DatatypeConverterInterface;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import com.sun.xml.bind.DatatypeConverterImpl;

public class OnceasDatatypeConverter implements DatatypeConverterInterface {
	DatatypeConverterImpl datatypeConverterImpl;
	private static final char[] hexCode="0123456789ABCDEF".toCharArray();
	
	@Override
	public String parseString(String lexicalXSDString) {
		return lexicalXSDString;
	}

	@Override
	public BigInteger parseInteger(String lexicalXSDInteger) {
		return DatatypeConverterImpl._parseInteger(lexicalXSDInteger);
	}

	@Override
	public int parseInt(String lexicalXSDInt) {
		return DatatypeConverterImpl._parseInt(lexicalXSDInt);
	}

	@Override
	public long parseLong(String lexicalXSDLong) {
		return DatatypeConverterImpl._parseLong(lexicalXSDLong);
	}

	@Override
	public short parseShort(String lexicalXSDShort) {
		return DatatypeConverterImpl._parseShort(lexicalXSDShort);
	}

	@Override
	public BigDecimal parseDecimal(String lexicalXSDDecimal) {
		return DatatypeConverterImpl._parseDecimal(lexicalXSDDecimal);
	}

	@Override
	public float parseFloat(String lexicalXSDFloat) {
		return DatatypeConverterImpl._parseFloat(lexicalXSDFloat);
	}

	@Override
	public double parseDouble(String lexicalXSDDouble) {
		return DatatypeConverterImpl._parseDouble(lexicalXSDDouble);
	}

	@Override
	public boolean parseBoolean(String lexicalXSDBoolean) {
		return DatatypeConverterImpl._parseBoolean(lexicalXSDBoolean);
	}

	@Override
	public byte parseByte(String lexicalXSDByte) {
		return DatatypeConverterImpl._parseByte(lexicalXSDByte);
	}

	@Override
	public QName parseQName(String lexicalXSDQName, NamespaceContext nsc) {
		return DatatypeConverterImpl._parseQName(lexicalXSDQName, nsc);
	}

	@Override
	public Calendar parseDateTime(String lexicalXSDDateTime) {
		return DatatypeConverterImpl._parseDateTime(lexicalXSDDateTime);
	}

	@Override
	public byte[] parseBase64Binary(String lexicalXSDBase64Binary) {
		return DatatypeConverterImpl._parseBase64Binary(lexicalXSDBase64Binary);
	}

	@Override
	public byte[] parseHexBinary(String lexicalXSDHexBinary) {
		int len = lexicalXSDHexBinary.length();

	    if (len % 2 != 0) return null;

	    byte[] out = new byte[len / 2];

	    for (int i = 0; i < len; i += 2) {
	      int h = hexToBin(lexicalXSDHexBinary.charAt(i));
	      int l = hexToBin(lexicalXSDHexBinary.charAt(i + 1));
	      if ((h == -1) || (l == -1)) {
	        return null;
	      }
	      out[(i / 2)] = (byte)(h * 16 + l);
	    }

	    return out;
	}
	
	private static int hexToBin(char ch) {
	    if (('0' <= ch) && (ch <= '9')) return ch - '0';
	    if (('A' <= ch) && (ch <= 'F')) return ch - 'A' + 10;
	    if (('a' <= ch) && (ch <= 'f')) return ch - 'a' + 10;
	    return -1;
	  }

	@Override
	public long parseUnsignedInt(String lexicalXSDUnsignedInt) {
		return DatatypeConverterImpl._parseLong(lexicalXSDUnsignedInt);
	}

	@Override
	public int parseUnsignedShort(String lexicalXSDUnsignedShort) {
		return DatatypeConverterImpl._parseInt(lexicalXSDUnsignedShort);
	}

	@Override
	public Calendar parseTime(String lexicalXSDTime) {
		return DatatypeConverterImpl._parseDateTime(lexicalXSDTime);
	}

	@Override
	public Calendar parseDate(String lexicalXSDDate) {
		return DatatypeConverterImpl._parseDateTime(lexicalXSDDate);
	}

	@Override
	public String parseAnySimpleType(String lexicalXSDAnySimpleType) {
		return lexicalXSDAnySimpleType;
	}

	@Override
	public String printString(String val) {
		return val;
	}

	@Override
	public String printInteger(BigInteger val) {
		return DatatypeConverterImpl._printInteger(val);
	}

	@Override
	public String printInt(int val) {
		return DatatypeConverterImpl._printInt(val);
	}

	@Override
	public String printLong(long val) {
		return DatatypeConverterImpl._printLong(val);
	}

	@Override
	public String printShort(short val) {
		return DatatypeConverterImpl._printShort(val);
	}

	@Override
	public String printDecimal(BigDecimal val) {
		return DatatypeConverterImpl._printDecimal(val);
	}

	@Override
	public String printFloat(float val) {
		return DatatypeConverterImpl._printFloat(val);
	}

	@Override
	public String printDouble(double val) {
		return DatatypeConverterImpl._printDouble(val);
	}

	@Override
	public String printBoolean(boolean val) {
		return DatatypeConverterImpl._printBoolean(val);
	}

	@Override
	public String printByte(byte val) {
		return DatatypeConverterImpl._printByte(val);
	}

	@Override
	public String printDateTime(Calendar val) {
		return DatatypeConverterImpl._printDateTime(val);
	}

	@Override
	public String printBase64Binary(byte[] val) {
		return DatatypeConverterImpl._printBase64Binary(val);
	}

	@Override
	public String printHexBinary(byte[] val) {
		StringBuilder r = new StringBuilder(val.length * 2);
	    for (byte b : val) {
	      r.append(hexCode[(b >> 4 & 0xF)]);
	      r.append(hexCode[(b & 0xF)]);
	    }
	    return r.toString();
	}

	@Override
	public String printUnsignedInt(long val) {
		return DatatypeConverterImpl._printLong(val);
	}

	@Override
	public String printUnsignedShort(int val) {
		return String.valueOf(val);
	}

	@Override
	public String printTime(Calendar val) {
		return DatatypeConverterImpl._printDateTime(val);
	}

	@Override
	public String printDate(Calendar val) {
		return DatatypeConverterImpl._printDate(val);
	}

	@Override
	public String printAnySimpleType(String val) {
		return val;
	}

	@Override
	public String printQName(QName arg0, NamespaceContext arg1) {
		return DatatypeConverterImpl._printQName(arg0, arg1);
	}

}
