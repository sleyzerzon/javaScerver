package core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HttpRequest {

	private HttpMethod method;
	private String url, version;
	private List<String> headers; //TODO: Map<String, String>
	private final byte[] body;
	private final int bodyLength;

	public HttpRequest(byte[] b) throws IOException {
		ByteArrayInputStream data = new ByteArrayInputStream(b);

		InputStreamReader in = new InputStreamReader(data);
		StringBuilder builder = new StringBuilder();
		String[] requestLine;

		int c;

		//initial line
		//METHOD PATH HTTP/x.x
		while((c = in.read()) != 10) {
			builder.append((char)c);
		}
		if (builder.charAt(builder.length()-1) == '\r') {
			builder.deleteCharAt(builder.length()-1);
		}
		requestLine = builder.toString().split("\\s+");
		method = HttpMethod.valueOf(requestLine[0]);
		url = requestLine[1];
		version = requestLine[2];
		System.out.println("method:"+method);
		System.out.println("resource:"+url);
		System.out.println("version:"+version);


		//headers
		//Header-name: w* value
		ArrayList<String> headersBuilder = new ArrayList<String>();
		System.out.println("\nheaders:");
		String header;
		do {
			builder = new StringBuilder();
			while((c = in.read()) != 10) {
				builder.append((char)c);
			}

			if (builder.charAt(builder.length()-1) == '\r') {
				builder.deleteCharAt(builder.length()-1);
			}
			header = builder.toString();
			if (!header.isEmpty())
				headersBuilder.add(header);
			System.out.println("h:"+header); //TODO: last empty header
		} while (!header.isEmpty());
		//blank line

		headers = Collections.unmodifiableList(headersBuilder);
		//body
		System.out.println("\nbody:");
		body = new byte[8192];
		bodyLength = data.read(body);
		System.out.println(bodyLength);


	}

	public HttpMethod getMethod() {
		return method;
	}

	public String getUrl() {
		return url;
	}

	public String getVersion() {
		return version;
	}

	public List<String> getHeaders() {
		return headers;
	}

	public byte[] getBody() {
		return body;
	}

	public int getBodyLength() {
		return bodyLength;
	}
	
	public static enum HttpMethod {
		GET,
		POST,
		PUT,
		DELETE,
	}

}
