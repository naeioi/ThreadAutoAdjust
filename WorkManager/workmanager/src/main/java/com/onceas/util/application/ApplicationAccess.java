package com.onceas.util.application;

public final class ApplicationAccess
{
    private static final class ApplicationAccessSingleton
    {
        private static final ApplicationAccess SINGLETON = new ApplicationAccess();

        private ApplicationAccessSingleton()
        {
        }
    }

    private ApplicationAccess()
    {
    }

    public static ApplicationAccess getApplicationAccess()
    {
        return ApplicationAccessSingleton.SINGLETON;
    }

    public ApplicationContextInternal getApplicationContext(String appName)
    {
        if(appName == null)
            return null;
        return GlobalApplictaionContext.getContext(appName);
    }
}
