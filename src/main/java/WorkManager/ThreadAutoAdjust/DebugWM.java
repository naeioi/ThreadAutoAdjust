package WorkManager.ThreadAutoAdjust;

public class DebugWM {
	public static int debug_Thread = 0; // ThreadPool������Ϣ���ƿ���

	public static int debug_LowWM = 0; // �ͼ�WorkManagerʵ�ֿ���

	public static int debug_entrypoint = 0;// WorkManager �����Ϣ����

	public static int debug_MaxThread = 0;

	public static boolean debug_MinThread = false;

	public static boolean debug_IncrementAdvisor = false;

	public static boolean debug_FairShareRequest = false;

	public static boolean debug_IncreAD = false;

	public static boolean debug;

	public static boolean debug_RequestManager = false;

	public static boolean debug_WMColl = false;

	public static boolean debug_WMService = false;

	public static boolean debug_ThreadPriorty = false; // ���ڴ�ӡ�̵߳����ȼ���Ϣ\\

	public static boolean debug_ThreadPriortyManager = false;

	public static boolean debug_WAmin;

	public static boolean debug_StuckThread = false;

	public static boolean debug_ResponseTimeRequest = false;

	public static boolean debug_Overload = false;

	public static boolean debug_LowMemory = false;

	public static boolean debug_CalendarQueue = false;

	public static boolean debug_EJB = false;

	public DebugWM() {

	}

	public static boolean isEnabled() {
		return debug;

	}

}
