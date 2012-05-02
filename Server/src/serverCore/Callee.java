package serverCore;

import java.nio.channels.SelectionKey;

public interface Callee extends Runnable{

	public void pickupCall(Server s, SelectionKey k, byte[] b, int count);
}
