package instanceProtocol;

import http.Controller;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class InstanceClassLoader extends ClassLoader {

	public Class<Controller> parseController(ByteArrayInputStream in) throws ClassNotFoundException {

		DataInputStream dataIn = new DataInputStream(in);
		byte[] data = new byte[in.available()];
		try{
			dataIn.readFully(data);
		} catch(IOException e) {
			return null;
		}
		

		Class<Controller> clazz = null;
		System.out.println("URLClassLoader: Defining class...");
		try { clazz = (Class<Controller>) defineClass("", data, 0, data.length);}
		catch (ClassFormatError e)
		{
			throw new ClassNotFoundException();
		}

		return clazz;

	}

}
