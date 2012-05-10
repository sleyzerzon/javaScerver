package tests;

import static org.junit.Assert.*;
import instanceProtocol.InstanceMethod;
import instanceProtocol.InstanceRequest;
import instanceProtocol.InstanceResponse;
import instanceProtocol.InstanceStatus;

import org.junit.Test;

import Controllers.JsonController;

public class InstanceTest {

	@Test
	public void responseTest() {
		InstanceResponse r = new InstanceResponse();
		r.setStatus(InstanceStatus.STATUS);
		r.setBody("happy days".getBytes());
		InstanceResponse r2 = InstanceResponse.fromBytes(r.getBytes());
		assertEquals(r, r2);
		assertEquals(new String(r.getBody()), new String(r2.getBody()));

	}

	@Test
	public void requestHeartbeatTest() {
		InstanceRequest r = new InstanceRequest();
		r.setMethod(InstanceMethod.HEARTBEAT);
		r.setBody("happy days".getBytes());
		InstanceRequest r2 = new InstanceRequest();
		r2.fromBytes(r.getBytes());
		assertEquals(r.getMethod(), r2.getMethod());
		assertEquals(new String(r.getBody()), new String(r2.getBody()));
		assertEquals(r, r2);
	}

	@Test
	public void requestControllerTest() {
		InstanceRequest r = new InstanceRequest();
		r.setMethod(InstanceMethod.CONTROLLER);
		r.setController(JsonController.class);

		InstanceRequest r2 = new InstanceRequest();

		r2.fromBytes(r.getBytes());
		try{
			assertEquals(r2.getController().newInstance().getResourcePath(),
					r.getController().newInstance().getResourcePath());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}

	}

}
