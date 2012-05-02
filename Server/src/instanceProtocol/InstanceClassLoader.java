package instanceProtocol;

import http.Controller;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class InstanceClassLoader extends ClassLoader {

	@SuppressWarnings("unchecked")
	public Class<? extends Controller> parseController(byte[] data, int offset) throws ClassNotFoundException {


		System.out.println("class length found:"+(data.length-offset));


		Class<? extends Controller> clazz = null;
		System.out.println("Defining class...");
		try { clazz = (Class<? extends Controller>) defineClass(null, data, offset, data.length-offset);}
		catch (ClassFormatError e)
		{
			e.printStackTrace();
		}

		return clazz;

	}

}
