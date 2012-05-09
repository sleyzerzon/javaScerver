package tests;

import static org.junit.Assert.*;
import instanceProtocol.InstanceResponse;
import instanceProtocol.InstanceStatus;

import org.junit.Test;

public class InstanceResponseTest {

	@Test
	public void test() {
		InstanceResponse r = new InstanceResponse();
		r.setStatus(InstanceStatus.STATUS);
		r.setBody("happy days".getBytes());
		InstanceResponse r2 = InstanceResponse.fromBytes(r.getBytes());
		assertEquals(r, r2);
		assertEquals(new String(r.getBody()), new String(r2.getBody()));
		
	}

}
