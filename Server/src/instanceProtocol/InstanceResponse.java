package instanceProtocol;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class InstanceResponse {
	/*
	 * format:
	 * [status]\n
	 * 
	 */
	
	private InstanceStatus status;
	private byte[] body;
	
	public InstanceResponse() {
		status = InstanceStatus.FAILED;
	}

	public void setStatus(InstanceStatus status) {
		this.status = status;
	}


	public InstanceStatus getStatus() {
		return status;
	}


	
	public byte[] getBytes() {
		byte[] head = (status.toString()+"\n").getBytes();
		byte[] all = new byte[head.length + body.length];
		System.arraycopy(head, 0, all, 0, head.length);
		System.arraycopy(body, 0, all, head.length, body.length);
		return all;
	}
	
	public static InstanceResponse fromBytes(byte[] data) {
		InstanceResponse response = new InstanceResponse();
		
		ByteArrayInputStream  in = new ByteArrayInputStream(data);
		InputStreamReader readMethod = new InputStreamReader(in);
		char c;
		int offset = 1;
		try {
			StringBuilder builder = new StringBuilder();
			while(readMethod.ready() && (c = (char)readMethod.read()) != '\n') {
				builder.append((char)c);
				offset++;
			}
			response.status = InstanceStatus.valueOf(builder.toString());
			response.body = new byte[data.length-offset];
			System.arraycopy(data, offset, response.body, 0, response.body.length);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	public byte[] getBody() {
		// TODO: give a copy, rather than the ptr
		return body;
	}
	
	public void setBody(byte[] b) {
		body = b;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof InstanceResponse))
			return false;
		InstanceResponse r = (InstanceResponse)obj;
		return r.status == status && Arrays.equals(r.body, body);
	}
}
