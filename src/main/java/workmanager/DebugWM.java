package workmanager;

public final class DebugWM {
    public static int debug_Thread = 0; // ThreadPool运行信息控制开关
    public static int debug_LowWM = 0; // 低级WorkManager实现开关
    public static int debug_entrypoint = 0;// workmanager 入口信息控制
    public static int debug_MaxThread = 0;
    public static boolean debug_MinThread = false;
    public static boolean debug_IncrementAdvisor = false;
    public static boolean debug_FairShareRequest = false;
    public static boolean debug_IncreAD = false;
    public static boolean debug;
    public static boolean debug_RequestManager = false;
    public static boolean debug_WMColl = false;
    public static boolean debug_WMService = false;
    public static boolean debug_ThreadPriorty = false; // 用于打印线程的优先级信息\\
    public static boolean debug_ThreadPriortyManager = false;
    public static boolean debug_WAmin;
    public static boolean debug_StuckThread = false;
    public static boolean debug_ResponseTimeRequest = false;
    public static boolean debug_Overload = false;
    public static boolean debug_LowMemory = false;
    public static boolean debug_CalendarQueue = false;
    public static boolean debug_EJB = false;

    private DebugWM() {

    }
}
