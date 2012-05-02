package serverCore.serverActions;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import serverCore.Caller;

public class PendingInitiate extends PendingAction {

	public SocketChannel channel;
	public Caller caller;
	
	
	public PendingInitiate(SocketChannel channel, Caller caller) {
		super(null);
		this.channel = channel;
		this.caller = caller;
	}

}
