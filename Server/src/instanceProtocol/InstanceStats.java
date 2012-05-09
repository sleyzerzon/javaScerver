package instanceProtocol;

import java.io.Serializable;

public class InstanceStats implements Serializable {

	private long avgLatency;
	private long requestsPerTime;
	private long maxLatency;
	
	public static InstanceStats newInitiate() {
		InstanceStats newInitiate = new InstanceStats();			
		newInitiate.avgLatency = 0;
		newInitiate.requestsPerTime = 0;
		newInitiate.maxLatency = 0;
		return newInitiate;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("request count: %d, average latency: %d, max latency %d", requestsPerTime, avgLatency, maxLatency);
	}
	
	public long getAvgLatency() {
		return avgLatency;
	}

	public void setAvgLatency(long avgLatency) {
		this.avgLatency = avgLatency;
	}

	public long getRequestsPerTime() {
		return requestsPerTime;
	}

	public void setRequestsPerTime(long requestsPerTime) {
		this.requestsPerTime = requestsPerTime;
	}

	public long getMaxLatency() {
		return maxLatency;
	}

	public void setMaxLatency(long maxLatency) {
		this.maxLatency = maxLatency;
	}

}
