package protocolHandlers;

import instanceProtocol.InstanceRequest;
import instanceProtocol.InstanceResponse;
import instanceProtocol.InstanceStatus;

import java.nio.channels.SelectionKey;

import serverCore.Caller;
import serverCore.ReceivedData;
import serverCore.Server;


public class InstanceRegistry implements Caller, ProtocolHandler {

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
		InstanceRequest request = new InstanceRequest();
		request.fromBytes(d.data);
		InstanceResponse response = new InstanceResponse();
		response.setStatus(InstanceStatus.OK);
		d.server.sendData(d.key, response.getBytes(), false);
		return false;
	}

	
}
