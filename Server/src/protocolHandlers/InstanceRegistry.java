package protocolHandlers;

import instanceProtocol.InstanceMethod;
import instanceProtocol.InstanceRequest;
import instanceProtocol.InstanceResponse;
import instanceProtocol.InstanceStatus;

import java.nio.channels.SelectionKey;
import java.util.HashSet;
import java.util.Set;

import serverCore.Caller;
import serverCore.ReceivedData;
import serverCore.Server;


public class InstanceRegistry implements Caller, ProtocolHandler {

	Set<SelectionKey> slaves;
	Server server;

	public InstanceRegistry(Server server) {
		this.server = server;
		slaves = new HashSet<SelectionKey>();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		//start connection 
	}

	@Override
	public void greetCounterparty(Server s, SelectionKey key) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean parseData(ReceivedData d) {
		if (slaves.contains(d.key)) {
			//they are responding
		} else {
			//they are greeting
			InstanceRequest request = InstanceRequest.fromBytes(d.data);
			InstanceResponse response = new InstanceResponse();
			if (request.getMethod() == InstanceMethod.GREET) {
				slaves.add(d.key);
				response.setStatus(InstanceStatus.OK);
			} else {
				response.setStatus(InstanceStatus.FAILED);
			}
			server.sendData(d.key, response.getBytes(), false);
		}
		return true;
	}


}
