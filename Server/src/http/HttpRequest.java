package http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

	private HttpMethod method;
	private String url, version;
	private Map<String, String> headers; 
	private final byte[] body;
	private final int bodyLength;

	public HttpRequest(byte[] b) throws IOException, NotHttpException {
		ByteArrayInputStream data = new ByteArrayInputStream(b);

		InputStreamReader in = new InputStreamReader(data);
		StringBuilder builder = new StringBuilder();
		String[] requestLine;

		int c;

		//initial line
		//METHOD PATH HTTP/x.x
		while( in.ready() && (c = in.read()) != 10) {
			builder.append((char)c);
			//TODO: fails on nonsense http
		}
		if (builder.charAt(builder.length()-1) == '\r') {
			builder.deleteCharAt(builder.length()-1);
		}
		requestLine = builder.toString().split("\\s+");
		if (requestLine.length != 3)
			throw new NotHttpException();
		
		method = HttpMethod.valueOf(requestLine[0]);
		url = requestLine[1];
		version = requestLine[2];
		System.out.println("method:"+method);
		System.out.println("resource:"+url);
		System.out.println("version:"+version);


		//headers
		//Header-name: w* value
		HashMap<String, String> headersBuilder = new HashMap<String, String>();
		System.out.println("\nheaders:");
		String[] header;
		do {
			builder = new StringBuilder();
			while((c = in.read()) != 10) {
				builder.append((char)c);
			}

			if (builder.charAt(builder.length()-1) == '\r') {
				builder.deleteCharAt(builder.length()-1);
			}
			header = builder.toString().split(":\\s+");
			if (header.length == 2){
				headersBuilder.put(header[0], header[1]);
				System.out.println(header[0]+": "+header[1]);
			}
		} while (header.length == 2);
		//blank line

		headers = Collections.unmodifiableMap(headersBuilder);
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

	public Map<String, String> getHeaders() {
		return headers;
	}

	public byte[] getBody() {
		return body;
	}

	public int getBodyLength() {
		return bodyLength;
	}
	
	public String getFragment() {
		return url.substring(url.lastIndexOf('#'));
	}
	
	public Map<String, String> getQueries() {
		//scheme://username:password@domain:port/path?query_string#fragment_id
		HashMap<String, String> queries = new HashMap<String, String>();
		String[] split = url.split("\\?");
		if (split.length!=2)
			return queries;
		String queryString = split[1];
		for (String query : queryString.split("&")) {
			String[] querySplit = query.split("=");
			if (querySplit.length != 2) 
				continue;
			queries.put(querySplit[0], querySplit[1]);
		}
			
		return queries;
	}
}
