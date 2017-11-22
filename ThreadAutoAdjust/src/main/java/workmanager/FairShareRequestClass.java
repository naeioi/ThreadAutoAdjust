package workmanager;

public class FairShareRequestClass extends ServiceClassSupport {
    public static final int DEFAULT_FAIR_SHARE = 50;
    private static final double DEFAULT_INCR = 1000D;
    private static final double PERIOD = 2000D;
    private static final double HALF_LIFE = 300000D;
    private static final double W;
    private static final double WC;
    private static final double A = 0.5D;
    private static final double AC = 0.5D;
    private int share;
    private int previouslyCompleted;
    private long previouslyUsed;
    private double smoothedIncr;
    private long initialIncrement;
    private static final boolean DEBUG = DebugWM.debug_FairShareRequest;

    static {
        W = Math.pow(0.5D, PERIOD / HALF_LIFE);
        WC = 1.0D - W;
    }

    FairShareRequestClass(String s) {
        this(s, DEFAULT_FAIR_SHARE);
    }

    FairShareRequestClass(String s, String s1, String s2) {
        this(s + "@" + s1 + "@" + s2, DEFAULT_FAIR_SHARE);
    }

    public FairShareRequestClass(String s, int i) {
        super(s);
        setShare(i);
    }

    private void setShare(int i) {
        if (share == i) {
            return;
        } else {
            share = i;
            smoothedIncr = DEFAULT_INCR / (double) i;
            long l = (long) (smoothedIncr + 1.0D);
            initialIncrement = l;
            setIncrements(l, l);
            return;
        }
    }

    protected long getIncrementForThreadPriorityCalculation() {
        return initialIncrement;
    }

    public void timeElapsed(long l, ServiceClassesStats serviceclassesstats) {
        log(this + "timeElapsed in");
        int i = previouslyCompleted;
        previouslyCompleted = getCompleted();
        int j = previouslyCompleted - i;
        long l1 = previouslyUsed;
        previouslyUsed = getThreadUse();
        if (j == 0) {
            smoothedIncr = W * smoothedIncr + (WC * DEFAULT_INCR)
                    / (double) share;
        } else {
            int k = (int) (previouslyUsed - l1);
            smoothedIncr = A * smoothedIncr + (AC * (double) k)
                    / (double) (j * share);
        }
        long l2 = serviceclassesstats.adjustFairShare(smoothedIncr);
        if (l2 <= 0L)
            l2 = 1L;
        setIncrements(l2, l2);
        log(this + "timeElapsed out");
    }

    // by syk
    public int getFairShare() {
        return share;
    }

    private static void log(String s) {
        if (DEBUG)
            System.out.println("<FairShareRequestClass>" + s);
    }

}
