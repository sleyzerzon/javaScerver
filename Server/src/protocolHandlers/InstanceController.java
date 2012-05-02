package protocolHandlers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;

import serverCore.Caller;
import serverCore.ReceivedData;
import serverCore.Server;


public class InstanceController implements Caller, ProtocolHandler {

	InetSocketAddress registry;
	
	public InstanceController(InetSocketAddress inetSocketAddress) {
		registry = inetSocketAddress;

	}

	public void phoneHome(Server s) {
		try {
			s.initiateConnection(registry, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void greetCounterparty(Server s, SelectionKey key) {
		s.sendData(key, "haha".getBytes(), false);

	}

	@Override
	public boolean parseData(ReceivedData d) {
		// TODO Auto-generated method stub
		return false;
		
	}

}
