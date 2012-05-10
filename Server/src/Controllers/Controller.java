package Controllers;

import http.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;
import instanceProtocol.InstanceStats;

import java.util.Collection;
import java.util.Set;


public interface Controller {

	public String getResourcePath();
	
	public Set<HttpMethod> getMethods();
	
	public HttpResponse handleRequest(HttpRequest r);

	public InstanceStats getStats();

	public void cullConnections(long dropRatio, boolean enact);

	void resetStats();
	
}
