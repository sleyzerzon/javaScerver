package Controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import http.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpStatus;


public class SimpleController implements Controller {

	final Set<HttpMethod> methods;
	final String resourcePath;
	private long requestCount;
	private ArrayList<Long> latencies;
	
	public SimpleController() {
		methods = new HashSet<HttpMethod>();
		methods.add(HttpMethod.GET);
		latencies = new ArrayList<Long>();
		requestCount = 0;
		resourcePath = "/";
	}
	@Override
	public String getResourcePath() {
		return resourcePath;
	}

	@Override
	public Set<HttpMethod> getMethods() {
		return methods;
	}

	@Override
	public HttpResponse handleRequest(HttpRequest r) {
		requestCount++;
		long start = System.nanoTime();
		HttpResponse response = new HttpResponse();
		response.setStatus(HttpStatus.OK);
		response.addHeader("Connection", "close");
		response.setMessage( 
				"<!DOCTYPE html>" +
						"<html>" +
						"<head>" +
						"<title>jphelan</title>" +
						"</head>" +
						"<body>" +
						"I am currently an undergraduate cs major here at Carnegie Mellon. " +
						"Right now I am looking at my text editor, and soon enough there will be more things on this page" +
						"</body>" +
						"</html>" +
				"\r\n\r\n");
		latencies.add(System.nanoTime() - start);
		return response;		
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Controller) {
			return ((Controller) o).getResourcePath() == getResourcePath() &&
					((Controller) o).getMethods().equals(getMethods());
		}
		else return false;
	}
	@Override
	public Collection<? extends Long> getLatencies() {
		return latencies;
	}
	@Override
	public long getRequestCount() {
		return requestCount;
	}
	@Override
	public void cullConnections(long dropRatio, boolean enact) {
		//latencies.clear();
	}

}
