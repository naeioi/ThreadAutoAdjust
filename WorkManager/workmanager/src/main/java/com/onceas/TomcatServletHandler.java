package com.onceas;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.core.ApplicationHttpRequestAdapter;
//import org.apache.catalina.core.StrutsRequestWrapperAdapter;
import org.apache.jasper.servlet.JspServlet;

import com.onceas.aspect.connector.BaseServletRequestHandler;
import com.onceas.aspect.connector.RequestContext;

public class TomcatServletHandler extends BaseServletRequestHandler {
    private static Logger log = Logger.getLogger(TomcatServletHandler.class
            .toString());

    public RequestContext getCurentRequestContext(final Servlet servlet,
                                                  final HttpServletRequest request) {
        return new RequestContext() {
            public String getApplicationName() {
                return getAppAndModule(servlet, request).get(0);
            }

            public String getModuleName() {
                return getAppAndModule(servlet, request).get(1);
            }
        };
    }

    private List<String> getAppAndModule(final Servlet servlet,
                                         final HttpServletRequest request) {
        String servletName = null;
        String appName = null;
		/*
		 * if(servlet instanceof JspServlet){ String jspFile = (String)
		 * request.getAttribute(org.apache.jasper.Constants.JSP_FILE); if
		 * (jspFile != null) { servletName = jspFile; } else { servletName =
		 * (String)
		 * request.getAttribute(org.apache.jasper.Constants.INC_SERVLET_PATH);
		 * if (servletName != null) { String pathInfo = (String)
		 * request.getAttribute( "javax.servlet.include.path_info"); if
		 * (pathInfo != null) { servletName += pathInfo; } } else { servletName
		 * = ((HttpServletRequest)request).getServletPath(); String pathInfo =
		 * ((HttpServletRequest)request).getPathInfo(); if (pathInfo != null) {
		 * servletName += pathInfo; } } } }
		 */
		/*
		 * 锟睫革拷tomcat源锟诫，RequestFacade锟斤拷锟斤拷锟斤拷getRequest()
		 * 锟斤拷锟斤拷锟斤拷锟斤拷ApplicationHttpRequest锟缴凤拷锟斤拷锟皆憋拷为public
		 */

        if (request instanceof RequestFacade) {
            RequestFacade tomcatRequestFacade = (RequestFacade) request;
            // using reflect access private field
            Field requestField = null;
            try {
                requestField = RequestFacade.class.getDeclaredField("request");
                requestField.setAccessible(true);
            } catch (SecurityException e) {
                log.severe("Cannot get Field[RequestFacade.request] in tomcat using reflection because of Security");
            } catch (NoSuchFieldException e) {
                log.severe("Cannot get Field[RequestFacade.request] in tomcat using reflection because of NoSuchField");
            }

            Request r = null;
            try {
                if (requestField != null) {
                    r = (Request) requestField.get(tomcatRequestFacade);
                }
            } catch (IllegalArgumentException e) {
                log.severe("Cannot get Value of Field[RequestFacade.request]of instance["
                        + tomcatRequestFacade
                        + "] in tomcat using reflection because of IllegalArgument");
            } catch (IllegalAccessException e) {
                log.severe("Cannot get Value of Field[RequestFacade.request]of instance["
                        + tomcatRequestFacade
                        + "] in tomcat using reflection because of IllegalAccess");
            }
            // 可能需要修改servletName
            if (r != null) {
                if (servletName == null) {
                    // servletName = r.getWrapper().getName();
                    servletName = r.getServletPath();
                }
                appName = r.getWrapper().getParent().getName();
            }
        }
        // else if(request instanceof
        // org.apache.catalina.core.ApplicationHttpRequest){// forwadr or
        // include
        else {
            try {
				/*ApplicationHttpRequestAdapter requestAdapter = new ApplicationHttpRequestAdapter(
						request);*/
                // ApplicationHttpRequest applicationHttpRequest =
                // (ApplicationHttpRequest)request;
                if (servletName == null)
                    servletName = request.getServletPath();
                appName = request.getContextPath();
            } catch (Exception e) {
                log.severe("Geting appName and moduleName error using StrutsRequestWrapperAdapter."
                        + "\n" + e);
            }

        }
        List<String> result = new ArrayList<String>(2);
        result.add(0, appName);
        result.add(1, servletName);
        if (Boolean.valueOf(System.getProperty("adapter.log", "false")))
            log.info("RequestContext for the request to[applicationName="
                    + appName + ",moduleName=" + servletName
                    + "] has been getted before obtaining WorkManager.");
        return result;
    }
}
