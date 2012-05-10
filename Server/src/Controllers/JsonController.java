package Controllers;

import http.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class JsonController implements Controller{

	final Set<HttpMethod> methods;
	final String resourcePath;
	private long requestCount;
	private ArrayList<Long> latencies;

	public JsonController() {
		methods = new HashSet<HttpMethod>();
		methods.add(HttpMethod.GET);
		resourcePath = "/";
		requestCount = 0;
		latencies = new ArrayList<Long>();
	}

	@Override
	public String getResourcePath() {
		// TODO Auto-generated method stub
		return resourcePath;
	}

	@Override
	public Set<HttpMethod> getMethods() {
		return methods;
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
	public HttpResponse handleRequest(HttpRequest request) {
		requestCount++;
		HttpResponse response = new HttpResponse();
		response.setStatus(HttpStatus.OK);
		response.addHeader("Connection", "close");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Content-type", "application/javascript");
		String avg = request.getQueries().get("avg");
		int avgLatency;
		try {
			avgLatency = Integer.valueOf(avg);
			latencies.add((long)avgLatency);
		} catch (NumberFormatException e) {
			avgLatency = 1;
		}
		response.setMessage( 
				"{ 'requests': '"+requestCount+"', 'latency':'"+avgLatency+"' }");
		return response;	
	}
	

	@Override
	public void cullConnections(long avgRequestRate) {
		// TODO Auto-generated method stub

	}

}
