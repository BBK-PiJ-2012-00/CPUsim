package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


import code.DataBus;
import code.DataBusImpl;
import code.Operand;
import code.OperandImpl;
import code.UpdateListener;

public class DataBusTest {
	private DataBus dBus;
	
	@Before
	public void setUp() {
		dBus = new DataBusImpl();
		dBus.registerListener(new UpdateListener(new TestFrame())); //To avoid null pointer exception
	}

	@Test
	public void testReadNull() { //Upon initial instantiation, data field should be null
		assertNull(dBus.read());
	}
	
	@Test
	public void testPut() { //Test put method
		dBus.put(new OperandImpl(5));
		int expected = 5;
		Operand dataOutput = (Operand) dBus.read();
		//Data downcast to Operand and then integer for easier testing
		int output = dataOutput.unwrapInteger();
		assertEquals(expected, output);		
	}

}
