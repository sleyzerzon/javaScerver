package core;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpResponse {

	private HttpStatus status;
	private String version = "HTTP/1.0";
	private Map<String, String> headers;
	private byte[] body;

	
	
	public HttpResponse() {
		status = HttpStatus.NOT_FOUND;
		headers = new HashMap<String, String>();
		headers.put("Server", "Jack");
		headers.put("Date", new Date().toString());
		body = new byte[0];
	}
	
	public byte[] getBytes() {
		StringBuilder response = new StringBuilder();
			
			//response line
			response.append(version + " " + status + "\n");			
			
			//headers
			for ( Entry<String, String> e : headers.entrySet()) {
				response.append(e.getKey()+": "+e.getValue()+"\n");
			}
			
			response.append("\r\n\r\n");
			byte[] head = response.toString().getBytes();
			byte[] result = new byte[head.length + body.length];
			System.arraycopy(head, 0, result, 0, head.length);
			
			//body
			System.arraycopy(body, 0, result, head.length, body.length);
		return result;
	}
	


	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getVersion() {
		return version;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
		headers.put("Content-Length", String.valueOf(body.length));
	}
	
	/**
	 * Convenience method to set the response body to the given string's bytes
	 * @param message
	 */
	public void setMessage(String message) {
		setBody(message.getBytes());
		headers.put("Content-Type", "text/html");
	} 
	
	public void addHeader(String key, String value) {
		headers.put(key, value);
	}
	
	public static enum HttpStatus {
		OK, 
		NOT_FOUND; 
		
		@Override
		public String toString() {
			switch (this) {
			case OK:
				return "200 OK";		

			default:
				return "404 NOT FOUND";
			}
		}

	}
	//TODO: only allow correct headers
	
}
