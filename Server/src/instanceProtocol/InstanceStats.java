package instanceProtocol;

public class InstanceStats {

	private int avgLatency;
	private int requestsPerTime;
	private int maxLatency;
	
	public static InstanceStats newInitiate() {
		InstanceStats newInitiate = new InstanceStats();			
		newInitiate.avgLatency = 0;
		newInitiate.requestsPerTime = 0;
		newInitiate.maxLatency = 0;
		return newInitiate;
	}

	public int getAvgLatency() {
		return avgLatency;
	}

	public void setAvgLatency(int avgLatency) {
		this.avgLatency = avgLatency;
	}

	public int getRequestsPerTime() {
		return requestsPerTime;
	}

	public void setRequestsPerTime(int requestsPerTime) {
		this.requestsPerTime = requestsPerTime;
	}

	public int getMaxLatency() {
		return maxLatency;
	}

	public void setMaxLatency(int maxLatency) {
		this.maxLatency = maxLatency;
	}

}
