package serverCore.serverActions;

import java.nio.channels.SelectionKey;

public class PendingTerminate extends PendingAction {
	public PendingTerminate(SelectionKey key) {
		super(key);
	}
}
