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
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

import Controllers.JsonController;

import serverCore.ReceivedData;
import serverCore.ResponseDirector;


public class InstanceRegistry implements ProtocolHandler, Runnable {

	private ConcurrentLinkedQueue<ReceivedData> queue;
	Map<SelectionKey, InstanceStats> instanceLatencies;
	Map<SelectionKey, Boolean> instanceResets;
	//TODO: think of a more expressive name than intermediary
	ResponseDirector intermediary;
	private static final long timeout = 3000;
	private Long avgRequestRate;
	private InstanceRequest controllerRequest;

	public InstanceRegistry(ResponseDirector intermediary) {
		queue = new ConcurrentLinkedQueue<ReceivedData>();
		this.intermediary = intermediary;
		avgRequestRate = (long) 0;
		instanceLatencies = new HashMap<SelectionKey, InstanceStats>();
		instanceResets = new HashMap<SelectionKey, Boolean>();
	}

	@Override
	public void run() {

		controllerRequest = new InstanceRequest();
		controllerRequest.setController(JsonController.class);
		controllerRequest.setMethod(InstanceMethod.CONTROLLER);
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
		InstanceRequest request;

		for(SelectionKey key : instanceLatencies.keySet()){
			request = new InstanceRequest();
			request.setMethod(InstanceMethod.HEARTBEAT);
			Boolean cull = false;
			if (instanceResets.containsKey(key) && instanceResets.get(key))
				cull = true;
			String body = avgRequestRate.toString() + ":" + cull.toString();
			request.setBody(body.getBytes());
			intermediary.makeRequest(request, key, false);
		}

	}

	boolean send = true;
	/*
	 * 
	 */
	private void balanceLoad() {
		//calculate load balance
		long requests = 0;
		long avgRequestCount = 0;
		long totalLatency = 0;
		for (Entry<SelectionKey, InstanceStats> entry : instanceLatencies.entrySet()) {
			System.out.println(entry.getValue());
			requests += entry.getValue().getRequestsPerTime();
			totalLatency += entry.getValue().getAvgLatency();
		}
		if (requests == 0) {
			System.out.println("empty all around");
			return;
		}
		avgRequestRate = totalLatency / instanceLatencies.size();
		avgRequestCount = requests / instanceLatencies.size();
		int i = 0;
		for (Entry<SelectionKey, InstanceStats> entry : instanceLatencies.entrySet()) {
			InstanceStats stats = entry.getValue();
			System.out.println(i++ + ", " + stats);
			if (stats.getRequestsPerTime() > (1.4 * avgRequestCount)){
				System.out.println("culling:"+i);
				instanceResets.put(entry.getKey(), true);
			}
			else instanceResets.put(entry.getKey(), false);
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
			InstanceRequest request = new InstanceRequest();
			request.fromBytes(d.data);
			//InstanceResponse response = new InstanceResponse();
			System.out.println("in " + request.getMethod());
			instanceLatencies.put(d.key, InstanceStats.newInitiate());
			System.out.println("initiated");
			String address = new String(request.getBody());
			System.out.println(address);
			intermediary.acceptAddress(address);
			intermediary.makeRequest(controllerRequest, d, false);
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
