package com.onceas;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;

import com.onceas.aspect.connector.tomcat.BaseWebContainerDeployInstrument;
import com.onceas.util.application.ApplicationContextInternal;
import com.onceas.util.application.GlobalApplictaionContext;
import com.onceas.work.WorkManagerCollection;


public class TomcatWebContainerDeployInstrument extends BaseWebContainerDeployInstrument {
    public void tomcatDeployWeb(Context context){
        if(context instanceof StandardContext){
            //appName 设置为 context container name;
            String appName = ((org.apache.catalina.Container)context).getName();
            String basePath = getBasePath((StandardContext) context);

            URL url = null;
            try {
                url = new File(basePath).toURL();
                doAfterDeploy(url, appName);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    public void afterUndeployPoint(String appName){
        //stripping starting slash of appName
        appName = stripStartSlash(appName);

        ApplicationContextInternal appCtxInt = GlobalApplictaionContext.removeContext(appName);
        if(appCtxInt != null){
            WorkManagerCollection wmc = appCtxInt.getWorkManagerCollection();
            wmc.destroy();
        }
    }

    protected String getBasePath(StandardContext context) {
        String docBase = null;
        Container container = context;
        while (container != null) {
            if (container instanceof Host)
                break;
            container = container.getParent();
        }
        File file = new File(context.getDocBase());
        if (!file.isAbsolute()) {
            if (container == null) {
                docBase = (new File(engineBase(context), context.getDocBase())).getPath();
            } else {
                // Use the "appBase" property of this container
                String appBase = ((Host) container).getAppBase();
                file = new File(appBase);
                if (!file.isAbsolute())
                    file = new File(engineBase(context), appBase);
                docBase = (new File(file, context.getDocBase())).getPath();
            }
        } else {
            docBase = file.getPath();
        }
        return docBase;
    }

    /**
     * Return a File object representing the base directory for the
     * entire servlet container (i.e. the Engine container if present).
     */
    protected File engineBase(StandardContext context) {
        String base=System.getProperty("catalina.base");
        if( base == null ) {
            StandardEngine eng=(StandardEngine)context.getParent().getParent();
            base=eng.getBaseDir();
        }
        return (new File(base));
    }
}
