package com.onceas.descriptor.wm.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.onceas.descriptor.wm.Wm;
import com.onceas.descriptor.wm.WmDescriptor;



public class WmDescriptorImpl implements WmDescriptor {

	public void exportXml(String fileName, Wm onceasWeb) {
		try {
			JAXBContext jc = JAXBContext.newInstance( "com.onceas.descriptor.wm" );
			Marshaller m = jc.createMarshaller();
			m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			m.marshal( onceasWeb, new PrintStream( fileName ) );
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch( IOException ioe ) {
			 ioe.printStackTrace();
		}
		return;
	}

	public Wm importXml(String fileName) {
		Wm wmRoot = null;
		try {
			 JAXBContext jc = JAXBContext.newInstance( "com.onceas.descriptor.wm" );
			 Unmarshaller u = jc.createUnmarshaller();
			 // donot validating, because of errors existing when configging onceas-servlet element in onceas-web.xml 
			 u.setValidating(false);
			 wmRoot = (Wm) u.unmarshal( getInputStream( fileName ) );
		 } catch( JAXBException je ) {
			 je.printStackTrace();
		 } 
		return wmRoot;
	}
	
	private InputStream getInputStream(String name){
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(name);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return fis;
	}
/*	private InputStream xml2Stream( String name )
	{
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(name);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		int i = 1;
		StringBuffer sb = new StringBuffer();
		while(i>=0)
		{
			byte[] buff = new byte[1024];
			try {
				i = fis.read(buff);
				String temp = "";
				if(i>0){
					for(int n=0;n<buff.length&&buff[n]!=0;n++)
						temp += (char) buff[n];
					sb.append(temp);
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
		DescriptorAdaptorImpl wa = new DescriptorAdaptorImpl();
		wa.getRules().add(new CommentRule());
		wa.getRules().add(new DoctypeRule());
		
		return new ByteArrayInputStream( wa.applyRules(sb.toString()).getBytes() );
	}*/

}
