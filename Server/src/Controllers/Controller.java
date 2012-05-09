package Controllers;

import http.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;

import java.util.Collection;
import java.util.Set;


public interface Controller {

	public String getResourcePath();
	
	public Set<HttpMethod> getMethods();
	
	public HttpResponse handleRequest(HttpRequest r);

	public Collection<? extends Long> getLatencies();

	public long getRequestCount();

	public void cullConnections(long avgRequestRate);
	
}
