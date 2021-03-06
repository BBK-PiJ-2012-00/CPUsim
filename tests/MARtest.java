package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import code.MemoryAddressRegister;
import code.MAR;
import code.UpdateListener;

public class MARtest {
	private MemoryAddressRegister mar;
	
	@Before
	public void setUp() {		
		mar = new MAR();
		mar.registerListener(new UpdateListener(new TestFrame()));
	}
	

	@Test
	public void testRead() {
		int output = mar.read();
		int expected = 0; //Default value of registerContents field is 0.
		assertEquals(expected, output);
	}
	
	@Test
	public void testWrite() {
		mar.write(99);
		int expected = 99;
		int output = mar.read();
		assertEquals(expected, output);
	}

}
