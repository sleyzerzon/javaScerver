package core;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class DispatcherRegistry {

	public DispatcherRegistry() {
		NetworkInterface ni;
		try {
			ni = NetworkInterface.getByName("eth0");
			Enumeration<InetAddress> a = ni.getInetAddresses();
			InetAddress i = null;
			while (a.hasMoreElements()){
				i = a.nextElement();
				//if (i.IS THE IP ADDRESS??)
				System.out.println(a.nextElement());
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}
