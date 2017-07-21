package workmanager;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.logging.Logger;

public final class RequestManager {
    private static final int INCREMENT_ADVISOR_PERIOD = 2000;
    private static final int INCREMENT_ADVISOR_START_DELAY = 2000;
    private static final int MAX_STANDBY_THREADS = 200;
    private static final int MAX_QUEUE_SIZE = 1000;

    // TODO what is the purpose of creating ACTIVATE_REQUEST and SHUTDOWN_REQUEST?
    private static final WorkAdapter ACTIVATE_REQUEST = new ActivateRequest();
    private static final WorkAdapter SHUTDOWN_REQUEST = new ShutdownRequest();
    private final List allThreads;
    private final Stack idleThreads;
    private final List healthyThreads;
    private final Stack standbyThreadPool;
    private final HashSet hogs;
    public final CalendarQueue queue;
    private long busyPeriodStart;
    private int toDecrement;
    private final ThreadGroup threadGroup;
    private final BitSet recycledIDs;
    public long departures = 0;

    public long mtcDepartures = 0;
    public long rejectedCount = 0;
    public long canceledCount = 0;

    private int queueDepth;
    private final IncrementAdvisor incrementAdvisor;
    private int queueNonEmpty;
    private int maxThreadIdValue;

    static FairShareRequestClass requestClass = new FairShareRequestClass("Default");
    private static Logger log = Logger.getLogger(RequestManager.class.toString());

    public static final class ShutdownError extends Error {

        ShutdownError() {
        }
    }

    private static final class ActivateRequest extends WorkAdapter {

        public void run() {
        }

        private ActivateRequest() {
        }

    }

    private static final class ShutdownRequest extends WorkAdapter {

        public void run() {
            throw new ShutdownError();
        }

        private ShutdownRequest() {
        }

    }

    private static final class Factory {

        static final RequestManager THE_ONE;

        // lazy load
        static {
            THE_ONE = new RequestManager();
            THE_ONE.incrPoolSize(IncrementAdvisor.getMinThreadPoolSize());
        }

        private Factory() {
        }
    }

    private RequestManager() {
        allThreads = new ArrayList();
        idleThreads = new Stack();
        healthyThreads = new ArrayList();
        standbyThreadPool = new Stack();
        hogs = new HashSet();
        queue = new CalendarQueue();
        recycledIDs = new BitSet();
        ThreadGroup threadgroup = null;
        try {
            threadgroup = new ThreadGroup("Pooled Threads");
        } catch (SecurityException securityexception) {
        }
        threadGroup = threadgroup;
        incrementAdvisor = new IncrementAdvisor(requestClass);
        (new Timer(true)).schedule(incrementAdvisor,
                INCREMENT_ADVISOR_START_DELAY, INCREMENT_ADVISOR_PERIOD);
    }

    public static RequestManager getInstance() {
        return Factory.THE_ONE;
    }

    public boolean executeIt(WorkAdapter workAdapter) {
        ExecuteThread executethread = null;
//			int i = 0;
        synchronized (this) {
            if (idleThreads.size() > 0) {
                executethread = (ExecuteThread) idleThreads.pop();
            }
            if (executethread != null) {
                workAdapter.started = true;
            } else {
                addToPriorityQueue(workAdapter);
                return false;
            }
        }
        departures++;
        executethread.notifyRequest(workAdapter);
        return true;
    }

    private void addToPriorityQueue(WorkAdapter workadapter) {
        if (queue.size() < MAX_QUEUE_SIZE) {
            if (queue.size() == 0)
                busyPeriodStart = System.currentTimeMillis();
            queue.add(workadapter, requestClass);
            queueDepth++;
            queueNonEmpty++;
        }
    }

    private boolean createThreadAndExecute(int i, WorkAdapter workadapter) {
        ExecuteThread executethread = createStandbyThread(i);
        executethread.setRequest(workadapter, System.currentTimeMillis());
        executethread.start();
        return true;
    }

    private ExecuteThread createStandbyThread(int i) {
        ExecuteThread executethread = create(i);
        executethread.setStandby(true);
        return executethread;
    }

    private ExecuteThread create(int i) {
        // need change classloader?
        ExecuteThread executethread = new ExecuteThread(i,
                "onceas.kernel.Default (self-tuning)", threadGroup);
        synchronized (allThreads) {
            allThreads.add(executethread);
        }
        return executethread;
    }

    private static void start(ExecuteThread executethread) {
        executethread.start();
        synchronized (executethread) {
            while (!executethread.isStarted())
                try {
                    executethread.wait();
                } catch (InterruptedException interruptedexception) {
                }
        }
    }

    /**
     * Register idle thread and judge if this WorkAdapter has another workmanager to do
     *
     * @param executethread
     * @param workadapter
     * @return
     */
    public boolean registerIdle(ExecuteThread executethread,
                                WorkAdapter workadapter) {
        long l = 0;
        long l1 = 0;
        long l2 = updateStats(workadapter, executethread);
        WorkAdapter workadapter1;
        // Does workCompleted operation need sync ?move workCompleted to
        // synchronized block by syk
        // workCompleted(workadapter);
        synchronized (this) {
            workCompleted(workadapter);
            if (canBeDeactivated(workadapter, executethread)) {
                deactivateThread(executethread);
                // System.out.println(executethread + " be deactivated!");
                return true;
            }
            workadapter1 = getNext();
            if (workadapter1 == null) {

                idleThreads.push(executethread);
                // System.out.println(executethread + " be put idleThreads pool
                // because of no workmanager to do.");
                l1 = getBusyPeriod(l2);
                if (l1 > 0L)
                    l = queue.resetVirtualTime();
            } else {
                workadapter1.started = true;
            }
        }
        if (workadapter1 == null) {
            if (l1 > 0L)
                fireQueueEmptied(l1, l);
            return true;
        } else {
            departures++;
            executethread.setRequest(workadapter1, l2);
            // System.out.println("ExecuteThread[" + executethread +"] continue
            // to execute workmanager["+workadapter1+"].");
            return false;
        }
    }


    /**
     * if this WorkAdapter's max and min constraints are satisfied.
     *
     * @param workadapter
     * @param executethread
     * @return
     */
    private boolean canBeDeactivated(WorkAdapter workadapter,
                                     ExecuteThread executethread) {
        return (toDecrement > 0 || executethread.isStandby());
    }

    private void getEffectiveWorks(WorkAdapter workadapters[]) {
        if (workadapters != null) {
            for (int i = 0; i < workadapters.length; i++) {
                workadapters[i] = workadapters[i].getEffective();
            }
        }
    }

    private void executeWorkList(WorkAdapter aworkadapter[],
                                 ExecuteThread aexecutethread[], int ai[]) {
        int i = 0;
        int j = 0;
        for (int k = 0; k < aworkadapter.length; k++)
            if (i < aexecutethread.length) {
                aexecutethread[i].notifyRequest(aworkadapter[k]);
                i++;
            } else {
                createThreadAndExecute(ai[j], aworkadapter[k]);
                j++;
            }

    }

    private ExecuteThread[] getStandbyThreads(int i) {
        int j = Math.min(i, standbyThreadPool.size());
        ExecuteThread aexecutethread[] = new ExecuteThread[j];
        for (int k = 0; k < aexecutethread.length; k++)
            aexecutethread[k] = (ExecuteThread) standbyThreadPool.pop();

        return aexecutethread;
    }

    private long getBusyPeriod(long l) {
        if (busyPeriodStart == 0L) {
            return 0L;
        } else {
            long l1 = l - busyPeriodStart;
            busyPeriodStart = 0L;
            return l1;
        }
    }

    private static void workCompleted(WorkAdapter workadapter) {
        if (workadapter != null) {
            workadapter.prepareForReuse();
        }
    }

    private WorkAdapter getNext() {
        WorkAdapter workadapter = getFromPriorityQueue();
        if (workadapter != null && workadapter != SHUTDOWN_REQUEST)
            queueDepth--;
        if (workadapter == null) {
            return null;
        }
        return workadapter.getEffective();
    }

    private WorkAdapter getFromPriorityQueue(WorkAdapter workadapter) {
        WorkAdapter workadapter1 = (WorkAdapter) queue.pop(workadapter,
                workadapter.requestClass);
        // here workadapter1 == workadapter == MaxConstraintProxy
        // after executing the following statement workadapter1 may be
        // min.ProxyEntry
        workadapter1 = workadapter1.getEffective();
        if (workadapter1 != null)
            return workadapter1;
        else
            return getFromPriorityQueue();
    }

    private WorkAdapter getFromPriorityQueue() {
        WorkAdapter workadapter;
        while ((workadapter = (WorkAdapter) queue.pop()) != null) {
            WorkAdapter workadapter1 = workadapter.getEffective();
            // ���� max.getNext()��maxqueue��ȡ������Ҫ��� priority
            // queue�е�workadapter.started ???? by syk
            if (workadapter1 != null)
                return workadapter1;
        }
        return null;
    }

    /**
     * Remove this ExecuteThread form threadPool and put it to StandbyPool
     *
     * @param executethread
     * @return
     */
    private boolean deactivateThread(ExecuteThread executethread) {
        if (!executethread.isStandby()) {
            toDecrement--;
            healthyThreads.remove(executethread);
        }
        addToStandbyPool(executethread);
        return true;
    }

    private long updateStats(WorkAdapter workadapter,
                             ExecuteThread executethread) {
        if (executethread.isHog())
            reclaimHogger(executethread);
        if (workadapter != null) {
            ServiceClassStatsSupport serviceclassstatssupport = (ServiceClassStatsSupport) workadapter.requestClass;
            return updateRequestClass(serviceclassstatssupport, executethread);
        } else {
            return System.currentTimeMillis();
        }
    }

    static long updateRequestClass(
            ServiceClassStatsSupport serviceclassstatssupport,
            ExecuteThread executethread) {
        long l = System.currentTimeMillis();
        if (serviceclassstatssupport != null) {
            long l1 = l - executethread.timeStamp;
            synchronized (RequestManager.class) {
                serviceclassstatssupport.completedCount++;
                serviceclassstatssupport.totalThreadUse += l1;
                serviceclassstatssupport.threadUseSquares += l1 * l1;
            }
        }
        return l;
    }

    private synchronized void reclaimHogger(ExecuteThread executethread) {
        if (hogs.remove(executethread))
            healthyThreads.add(executethread);
        executethread.setHog(false);
    }

    private void fireQueueEmptied(long l, long l1) {
        synchronized (requestClass) {
            float f = (float) l1 / (float) l;
            requestClass.queueEmptied(l, f);

        }
    }

    private synchronized void decrPoolSize(int i) {
        for (; i > 0; i--) {
            if (idleThreads.isEmpty()) {
                toDecrement = i;
                return;
            }
            ExecuteThread executethread = (ExecuteThread) idleThreads.pop();
            healthyThreads.remove(executethread);
            addToStandbyPool(executethread);
        }

    }

    /**
     * TODO Why we need StandbyPool
     *
     * @param executethread
     */
    private synchronized void addToStandbyPool(ExecuteThread executethread) {
        if (standbyThreadPool.size() > MAX_STANDBY_THREADS) {
            recycledIDs.set(executethread.id);
//			WorkManagerLogger.logDecreasingThreads(1);
            executethread.notifyRequest(SHUTDOWN_REQUEST);
            synchronized (allThreads) {
                allThreads.remove(executethread);
            }
        } else {
            executethread.setStandby(true);
            standbyThreadPool.add(executethread);
        }
    }

    /**
     * TODO when we get thread from StandbyPool
     *
     * @return
     */
    private synchronized ExecuteThread getFromStandbyPool() {
        if (standbyThreadPool.size() == 0) {
            return null;
        } else {
            ExecuteThread executethread = (ExecuteThread) standbyThreadPool
                    .pop();
            executethread.setStandby(false);
            return executethread;
        }
    }

    private static void activateStandbyThread(ExecuteThread executethread) {
        executethread.notifyRequest(ACTIVATE_REQUEST);
    }

    public int purgeHogs(int i) {
        long l = System.currentTimeMillis();
        synchronized (this) {
            long l1 = l - (long) i;
            int j = healthyThreads.size() - 1;
            for (int k = j; k >= 0; k--) {
                ExecuteThread executethread = (ExecuteThread) healthyThreads
                        .get(k);
                WorkAdapter workadapter = executethread.getCurrentWork();
                if (workadapter == null)
                    continue;
                long l2 = l1 - executethread.getTimeStamp();
                if (l2 <= 0L)
                    continue;
                if (k != j)
                    healthyThreads.set(k, healthyThreads.get(j));
                healthyThreads.remove(j--);
                executethread.setHog(true);
                hogs.add(executethread);
            }

        }
        return healthyThreads.size();
    }

    private int threadID() {
        int i = recycledIDs.nextSetBit(0);
        if (i < 0) {
            return maxThreadIdValue++;
        } else {
            recycledIDs.clear(i);
            return i;
        }
    }

    private int[] threadID(int i) {
        if (i == 0)
            return null;
        int ai[] = new int[i];
        for (int j = 0; j < i; j++)
            ai[j] = threadID();

        return ai;
    }

    public void incrPoolSize(int i) {
        if (i < 0) {
            decrPoolSize(-i);
            return;
        }
        if (healthyThreads.size() + hogs.size() >= IncrementAdvisor
                .getMaxThreadPoolSize())
            return;
        int j = i;
        synchronized (this) {
            do {
                if (j <= 0)
                    break;
                ExecuteThread executethread1 = getFromStandbyPool();
                if (executethread1 == null)
                    break;
                healthyThreads.add(executethread1);
                activateStandbyThread(executethread1);
                j--;
            } while (true);
        }
        // if there is not enough standby threads, then cread the left
        for (j = getActualIncrementCount(j); j > 0; j--) {
            ExecuteThread executethread;
            synchronized (this) {
                executethread = create(threadID());
                healthyThreads.add(executethread);
            }
            start(executethread);
        }

    }

    private int getActualIncrementCount(int i) {
        return Math.min(i, IncrementAdvisor.getMaxThreadPoolSize()
                - getExecuteThreadCount());
    }

    public int getQueueDepth() {
        return queueDepth;
    }

    public int getTotalRequestsCount() {
        return getQueueDepth() + (healthyThreads.size() - idleThreads.size());
    }

    public int getExecuteThreadCount() {
        return allThreads.size();
    }

    public int getActiveExecuteThreadCount() {
        return healthyThreads.size();
    }

    public long getQueueDepartures() {
        return departures;
    }

    public int getIdleThreadCount() {
        return idleThreads.size();
    }

    public int getStandbyCount() {
        return standbyThreadPool.size();
    }

    public int getHogSize() {
        return hogs.size();
    }

    public ExecuteThread[] getExecuteThreads() {
        synchronized (allThreads) {
            ExecuteThread aexecutethread[] = new ExecuteThread[allThreads
                    .size()];
            allThreads.toArray(aexecutethread);
            return aexecutethread;
        }
    }

    public double getThroughput() {
        return incrementAdvisor.getThroughput();
    }

    synchronized boolean isQueueNonEmpty() {
        boolean flag = queueNonEmpty > 0;
        queueNonEmpty = 0;
        return flag;
    }

    private static boolean debugEnabled() {
        return DebugWM.debug_RequestManager;
    }

    private static void log(String s) {
        // SelfTuningDebugLogger.debug("<RequestManager>" + s);
        System.out.println("<RequestManager>" + s);
    }
}
