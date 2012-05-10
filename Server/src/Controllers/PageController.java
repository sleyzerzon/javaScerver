package Controllers;

import http.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpStatus;
import instanceProtocol.InstanceStats;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

public class PageController implements Controller {

	List<String> instances;

	public PageController() {
		instances = new ArrayList<String>();
	}

	@Override
	public String getResourcePath() {
		return null;
	}

	@Override
	public Set<HttpMethod> getMethods() {
		return null;
	}

	Random random = new Random();
	@Override
	public HttpResponse handleRequest(HttpRequest request) {
		HttpResponse response = new HttpResponse();
		response.setStatus(HttpStatus.OK);
		response.addHeader("Connection", "close");
		System.out.println("index:"+request.getUrl().indexOf("instance"));
		if (request.getUrl().indexOf("instance") != -1) {
			response.addHeader("Content-type", "application/javascript");
			String instance = instances.isEmpty()?"none":instances.get(random.nextInt(instances.size()));
			String message = "{ 'instance': '"+instance+"' }";
			System.out.println(message);
			response.setMessage(message);
			return response;
		}
			
		Map<String, String> queries = request.getQueries();
		for (Entry<String, String> e : queries.entrySet())
			System.out.println(e.getKey() + ":" + e.getValue());
		response.addHeader("Content-Type", "text/html");
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("StaticPage.html");
		//File file = new File(in.getFile());
		
		try {
			byte[] fileBody = new byte[in.available()];
			DataInputStream dis = new DataInputStream(in);
			//c = new byte[dis.available()];
			//System.out.println("class length is:"+c.length);
			dis.readFully(fileBody);
			
			//FileInputStream fin = new FileInputStream(file);
			//fin.read(fileBody);
			response.setBody(fileBody);
		} catch (IOException e) {

		}
		return response;	
	}


	@Override
	public InstanceStats getStats() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cullConnections(long dropRatio, boolean enact) {
		// TODO Auto-generated method stub

	}

	public void removeAddress(String hostAddress) {
		instances.remove(hostAddress);
	}

	public void acceptAddress(String hostAddress) {
		if (hostAddress.startsWith("localhost/"))
			instances.add(hostAddress.substring(10));
		else instances.add(hostAddress);

	}

	@Override
	public void resetStats() {
		// TODO Auto-generated method stub
		
	}
}
