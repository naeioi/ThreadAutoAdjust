package WorkManager.ThreadAutoAdjust;

public class ServiceClassStatsSupport {

	public ServiceClassStatsSupport(String s) {
		name = s;
	}

	public final void workCompleted(long l, long l1) {
		completedCount++;
		totalThreadUse += l;
		totalCPU += l1;
		cpuSquares += l1 * l1;
		threadUseSquares += l * l;
	}

	public String getName() {
		return name;
	}

	public final int getCompleted() {
		return completedCount;
	}

	public final long getThreadUse() {
		return totalThreadUse;
	}

	public final long getThreadUseSquares() {
		return threadUseSquares;
	}

	public final long getCPU() {
		return totalCPU;
	}

	public final long getCPUSquares() {
		return cpuSquares;
	}

	private long getThreadAvg() {
		return div(totalThreadUse, completedCount);
	}

	private double getThreadStdev() {
		return stdev(totalThreadUse, threadUseSquares, completedCount);
	}

	public final long getCPUAvg() {
		return div(totalCPU, completedCount);
	}

	public final double getCPUStdev() {
		return stdev(totalCPU, cpuSquares, completedCount);
	}

	static long div(long l, long l1) {
		return l != 0L ? l1 != 0L ? (l + l1 / 2L) / l1 : 0x7fffffffffffffffL
				: 0L;
	}

	static double stdev(long l, long l1, int i) {
		double d = (double) l / (double) i;
		return Math.sqrt((double) l1 / (double) i - d * d);
	}

	public final String toString() {
		return name + ": completed=" + completedCount + ", avg time="
				+ getThreadAvg() + "+/-" + getThreadStdev() + ", avg cpu="
				+ getCPUAvg() + "+/-" + getCPUStdev();
	}

	private final String name;

	int completedCount;

	long totalThreadUse;

	long threadUseSquares;

	private long totalCPU;

	long cpuSquares;
}
