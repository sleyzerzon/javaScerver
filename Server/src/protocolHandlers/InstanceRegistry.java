package protocolHandlers;

import instanceProtocol.InstanceMethod;
import instanceProtocol.InstanceRequest;
import instanceProtocol.InstanceResponse;
import instanceProtocol.InstanceStatus;

import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import serverCore.ReceivedData;
import serverCore.ResponseDirector;


public class InstanceRegistry implements ProtocolHandler, Runnable {

	private ConcurrentLinkedQueue<ReceivedData> queue;
	Map<SelectionKey, Integer> instanceLatencies;
	//TODO: think of a more expressive name than intermediary
	ResponseDirector intermediary;

	public InstanceRegistry(ResponseDirector intermediary) {
		queue = new ConcurrentLinkedQueue<ReceivedData>();
		this.intermediary = intermediary;
		instanceLatencies = new HashMap<SelectionKey, Integer>();
	}

	@Override
	public void run() {
		ReceivedData r;
		//eat from the queue of new instances
		while(true) {
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
		//send heartbeat to get average latencies
		//recalculate load balancing and update instances
	}

	@Override
	public boolean parseData(ReceivedData d) {
		queue.add(d);
		return true;
	}
	
	private void eatData(ReceivedData d) {
		if (instanceLatencies.containsKey(d.key)) {
			//they are responding
		} else {
			//they are greeting
			InstanceRequest request = InstanceRequest.fromBytes(d.data);
			InstanceResponse response = new InstanceResponse();
			if (request.getMethod() == InstanceMethod.GREET) {
				instanceLatencies.put(d.key, 0);
				response.setStatus(InstanceStatus.OK);
			} else {
				response.setStatus(InstanceStatus.FAILED);
			}
			intermediary.acceptReponse(response, d, false);
			
		}
	}


}
