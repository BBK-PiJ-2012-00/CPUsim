package tests;

import static org.junit.Assert.*;

import org.junit.Test;


import code.DataLine;
import code.DataLineImpl;
import code.Operand;
import code.OperandImpl;

public class DataLineTest {
	private DataLine dLine = new DataLineImpl();

	@Test
	public void testReadNull() { //Upon initial instantiation, data field should be null
		assertNull(dLine.read());
	}
	
	@Test
	public void testPut() { //Test put method
		dLine.put(new OperandImpl(5));
		int expected = 5;
		Operand dataOutput = (Operand) dLine.read();
		//Data downcast to Operand and then integer for easier testing
		int output = dataOutput.unwrapInteger();
		assertEquals(expected, output);		
	}

}
