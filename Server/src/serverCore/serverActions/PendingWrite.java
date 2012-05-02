package serverCore.serverActions;
import java.nio.channels.SelectionKey;

public class PendingWrite extends PendingAction{
		public byte[] data;
		public boolean terminates;
		
		public PendingWrite(SelectionKey k, byte[] d, boolean terminates) {
			super(k);
			data = d;
			this.terminates = terminates;
		}
	}