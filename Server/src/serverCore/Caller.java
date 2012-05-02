package serverCore;

import java.nio.channels.SelectionKey;

public interface Caller extends Runnable {

	void greetCounterparty(Server s, SelectionKey key);
	
}
