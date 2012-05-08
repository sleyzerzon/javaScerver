package instanceProtocol;

public class InstanceResponse {
	/*
	 * format:
	 * [status]\n
	 * 
	 */
	
	private InstanceStatus status;
	
	public InstanceResponse() {
		// TODO Auto-generated constructor stub
	}

	public void setStatus(InstanceStatus status) {
		this.status = status;
	}
	
	public byte[] getBytes() {
		return status.toString().getBytes();
	}
	
	public static InstanceResponse fromBytes(byte[] d) {
		InstanceResponse response = new InstanceResponse();
		response.status = InstanceStatus.valueOf(new String(d));
		return response;
	}
}
