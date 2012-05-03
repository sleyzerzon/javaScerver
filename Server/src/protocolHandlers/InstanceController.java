package protocolHandlers;

import instanceProtocol.InstanceMethod;
import instanceProtocol.InstanceRequest;
import instanceProtocol.InstanceResponse;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;

import Controllers.SimpleController;

import serverCore.Caller;
import serverCore.ReceivedData;
import serverCore.Server;


public class InstanceController implements Caller, ProtocolHandler {

	InetSocketAddress registry;
	boolean registered;
	
	public InstanceController(InetSocketAddress inetSocketAddress) {
		registry = inetSocketAddress;
		registered = false;

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
		if (!registered) {
			InstanceRequest request= new InstanceRequest();
			request.setMethod(InstanceMethod.GREET);
			request.setController(null);
			s.sendData(key, request.getBytes(), false);
		}
	}

	@Override
	public boolean parseData(ReceivedData d) {
		if (!registered) {
			InstanceResponse response = new InstanceResponse();
			response.fromBytes(d.data);
			System.out.println(response.getStatus());
		}
		return true;
		
	}

}
