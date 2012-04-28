package core;

import java.nio.channels.SelectionKey;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.nio.channels.SocketChannel;

public abstract class Director implements Runnable {

	ConcurrentLinkedQueue<ReceivedData> queue;

	public Director() {
		queue = new ConcurrentLinkedQueue<ReceivedData>();
	}

	public void parseLater(Server s, SelectionKey k, byte[] b, int count) {
		ReceivedData d = new ReceivedData(s, k, Arrays.copyOf(b, count));
		synchronized (queue) {
			queue.add(d);
			queue.notify();	
		}
	}

	protected abstract void parseData(ReceivedData d); 

	public void run() {
		while(true) {
			synchronized(queue){
				while(queue.isEmpty()) {
					try {
						queue.wait();
					} catch (InterruptedException e) {
					}
				}
				parseData(queue.poll());
			}
		}
	}

	protected class ReceivedData {
		Server server;
		SelectionKey key;
		byte[] data;

		public ReceivedData(Server s, SelectionKey k, byte[] b) {
			this.server = s;
			this.key = k;
			this.data = b;
		}
	}
}
