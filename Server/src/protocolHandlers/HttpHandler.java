package protocolHandlers;

import http.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpStatus;
import http.NotHttpException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Controllers.Controller;
import Controllers.SimpleController;


import serverCore.ReceivedData;


public class HttpHandler implements ProtocolHandler {

	private int count = 1;
	private Map<String, Controller> routes;

	public HttpHandler() {
		routes = new HashMap<String, Controller>();
		registerController(new SimpleController());
	}
	
	public boolean registerController(Controller c) {
		routes.put(c.getResourcePath(), c);
		return true;
	}
	
	public boolean deregisterController(Controller c) {
		if (routes.containsValue(c))
			routes.remove(c.getResourcePath());
		return true;
	}

	@Override
	public boolean parseData(ReceivedData d) {
		System.out.println(count++);
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
		Controller c = routes.get(request.getUrl());
		if (c != null && c.getMethods().contains(request.getMethod())) {
			response = c.handleRequest(request);
		} else {
			response = new HttpResponse();
		}
		//TODO: reponseDirector.acceptReponse
		d.server.sendData(d.key, response.getBytes(), true);
		return true;
	}

}
