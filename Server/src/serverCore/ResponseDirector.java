package serverCore;


import instanceProtocol.InstanceRequest;
import instanceProtocol.InstanceResponse;
import instanceProtocol.InstanceStats;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import Controllers.Controller;
import protocolHandlers.HttpHandler;
import protocolHandlers.InstanceController;
import protocolHandlers.InstanceRegistry;
import protocolHandlers.ProtocolHandler;



/**
 * Consumes all data read by server
 * Directs server to write, terminate, or continue reading
 * runs in its own thread, consuming from a queue
 * @author jack
 *
 */
public class ResponseDirector implements Callee {

	ConcurrentLinkedQueue<ReceivedData> queue;
	ConcurrentHashMap<SelectionKey, ConnectionStatus> history;
	HashSet<SelectionKey> trustedPeers;
	ProtocolHandler localController;
	private HttpHandler httpHander;
	private boolean isChief = false;
	private Server server;

	public Server getServer() {
		return server;
	}

	public ResponseDirector(boolean isChief, InetSocketAddress master, Server server) {
		queue = new ConcurrentLinkedQueue<ReceivedData>();
		history = new ConcurrentHashMap<SelectionKey, ConnectionStatus>();
		trustedPeers = new HashSet<SelectionKey>();
		this.isChief = isChief;
		this.server = server;
		
		//TODO: could make more of these in threads to use cores
		httpHander = new HttpHandler(isChief);
		if (isChief) {
			localController = new InstanceRegistry(this);
		} else {
			localController = new InstanceController(master, this);
		}
	}

	public void pickupCall(Server s, SelectionKey k, byte[] b, int count) {
		ReceivedData d = new ReceivedData(s, k, Arrays.copyOf(b, count));
		synchronized (queue) {
			queue.add(d);
			queue.notify();	
		}
	}

	public void run() {
		if (!isChief) {
			InstanceController localNode = ((InstanceController)localController);
			try {
				server.initiateConnection(localNode.getRegistry(), localNode);
			} catch (IOException e) {
				// TODO don't know what to do if died
			}
			//((InstanceController)localController).phoneHome(server); 
		} else {
			new Thread((InstanceRegistry)localController).start();
		}
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

			synchronized(trustedPeers) {
				
				if (trustedPeers.contains(r.key)) {
					localController.parseData(r);
				} else {
					if (! (httpHander.parseData(r))) {
						localController.parseData(r);
						registerPeer(r.key);
					}
				}
			}
		}
	}

	public void registerPeer(SelectionKey key) {
		synchronized(trustedPeers){
			trustedPeers.add(key);
		}
	}

	public void acceptReponse(InstanceResponse response, ReceivedData d, boolean b) {
		server.sendData(d.key, response.getBytes(), b);
	}
	
	public void makeRequest(InstanceRequest request, ReceivedData d, boolean b) {
		server.sendData(d.key, request.getBytes(), b);
	}
	
	public void makeRequest(InstanceRequest request, SelectionKey key, boolean b) {
		server.sendData(key, request.getBytes(), b);
	}

	public boolean registerHttpRoute(Class<? extends Controller> controller, String path) {
		try {
			System.out.println(controller+" shouldn't be null");
			Controller c = controller.newInstance();
			System.out.println(controller.getName()+":"+c.getResourcePath());
			httpHander.registerController(c);
		} catch (InstantiationException e) {
			return false;
		} catch (IllegalAccessException e) {
			return false;
		}
		return true;
		
	}

	public InstanceStats getStats() {
		// TODO Auto-generated method stub
		return httpHander.getStats();
	}

	public void cullHttpConnections(long avgRequestRate, boolean enact) {
		httpHander.cullHttpConnections(avgRequestRate, enact);
		
	}

	public void acceptAddress(String hostAddress) {
		httpHander.acceptAddress(hostAddress);
		
	}

}
