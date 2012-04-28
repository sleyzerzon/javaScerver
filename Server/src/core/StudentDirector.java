package core;

import java.io.IOException;

import core.Director.ReceivedData;
import core.HttpRequest.HttpMethod;
import core.HttpResponse.HttpStatus;

public class StudentDirector extends Director {
	private int count = 1;
	
	@Override
	protected void parseData(ReceivedData d) {
		System.out.println(count++);
		HttpRequest request;
		try {
			request = new HttpRequest(d.data);
		} catch (IOException e) {
			e.printStackTrace();
			return; //TODO: LOG ERROR
		}

		HttpResponse response = new HttpResponse();

		if (request.getMethod() == HttpMethod.GET && request.getUrl().equals("/")) { 
			response.setStatus(HttpStatus.OK);
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
		}
		d.server.send(d.key, response.getBytes());
	}

}
