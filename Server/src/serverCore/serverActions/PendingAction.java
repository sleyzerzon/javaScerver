package serverCore.serverActions;

import java.nio.channels.SelectionKey;
import java.util.concurrent.atomic.AtomicBoolean;

public class PendingAction {

	
	public SelectionKey key;
	public AtomicBoolean cancelled;;

	public PendingAction(SelectionKey key) {
		this.key = key;
		cancelled = new AtomicBoolean();
		cancelled.set(false);
	}
	
	public void cancel() {
		cancelled.set(true);
	}
	
	public boolean isCancelled(){
		return cancelled.get();
	}
}
