package protocolHandlers;

import instanceProtocol.InstanceMethod;
import instanceProtocol.InstanceRequest;
import instanceProtocol.InstanceResponse;
import instanceProtocol.InstanceStatus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;

import Controllers.SimpleController;

import serverCore.Caller;
import serverCore.ReceivedData;
import serverCore.ResponseDirector;
import serverCore.Server;


public class InstanceController implements Caller, ProtocolHandler {

	InetSocketAddress registry;
	boolean registered;
	ResponseDirector intermediary;

	public InstanceController(InetSocketAddress inetSocketAddress, ResponseDirector r) {
		registry = inetSocketAddress;
		registered = false;
		intermediary = r;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override 
	/*TODO: this is currently running in the server thread
	 *initiated request should be queued, just be a special case of parseData
	 */
	public void greetCounterparty(Server s, SelectionKey key) {
		if (!registered) {
			InstanceRequest request= new InstanceRequest();
			request.setMethod(InstanceMethod.GREET);
			request.setController(null);
			
			//TODO: abstract into responsedirector
			s.sendData(key, request.getBytes(), false);
		} else {
			//shouldn't happen
			throw new RuntimeException(
					"Instance protocal broken - registered instance shouldn't initiate any requests");
		}
	}

	@Override
	public boolean parseData(ReceivedData d) {
		eatData(d); //TODO:put in queue
		return true;
	}
	
	private void eatData(ReceivedData d) {
		if (!registered) {
			//they are responding to your greeting
			InstanceResponse response = InstanceResponse.fromBytes(d.data);
			System.out.println(response.getStatus());
			registered = true;
		} else {
			//they are calling you
			InstanceRequest request = InstanceRequest.fromBytes(d.data);
			InstanceResponse response = new InstanceResponse();
			switch (request.getMethod()) {
			case CONTROLLER:
				if (intermediary.registerHttpRoute(request.getController())) {
					response.setStatus(InstanceStatus.OK); 
				} else {
					response.setStatus(InstanceStatus.FAILED); 
				}
				break;
				
			case GREET:
				response.setStatus(InstanceStatus.FAILED);
				break;
				
			case HEARBEAT:
				break;
				
			case SLEEP:
				break;

			default:
				break;
			}
			
		}
	}

	public InetSocketAddress getRegistry() {
		return registry;
	}

}
