package protocolHandlers;

import instanceProtocol.InstanceMethod;
import instanceProtocol.InstanceRequest;
import instanceProtocol.InstanceResponse;
import instanceProtocol.InstanceStats;
import instanceProtocol.InstanceStatus;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

import Controllers.SimpleController;

import serverCore.ReceivedData;
import serverCore.ResponseDirector;


public class InstanceRegistry implements ProtocolHandler, Runnable {

	private ConcurrentLinkedQueue<ReceivedData> queue;
	Map<SelectionKey, InstanceStats> instanceLatencies;
	//TODO: think of a more expressive name than intermediary
	ResponseDirector intermediary;
	public static final long timeout = 1000;

	public InstanceRegistry(ResponseDirector intermediary) {
		queue = new ConcurrentLinkedQueue<ReceivedData>();
		this.intermediary = intermediary;
		instanceLatencies = new HashMap<SelectionKey, InstanceStats>();
	}

	@Override
	public void run() {

		while(true) {

			try {
				synchronized(Thread.currentThread()) {
					Thread.currentThread().wait(timeout);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!queue.isEmpty()) {
				eatData(queue.poll());
			}

			balanceLoad();
			heartbeat();
		}
	}

	/*
	 * sends request for updated performance stats to each dependent 
	 */
	private void heartbeat() {
		InstanceRequest request = new InstanceRequest();
		request.setMethod(InstanceMethod.HEARBEAT);
		for(SelectionKey key : instanceLatencies.keySet()){
			intermediary.makeRequest(request, key, false);
		}

	}

	boolean send = true;
	/*
	 * 
	 */
	private void balanceLoad() {
		//calculate load balance
		InstanceRequest request = new InstanceRequest();
		request.setController(SimpleController.class);
		request.setMethod(InstanceMethod.CONTROLLER);
		if (send) {
			for (Entry<SelectionKey, InstanceStats> entry : instanceLatencies.entrySet()) {
				if (entry.getValue().getRequestsPerTime() == 0)
					System.out.println("out " + request.getMethod() + ": " + request.getController().getName());
				intermediary.makeRequest(request, entry.getKey(), false);
				send = false;
			}
		} else {
			for (Entry<SelectionKey, InstanceStats> entry : instanceLatencies.entrySet()) {
				System.out.println(entry.getValue());
			}
		}
	}

	@Override
	public boolean parseData(ReceivedData d) {
		queue.add(d);
		return true;
	}

	private void eatData(ReceivedData d) {
		if (instanceLatencies.containsKey(d.key)) {
			//they are responding
			InstanceResponse response = InstanceResponse.fromBytes(d.data);
			switch (response.getStatus()) {
			case OK:
				break;

			case STATUS:
				instanceLatencies.put(d.key, getStats(response.getBody()));
				break;

			default:
				break;
			}
		} else {
			//they are greeting
			InstanceRequest request = InstanceRequest.fromBytes(d.data);
			//InstanceResponse response = new InstanceResponse();
			System.out.println("in " + request.getMethod());
			instanceLatencies.put(d.key, InstanceStats.newInitiate());
			/*if (request.getMethod() == InstanceMethod.GREET) {

				response.setStatus(InstanceStatus.OK);
			} else {
				response.setStatus(InstanceStatus.FAILED);
			}
			intermediary.acceptReponse(response, d, false);
			 */
		}
	}

	private InstanceStats getStats(byte[] body) {
		InstanceStats stats = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(body);
			ObjectInput in = new ObjectInputStream(bis);
			stats = (InstanceStats)in.readObject(); 
		} catch(IOException e) {

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
		}
		return stats;
	}
}
