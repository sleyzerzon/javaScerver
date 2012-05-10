package protocolHandlers;

import http.NotHttpException;
import serverCore.ReceivedData;


public interface ProtocolHandler extends Runnable {

	boolean parseData(ReceivedData d);
}
