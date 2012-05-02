package protocolHandlers;

import http.HttpRequest;
import http.HttpResponse;
import http.NotHttpException;
import http.HttpRequest.HttpMethod;
import http.HttpResponse.HttpStatus;

import java.io.IOException;
import java.util.Map;


import serverCore.ReceivedData;


public class HttpHandler implements ProtocolHandler {

	private int count = 1;
	private Map<String, Controller> Routes;
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
	
		HttpResponse response = new HttpResponse();
	
		if (request.getMethod() == HttpMethod.GET && request.getUrl().equals("/")) { 
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
			d.server.sendData(d.key, response.getBytes(), false);
		}
		d.server.sendData(d.key, response.getBytes(), false);
		return true;
	}

}
