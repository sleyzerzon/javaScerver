package requestHandlers;

import serverCore.NotHttpException;
import serverCore.ReceivedData;


public interface RequestHandler {

	boolean parseData(ReceivedData d);
}
