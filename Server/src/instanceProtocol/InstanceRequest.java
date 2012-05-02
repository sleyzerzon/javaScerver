package instanceProtocol;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import Controllers.SimpleController;

import http.Controller;

public class InstanceRequest {

	InstanceMethod method;
	private Class<? extends Controller> controller;

	public InstanceRequest() {
		// TODO Auto-generated constructor stub
	}

	public void setMethod(InstanceMethod method) {
		this.method = method;
	}

	public void fromBytes(byte[] data) {
		// TODO Auto-generated method stub
		ByteArrayInputStream  in = new ByteArrayInputStream(data);
		InputStreamReader readMethod = new InputStreamReader(in);
		int c;
		try {
			StringBuilder builder = new StringBuilder();
			while(readMethod.ready() && (c =readMethod.read()) != 10) {
				builder.append((char)c);
			}
			System.out.println(builder.toString());
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		InstanceClassLoader cl = new InstanceClassLoader();
		Class<? extends Controller> cont;
		try {
			cont = cl.parseController(in);
			Controller controller = cont.newInstance();
			System.out.println(controller.say());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public byte[] getBytes() {
		// TODO Auto-generated method stub
		byte[] m = (method.toString() + "\n").getBytes();
		

		String className = controller.getName();  // e.g. "foo.Bar"
		String resourceName = "/home/jack/214/gui/eclipse/javaScerver/bin/Controllers/" + className +".class"; 
		System.out.println(resourceName);
		InputStream is = controller.getClassLoader().getResourceAsStream(resourceName);
		DataInputStream dis = new DataInputStream(is);
		try {
			byte[] c = new byte[is.available()];
			dis.readFully(c);
			byte[] all = new byte[m.length + c.length];
			System.arraycopy(m, 0, all, 0, m.length);
			System.arraycopy(c, 0, all, m.length, c.length);
			return all;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void setController(Class<? extends Controller> controller) {
		this.controller = controller;
		
	}
}
