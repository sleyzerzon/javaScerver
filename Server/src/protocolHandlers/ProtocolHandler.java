package protocolHandlers;

import http.NotHttpException;
import serverCore.ReceivedData;


public interface ProtocolHandler {

	boolean parseData(ReceivedData d);
}
