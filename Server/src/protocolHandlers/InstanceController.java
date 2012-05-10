package protocolHandlers;

import instanceProtocol.InstanceMethod;
import instanceProtocol.InstanceRequest;
import instanceProtocol.InstanceResponse;
import instanceProtocol.InstanceStats;
import instanceProtocol.InstanceStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.util.concurrent.ConcurrentLinkedQueue;

import Controllers.SimpleController;

import serverCore.Caller;
import serverCore.ReceivedData;
import serverCore.ResponseDirector;
import serverCore.Server;


public class InstanceController implements Caller, ProtocolHandler {

	InetSocketAddress registry;
	boolean registered;
	ResponseDirector intermediary;
	ConcurrentLinkedQueue<ReceivedData> queue;

	public InstanceController(InetSocketAddress inetSocketAddress, ResponseDirector r) {
		registry = inetSocketAddress;
		registered = false;
		intermediary = r;
		queue = new ConcurrentLinkedQueue<ReceivedData>();
	}

	@Override
	public void run() {
		while(true) {
			ReceivedData r = null;
			synchronized(queue){
				while(queue.isEmpty()) {
					try {
						queue.wait();
					} catch (InterruptedException e) {
					}
				}
				r = queue.poll();
			}
			
			eatData(r);

		}

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
			request.setBody(intermediary.getServer().getAddress().getBytes());

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
		synchronized (queue) {
			queue.add(d);
			queue.notify();	
		}
		return true;
	}

	private void eatData(ReceivedData d) {
		/*if (!registered) {
			//they are responding to your greeting
			InstanceResponse response = InstanceResponse.fromBytes(d.data);
			System.out.println(response.getStatus());
			registered = true;
		} else { */
		//they are calling you
		InstanceRequest request = new InstanceRequest();
		request.fromBytes(d.data);
		InstanceResponse response = new InstanceResponse();
		switch (request.getMethod()) {
		case CONTROLLER:
			if (request.getController() == null)
				throw new RuntimeException("what?");
			if (intermediary.registerHttpRoute(request.getController(), "/")) {
				response.setStatus(InstanceStatus.OK); 
			} else {
				response.setStatus(InstanceStatus.FAILED); 
			}
			//intermediary.acceptReponse(response, d, false);
			break;

		case GREET:
			response.setStatus(InstanceStatus.FAILED);
			break;

		case HEARTBEAT:
			String[] heartBeat = new String(request.getBody()).split(":");
			long avgRequestRate = Long.parseLong(heartBeat[0]);
			boolean cull = heartBeat[1].equalsIgnoreCase("true");
			System.out.println("\n\n\n\n\n"+avgRequestRate + ":" + cull);
			intermediary.cullHttpConnections(avgRequestRate, cull);
			sendStats(d);
			break;

		case SLEEP:
			break;

		default:
			break;
		}

		//}
	}

	private void sendStats(ReceivedData d) {
		InstanceStats stats = intermediary.getStats();
		InstanceResponse response = new InstanceResponse();
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);
			out.writeObject(stats);

			response.setStatus(InstanceStatus.STATUS);
			response.setBody(bos.toByteArray()); 
		} catch (IOException e) {
			
		}

		intermediary.acceptReponse(response, d, false);
	}

	public InetSocketAddress getRegistry() {
		return registry;
	}

}
