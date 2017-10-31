package workmanager;

public class ServiceClassesStats {

	public ServiceClassesStats() {
	}

	public void reset() {
		fairShareCorrection = fairShareSum != 0.0D ? (1000D * (double) fairShareCount)
				/ fairShareSum
				: 1000D;
		responseTimeCorrection = responseTimeSum != 0.0D ? (100D * (double) responseTimeCount)
				/ responseTimeSum
				: 100D;
		fairShareCount = 0;
		fairShareSum = 0.0D;
		responseTimeCount = 0;
		responseTimeSum = 0.0D;
	}

	public long adjustFairShare(double d) {
		fairShareCount++;
		fairShareSum += d;
		return (long) (d * fairShareCorrection);
	}

	public long adjustResponseTime(double d) {
		responseTimeCount++;
		responseTimeSum += d;
		return (long) (d * responseTimeCorrection);
	}

	public static final double TARGET_RESPONSE_TIME_INCR = 100D;

	public static final double TARGET_FAIR_SHARE_INCR = 1000D;

	int fairShareCount;

	double fairShareSum;

	double fairShareCorrection;

	int responseTimeCount;

	double responseTimeSum;

	double responseTimeCorrection;
}
