package serverCore;


import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import protocolHandlers.HttpHandler;
import protocolHandlers.InstanceController;
import protocolHandlers.InstanceRegistry;
import protocolHandlers.ProtocolHandler;



/**
 * Consumes all data read by server
 * Directs server to write, terminate, or continue reading
 * @author jack
 *
 */
public class ResponseDirector implements Callee {

	ConcurrentLinkedQueue<ReceivedData> queue;
	ConcurrentHashMap<SelectionKey, ConnectionStatus> history;
	HashSet<SelectionKey> trustedPeers;
	ProtocolHandler localController;
	private boolean isChief = false;
	Server server;

	public ResponseDirector(boolean isChief, InetSocketAddress inetSocketAddress, Server server) {
		queue = new ConcurrentLinkedQueue<ReceivedData>();
		history = new ConcurrentHashMap<SelectionKey, ConnectionStatus>();
		trustedPeers = new HashSet<SelectionKey>();
		this.isChief = isChief;
		this.server = server;
		if (isChief) {
			localController = new InstanceRegistry();
		} else {
			localController = new InstanceController(inetSocketAddress);
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
		if (!isChief)
			((InstanceController)localController).phoneHome(server);
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
					if (! (new HttpHandler().parseData(r))) {
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

}
