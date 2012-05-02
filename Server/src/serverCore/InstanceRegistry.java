package serverCore;

import java.nio.channels.SelectionKey;

import requestHandlers.RequestHandler;

public class InstanceRegistry implements Caller, RequestHandler {

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
