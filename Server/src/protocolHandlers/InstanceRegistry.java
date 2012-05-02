package protocolHandlers;

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
		System.out.println(new String(d.data));
		return false;
	}

	
}
