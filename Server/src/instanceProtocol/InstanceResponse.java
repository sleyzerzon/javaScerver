package instanceProtocol;

public class InstanceResponse {

	InstanceStatus status;
	
	public InstanceResponse() {
		status = InstanceStatus.FAILED;
	}

	public void setStatus(InstanceStatus status) {
		this.status = status;
		
	}

	public byte[] getBytes() {
		// TODO Auto-generated method stub
		return (status.toString()).getBytes();
	}

	public void fromBytes(byte[] data) {
		status = InstanceStatus.valueOf(new String(data));
	}

	public InstanceStatus getStatus() {
		return status;
	}
}
