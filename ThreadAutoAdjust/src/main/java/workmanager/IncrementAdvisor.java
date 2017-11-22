package workmanager;

import java.util.ConcurrentModificationException;
import java.util.Properties;
import java.util.Random;
import java.util.TimerTask;

public final class IncrementAdvisor extends TimerTask {
    private static final class SmoothedStats {
        static long freshnessHalfLife = 30000L;
        private long updateTimestamp = 0;
        private double freshness = 1;
        private double n;
        private double sum;
        private double squaresSum;
        private static final double NORM_CUMULATIVE[] = {
                0.0013498980320000001, 0.0018658133009999999, 0.0025551303310000001, 0.0034669738040000002, 0.0046611880249999996,
                0.0062096653260000001, 0.0081975359259999995, 0.010724110021000001, 0.013903447512999999, 0.017864420562000001,
                0.022750131948000001, 0.028716559815000001, 0.035930319112000002, 0.044565462761999998, 0.054799291698999997,
                0.066807201270000005, 0.080756659236000006, 0.096800484586000005, 0.11506967022300001, 0.13566606094799999,
                0.158655253932, 0.184060125347, 0.21185539858300001, 0.24196365222399999, 0.27425311775099998,
                0.30853753872599998, 0.34457825839, 0.38208857781099997, 0.42074029056200002, 0.46017216272299999,
                0.5D,
                0.53982783727700001, 0.57925970943899996, 0.61791142218899997, 0.65542174161, 0.69146246127400002,
                0.72574688224899997, 0.75803634777600004, 0.78814460141700005, 0.815939874653, 0.84134474606800003,
                0.86433393905199996, 0.88493032977700004, 0.90319951541400001, 0.91924334076400005, 0.93319279873000005,
                0.945200708301, 0.95543453723799998, 0.96406968088800005, 0.97128344018500001, 0.97724986805199998,
                0.98213557943800001, 0.98609655248700001, 0.98927588997899996, 0.99180246407399997, 0.99379033467400002,
                0.99533881197499996, 0.99653302619600004, 0.99744486966900003, 0.998134186699, 0.99865010196799997
        };

        private static double normCumulative(double d) {
            if (d < -3.0) {
                d = -3.0;
            }
            if (d > 3.0) {
                d = 3.0;
            }
            return NORM_CUMULATIVE[(int) (10.0 * (d + 3.0))];
        }

        void add(double d) {
            long now = System.currentTimeMillis();
            long delta = now - updateTimestamp;
            // double d1 = Math.pow(0.5, (now - updateTimestamp) / HORIZON);
            double d1 = 1 - 1 / HORIZON;
            n = d1 * n + 1.0;
            sum = d1 * sum + d;
            squaresSum = d1 * squaresSum + d * d;
            freshness = d1 * freshness * Math.pow(0.5, delta / freshnessHalfLife) + 1 - d1;
            updateTimestamp = now;
        }

        double getAvg() {
            return sum / n;
        }

        boolean exceedsZ(double d, double d1) {
            double d2 = sum - n * d;
            return n > 1.0 && d2 * d2 > (n * squaresSum - sum * sum) * d1 * d1;
        }

        double zScore(double d) {
            double d1 = sum / n;
            return (d - d1) / Math.sqrt(squaresSum / n - d1 * d1);
        }

        double pLessThan(SmoothedStats smoothedstats) {
            double d = smoothedstats.sum / smoothedstats.n - sum / n;
            double d1 = (((squaresSum - (sum * sum) / n) + smoothedstats.squaresSum) - (smoothedstats.sum * smoothedstats.sum)
                    / smoothedstats.n)
                    / (n + smoothedstats.n);
            double d2 = d1 * (1.0 / n + 1.0 / smoothedstats.n);
            return d2 != 0.0 ? normCumulative(d
                    / Math.sqrt(d2)) : d <= 0.0 ? 0 : 1;
        }

        double getFreshness() {
            return Math.pow(0.5, (System.currentTimeMillis() - updateTimestamp) / freshnessHalfLife) * freshness;
        }

        SmoothedStats(double d) {
            n = 1.0;
            sum = d;
            squaresSum = d * d;
            updateTimestamp = System.currentTimeMillis();
        }
    }
    private static final int DEFAULT_MIN_POOL_SIZE = 10;

    private static final int DEFAULT_MAX_POOL_SIZE = 1000;

    private static final int MIN_POOL_SIZE = Integer.parseInt(System.getProperty("workmanager.minPoolSize", String.valueOf(DEFAULT_MIN_POOL_SIZE)));

    // private static final int MIN_POOL_SIZE = 200;
    private static final int MAX_POOL_SIZE = Integer.parseInt(System.getProperty("workmanager.maxPoolSize", String.valueOf(DEFAULT_MAX_POOL_SIZE)));

    // private static final int MAX_POOL_SIZE = 1000;
    public static final double PERIOD = 2000D;

    private static final double NOVELTY_ATTRACTION = 0.5D;

    private static final Random RANDOM = new Random(123L);

    private static double HORIZON = 30000D;

    private static final int Y_THRESHOLD_FOR_CPU_INTENSIVE_LOAD = 15000;

    private static final int HIGH_THROUGHPUT_THRESHOLD = 20000;

    private SmoothedStats throughput[];

    private int zeroCompletedDuration;

    private int previousCompleted;

    private long previousThreadTime;

    private long timeStamp;

    private final ServiceClassesStats stats = new ServiceClassesStats();

    private int attemptToIncrementCount;

    /* the max completed jobs per second.
     * What's the difference from max throughput?
     */
    private int maxY;

    private double work_completed_per_second;

    // private double previousY;

    private double maxThroughput;

    private double lastThroughput;

    private int previousSampleIndex;

    private int nextSampleIndex;

    private static Properties properties;

    FairShareRequestClass requestClass;

    public IncrementAdvisor(FairShareRequestClass requestClass) {
        throughput = new SmoothedStats[0];
        timeStamp = System.currentTimeMillis();
        maxY = 0;
        this.requestClass = requestClass;
    }

    /**
     * @param i current healthy threads count
     * @param d 每秒完成的任务数
     */
    private void addSample(int i, double d) {
        if (i == 0)
            return;
        if (debugEnabled())
            log("adding sample. n=" + i + ", " + d);
        SmoothedStats smoothedstats = throughput[i];
        if (smoothedstats == null) {
            smoothedstats = throughput[i] = new SmoothedStats(d);
        } else {
            if (smoothedstats.exceedsZ(d, 3D)) {
                HORIZON = (HORIZON + 1.0D) / 2D;
                if (debugEnabled())
                    log("outlier z= " + smoothedstats.zScore(d) + ", y=" + d
                            + ", avg=" + smoothedstats.getAvg()
                            + " halve horizon to " + HORIZON);
            } else {
                HORIZON++;
            }
            smoothedstats.add(d);
        }
        lastThroughput = smoothedstats.getAvg();
    }

    private void showThroughput() {
        if (debugEnabled()) {
            String s = "";
            String s1 = "";
            for (int i = 0; i < throughput.length; i++) {
                SmoothedStats smoothedstats = throughput[i];
                s1 = s1
                        + (smoothedstats != null ? s
                        + (int) (smoothedstats.getAvg() + NOVELTY_ATTRACTION)
                        : s + "N/A");
                s = "\t";
            }

            log(s1);
        }
    }

    public void run() {
        if (DebugWM.debug_FairShareRequest) {
            System.out.println("IncrementAdvisor:   run()");
        }
        boolean flag = debugEnabled();
        RequestManager requestmanager = RequestManager.getInstance();
//		activeRequestClassesInOverload();
        int work_completed_total;
        long thread_usetime_total;

        work_completed_total = 0;
        thread_usetime_total = 0L;
        ServiceClassStatsSupport serviceclassstatssupport = (ServiceClassStatsSupport) requestClass;
        if (serviceclassstatssupport != null) {
            work_completed_total += serviceclassstatssupport.getCompleted();
            thread_usetime_total += serviceclassstatssupport.getThreadUse();
        }

        long now = System.currentTimeMillis();
        long time_interval = now - timeStamp;
        int work_completed_period = work_completed_total - previousCompleted;
        int thread_usetime_period = (int) (thread_usetime_total - previousThreadTime);

        timeStamp = now;
        previousCompleted = work_completed_total;
        previousThreadTime = thread_usetime_total;
        // previousY = work_completed_per_second;

        if (work_completed_period == 0) zeroCompletedDuration++;
        else zeroCompletedDuration = 0;
        if (time_interval == 0L) return;

        // 降低hog的标准，加快调整速度
        int hogthreads_threshold = work_completed_period != 0 && 7 * thread_usetime_period >= 4000 * work_completed_period ? div(7 * thread_usetime_period, work_completed_period) : 4000;
        int healthy_threads_length = requestmanager.purgeHogs(hogthreads_threshold);

        if (flag)
            log("active threads: "
                    + requestmanager.getActiveExecuteThreadCount()
                    + ", standby: " + requestmanager.getStandbyCount()
                    + ", idle: " + requestmanager.getIdleThreadCount()
                    + ", hogs: " + requestmanager.getHogSize());

        //only the time used by thread during sampling period above half of the interval time(1s) will cause the param real_parallelism greater than zero
         /* For perfect parallel, thread_usetime == time_interval * parallelism,
          * When high parallelism hurts performance, thread_usetime > time_interval * parallelism
          */
        int real_parallelism = (int) (((long) thread_usetime_period + time_interval / 2L) / time_interval);
        // y : 每秒完成的任务数
        work_completed_per_second = (1000D * (double) work_completed_period) / (double) time_interval;

        if (flag)
            log("y,dCompleted,elapsedTime,threadUse,n,usedThreads,queuelength=\t"
                    + (int) (work_completed_per_second + 0.5D)
                    + "\t"
                    + work_completed_period
                    + "\t"
                    + time_interval
                    + "\t"
                    + thread_usetime_period
                    + "\t"
                    + healthy_threads_length + "\t" + real_parallelism + "\t" + requestmanager.queue.size());

        int queue_length = requestmanager.queue.size();

        if (debugEnabled()) {
            System.out.println("****************");
            System.out.println("");
            System.out.println("k1 = " + real_parallelism + "  j1 = " + healthy_threads_length);
            System.out.println("k = " + thread_usetime_period + "  l2 = " + time_interval);
        }
        if (queue_length > 0 && work_completed_period > 0 && real_parallelism < healthy_threads_length) {
            // thread utilization in last interval is lower than the predicted
            // utilization
            if (debugEnabled()) {
                System.out.println("Suspicious: " + work_completed_per_second + "\t" + real_parallelism);
                System.out.println("");
                System.out.println("****************");
            }
            return;
        }

        if (healthy_threads_length >= throughput.length) {
            SmoothedStats asmoothedstats[] = throughput;
            throughput = new SmoothedStats[healthy_threads_length + 1];
            System.arraycopy(asmoothedstats, 0, throughput, 0,
                    asmoothedstats.length);
        }

        stats.reset();
        try {
            /* TODO: Intention of timeElapsed
             * I guess it is use to adjust priority of requestClass
             * Because we have only one requestClass here this is actually of no use
             */
            requestClass.timeElapsed(time_interval, stats);
        } catch (ConcurrentModificationException _e) {
        }

        int min_threadpool_size = getMinThreadPoolSize();
        if (healthy_threads_length < min_threadpool_size) {
            // 如果健康线程个数小于最小线程池大小，则补充相应的差额
            if (debugEnabled()) {
                System.out.println("如果健康线程个数小于最小线程池大小，则补充相应的差额");
                System.out.println("");
                System.out.println("****************");
            }
            //  如果健康线程个数小于最小线程池大小，则逐一递增
            requestmanager.incrPoolSize(1);
            return;
        }

        if (queue_length > 0 && zeroCompletedDuration > 2) {
            // 等待队列不为空，且连续两个Interval没有任务完成，即大于4s
            requestmanager.incrPoolSize(1);
            if (debugEnabled()) {
                System.out.println("等待队列不为空，且连续两个Interval没有任务完成，增加线程池容量 +1");
                System.out.println("");
                System.out.println("****************");
            }
            return;
        }

        /* TODO: what's the intention?
         * priority: low
         */
        if (maxY == 0 && work_completed_per_second > Y_THRESHOLD_FOR_CPU_INTENSIVE_LOAD) {
            // 第一次有任务到来，每秒完成的任务数超过Y_THRESHOLD_FOR_CPU_INTENSIVE_LOAD，则减少线程数至CPU数量
            reset(requestmanager, healthy_threads_length);
            if (debugEnabled()) {
                System.out
                        .println("第一次有任务到来，每秒完成的任务数超过Y_THRESHOLD_FOR_CPU_INTENSIVE_LOAD，则减少线程数至CPU数量");
                System.out.println("");
                System.out.println("****************");
            }
            return;
        }

        /* detect bottleneck caused by high parallelism */
        if (real_parallelism >= healthy_threads_length)
            addSample(healthy_threads_length, work_completed_per_second);
        initMaxValues(requestmanager.getTotalRequestsCount(), work_completed_per_second);
        initIndexes(healthy_threads_length);
        /* TODO: what's decrAttraction? */
        double d = RANDOM.nextFloat();
        double d1 = getDecrAttraction(healthy_threads_length, min_threadpool_size);
        double d2 = getIncrAttraction(healthy_threads_length);
        if (/* debugEnabled() */ true) {
            System.out.println("attraction decr=" + d1 + ", incr=" + d2
                    + ", rand=" + d);
        }

        if (d1 > d2 || (d1 == d2 && RANDOM.nextFloat() > 0.5)) {
            if (d1 > d) {
                if (/*debugEnabled()*/true) {
                    System.out.println("incrPoolSize = "
                            + (previousSampleIndex - healthy_threads_length));
                    System.out.println("");
                    System.out.println("****************");
                }
                // requestmanager.incrPoolSize(previousSampleIndex - healthy_threads_length);
                requestmanager.incrPoolSize(-2);
            }
            attemptToIncrementCount = 0;
            return;
        }

        if (queue_length > 0 && healthy_threads_length < getMaxThreadPoolSize()
                && (attemptToIncrementCount >= 3 || d2 > d)) {
            attemptToIncrementCount = 0;
            int k2 = 1;
            if (!mustIncrementByOne(d2, d1, healthy_threads_length, nextSampleIndex))
                k2 = Math.max(nextSampleIndex - healthy_threads_length,
                        getIncrementInterval((int) work_completed_per_second));

            requestmanager.incrPoolSize(k2);
            if (/* debugEnabled() */ true) {
                System.out.println("nextSampleIndex - j1 = "
                        + (nextSampleIndex - healthy_threads_length));
                System.out.println("getIncrementInterval = "
                        + (getIncrementInterval((int) work_completed_per_second)));
                System.out
                        .println("attemptToIncrementCount >= 3 || d2 > d Growing with attraction= "
                                + d2 + ", increment interval=" + k2);
                System.out.println("");
                System.out.println("****************");
            }
            return;
        } else {
            attemptToIncrementCount++;
            return;
        }

    }

    private boolean mustIncrementByOne(double d, double d1, int i, int j) {
        if (j > i + 1
                && d1 == SmoothedStats.NORM_CUMULATIVE[0]
                && d == SmoothedStats.NORM_CUMULATIVE[0]) {
            SmoothedStats asmoothedstats[] = throughput;
            throughput = new SmoothedStats[i];
            System.arraycopy(asmoothedstats, 0, throughput, 0,
                    throughput.length);
            return true;
        } else {
            return false;
        }
    }

    /**
     * If the availableProcessors is not richful, then we will decrPoolSize
     *
     * @param requestmanager
     * @param i
     */
    private static void reset(RequestManager requestmanager, int i) {
        int j = Runtime.getRuntime().availableProcessors();
        if (i <= j)
            return;
        if (debugEnabled())
            log("resetting thread count to cpucount=" + j);
        // decrPoolSize
        requestmanager.incrPoolSize(j - i);
    }

    /**
     * @param total_reqs current total request count
     * @param completed_last_sec completed jobs per second
     */
    //maxThroughout in consideration of history data while maxY just reflect current value
    private void initMaxValues(int total_reqs, double completed_last_sec) {
        if (total_reqs == 0) {
            if (debugEnabled())
                log("RESETTING maxThroughput and maxY");
            maxThroughput = maxY = 0;
            lastThroughput = 0.0D;
        } else {
            maxThroughput = Math.max(maxThroughput, lastThroughput);
            if (debugEnabled())
                log("maxThroughput=" + maxThroughput + ",lastThroughput="
                        + lastThroughput);
            maxY = (int) Math.max(maxY, completed_last_sec);
            if (debugEnabled())
                log("maxY=" + maxY + ", y=" + completed_last_sec);
        }
    }

    /**
     * @param i parallelism
     */
    private void initIndexes(int i) {
        previousSampleIndex = 0;
        nextSampleIndex = 0;
        int j = i - 1;
        long now = System.currentTimeMillis();
        /* find last throughput[j] != null for j < i */
        do {
            if (j < 0)
                break;
            if (throughput[j] != null) {
                if(now - throughput[j].updateTimestamp > 10000)
                    throughput[j] = null;
                else {
                    previousSampleIndex = j;
                    break;
                }
            }
            j--;
        } while (true);
        /* find first throughput[j] != null for j > i */
        j = i + 1;
        do {
            if (j >= throughput.length)
                break;
            if (throughput[j] != null) {
                if(now - throughput[j].updateTimestamp > 10000)
                    throughput[j] = null;
                else {
                    nextSampleIndex = j;
                    break;
                }
            }
            j++;
        } while (true);
    }

    private int getIncrementInterval(int i) {
        return 1;
//        if (maxY == 0 || i == 0)
//            return 1;
//        int j = maxY / i;
//        if (j <= 1)
//            return 1;
//        // 最大不能超过20
//        int k = Math.min(20, 3 * j + 1);
//        if (debugEnabled())
//            log("Calculated increment interval=" + k);
//        return k;
    }

//	private void activeRequestClassesInOverload() {
//		RequestManager requestmanager = RequestManager.getInstance();
//		int i = ServerWorkManagerImpl.SHARED_OVERLOAD_MANAGER.getCapacity();
//		if (requestmanager.getTotalRequestsCount() >= i)
//			requestmanager.activeRequestClassesInOverload(i);
//		else
//			requestmanager.resetActiveRequestClasses();
//	}

    private static int div(int i, int j) {
        return (i + j / 2) / j;
    }

    final private Random prand = new Random();
    private double getIncrAttraction(int i) {
        if (/* debugEnabled() */ true)
            log("[getIncrAttraction] next=" + nextSampleIndex + ", current="
                    + i);
        if (nextSampleIndex == 0)
            return NOVELTY_ATTRACTION;
        SmoothedStats smoothedstats = throughput[nextSampleIndex];
        if (smoothedstats == null || throughput[i] == null /* || throughput[i].getFreshness() < prand.nextDouble() */)
            return NOVELTY_ATTRACTION;
        else
            return throughput[i].pLessThan(smoothedstats);
    }

    /**
     * @param i health threads count
     * @param j min thread pool size
     * @return
     */
    private double getDecrAttraction(int i, int j) {
        if (debugEnabled())
            log("[getDecrAttraction] previous=" + previousSampleIndex
                    + ", current=" + i);
        if (i <= j)
            return 0.0D;
        if(previousSampleIndex == 0)
            return NOVELTY_ATTRACTION;
        SmoothedStats smoothedstats = throughput[previousSampleIndex];
        if (smoothedstats == null || throughput[i] == null /* || throughput[i].getFreshness() < prand.nextDouble() */)
            return NOVELTY_ATTRACTION;
        double d = throughput[i].pLessThan(smoothedstats);
        // HIGH_THROUGHPUT_THRESHOLD = 20000
        if (d > NOVELTY_ATTRACTION
                && lastThroughput < HIGH_THROUGHPUT_THRESHOLD
                && notEnoughVariationFromMax()) {
            if (debugEnabled())
                log("decrAttraction is " + d + " but limiting it to 0.5");
            return NOVELTY_ATTRACTION;
        } else {
            return d;
        }
    }

    // 与 max 相比的变化是否足够大
    private boolean notEnoughVariationFromMax() {
        if (lastThroughput >= maxThroughput)
            return true;
        double d = maxThroughput - lastThroughput;
        return d * 100D <= 20D * maxThroughput;
    }

    public static int getMinThreadPoolSize() {
        if (MIN_POOL_SIZE > 0)
            return MIN_POOL_SIZE;
        return 200;
    }

    public static int getMaxThreadPoolSize() {
        if (MAX_POOL_SIZE > 0)
            return MAX_POOL_SIZE;
        return 1000;
    }

    public double getThroughput() {
        return work_completed_per_second;
    }

    private static boolean debugEnabled() {
        return false;
    }

    private static void log(String s) {
        if (DebugWM.debug_IncreAD)
            System.out.println("<IncrAdvisor>" + s);
    }

}