package serverCore;

import java.nio.channels.SelectionKey;

public class ReceivedData {
	public Server server;
	public SelectionKey key;
	public byte[] data; 

	public ReceivedData(Server s, SelectionKey k, byte[] b) {
		this.server = s;
		this.key = k;
		this.data = b;
	}
}