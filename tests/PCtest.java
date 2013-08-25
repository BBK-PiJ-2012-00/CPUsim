package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import code.ProgramCounter;
import code.PC;
import code.UpdateListener;

public class PCtest {
	private ProgramCounter pc;

	@Before
	public void setUp() throws Exception {
		pc = new PC();
		pc.registerListener(new UpdateListener(new TestFrame()));
	}

	@Test
	public void testGetValue() {
		int output = pc.getValue();
		int expected = 0;
		assertEquals(expected, output); //Default address value is 0, therefore 0 should be returned
	}
	
	@Test
	public void testIncrementPC() {
		pc.incrementPC();
		int output = pc.getValue();
		int expected = 1; //If default PC value is 0, incrementing should mean it equals 1.
		assertEquals(expected, output);
	}
	
	@Test
	public void testSetPC() {
		pc.setPC(99);
		int output = pc.getValue();
		int expected = 99;
		assertEquals(expected, output);
	}
	


}
