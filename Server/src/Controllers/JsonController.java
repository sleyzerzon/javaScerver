package Controllers;

import http.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;

import java.util.Collection;
import java.util.Set;

public class JsonController implements Controller{

	@Override
	public String getResourcePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<HttpMethod> getMethods() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpResponse handleRequest(HttpRequest r) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<? extends Long> getLatencies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getRequestCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void cullConnections(long avgRequestRate) {
		// TODO Auto-generated method stub
		
	}

}
