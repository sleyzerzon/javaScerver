package protocolHandlers;

import http.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpStatus;
import http.NotHttpException;
import instanceProtocol.InstanceStats;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Controllers.Controller;
import Controllers.PageController;
import Controllers.SimpleController;


import serverCore.ReceivedData;


public class HttpHandler implements ProtocolHandler {

	private int count = 1;
	private Map<String, Controller> routes;
	private PageController pageController;

	public HttpHandler(boolean isHead) {
		routes = new HashMap<String, Controller>();
		if (isHead)
			pageController = new PageController();
		else pageController = null;
	}

	public boolean registerController(Controller c) {
		System.out.println("registering:"+c.getResourcePath() );
		routes.put(c.getResourcePath(), c);
		if (!routes.containsKey("/"))
			throw new RuntimeException("whaa");
		return true;
	}

	public boolean deregisterController(Controller c) {
		if (routes.containsValue(c))
			routes.remove(c.getResourcePath());
		return true;
	}

	@Override
	public boolean parseData(ReceivedData d) {
		HttpRequest request;
		if (d == null)
			return false;
		try {
			request = new HttpRequest(d.data);
		} catch (IOException e) {
			return false; //TODO: LOG ERROR
		} catch (NotHttpException e) {
			// TODO Auto-generated catch block
			return false;
		}
		HttpResponse response;
		if (pageController != null){
			response = pageController.handleRequest(request);
		} else {
			Controller c = routes.get(request.getPath());
			if (c != null && c.getMethods().contains(request.getMethod())) {
				response = c.handleRequest(request);
			} else {
				response = new HttpResponse();
			}
		}
		//TODO: reponseDirector.acceptReponse
		d.server.sendData(d.key, response.getBytes(), true);
		return true;
	}

	public InstanceStats getStats() {
		
		long requestCount = 0;
		long totalLatency = 0;
		long maxLatency = 0;
		InstanceStats stats = new InstanceStats();
		stats.setAvgLatency(0);
		stats.setMaxLatency(0);
		stats.setRequestsPerTime(0);
		for(Controller controller : routes.values()) {
		//	latencies.addAll(controller.getLatencies());
			stats =  controller.getStats();
			//System.out.println("controller:"+stats);
			//if (stats.getMaxLatency() > maxLatency)
				//maxLatency = stats.getMaxLatency();
			//totalLatency = stats.getAvgLatency();
			//requestCount = stats.getRequestsPerTime();
			//controller.resetStats();
		}
		/*stats = new InstanceStats();
		stats.setRequestsPerTime(requestCount);
		if (requestCount > 0) {
			stats.setAvgLatency(totalLatency);
			stats.setMaxLatency(maxLatency); 
		} else {
			stats.setAvgLatency(0);
			stats.setMaxLatency(0); 
		}*/
		return stats;
	}

	public void cullHttpConnections(long avgRequestRate, boolean enact) {

			
		for(Controller controller : routes.values()) {
			controller.cullConnections(0, enact);
		}

	}

	public void removeAddress(String hostAddress) {
		pageController.removeAddress(hostAddress);
	}
	public void acceptAddress(String hostAddress) {
		pageController.acceptAddress(hostAddress);

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
