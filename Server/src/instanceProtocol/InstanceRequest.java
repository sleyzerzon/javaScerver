package instanceProtocol;

import http.HttpMethod;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import Controllers.Controller;
import Controllers.SimpleController;


public class InstanceRequest {

	private InstanceMethod method;
	private Class<? extends Controller> controller;

	public InstanceRequest() {
		method = null;
		controller = null;
	}

	public static InstanceRequest fromBytes(byte[] data) {
		// TODO Auto-generated method stub
		InstanceRequest request = new InstanceRequest();
		System.out.println("data length:"+data.length);
		ByteArrayInputStream  in = new ByteArrayInputStream(data);
		InputStreamReader readMethod = new InputStreamReader(in);
		char c;
		int offset = 1;
		try {
			StringBuilder builder = new StringBuilder();
			while(readMethod.ready() && (c = (char)readMethod.read()) != '\n') {
				builder.append((char)c);
				offset++;
			}
			request.method = InstanceMethod.valueOf(builder.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Class<? extends Controller> cont = null;
		switch (request.method) {
		case GREET:
			
			break;
			
		case HEARBEAT:
			
			break;
			
		case CONTROLLER:
			InstanceClassLoader cl = new InstanceClassLoader();
			try {
				cont = cl.parseController(data, offset);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
		request.controller = cont;
		
		return request;
	}

	public byte[] getBytes() {
		// TODO Auto-generated method stub
		byte[] m = (method.toString() + '\n').getBytes();

		if (controller == null)
			return m;

		byte[] c = new byte[0];
		switch (method) {

		case CONTROLLER:
			c = crunchitizeController();
			break;

		case GREET:
			break;
			
		case HEARBEAT:
			break;
			
		default:
			break;
		}
		
		byte[] all = new byte[m.length + c.length];
		System.arraycopy(m, 0, all, 0, m.length);
		if (c.length > 0)
			System.arraycopy(c, 0, all, m.length, c.length);
		
		return all;

	}

	
	private byte[] crunchitizeController(){
		byte[] c;
		URL path = Thread.currentThread().getContextClassLoader().getSystemResource(controller.getName().replaceAll("\\.", "\\\\")+".class");
		try {
			File file = new File(URLDecoder.decode(path.getFile(), "UTF-8"));
			FileInputStream is = new FileInputStream(file);

			DataInputStream dis = new DataInputStream(is);
			c = new byte[(int) file.length()];
			System.out.println("class length is:"+c.length);
			dis.readFully(c);
			return c;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	return new byte[0];
	}

	public void setController(Class<? extends Controller> controller) {
		this.controller = controller;
	}

	public Class<? extends Controller> getController() {
		return controller;
	}

	public InstanceMethod getMethod() {
		return method;
	}
	
	public void setMethod(InstanceMethod method) {
		this.method = method;
	}
}
