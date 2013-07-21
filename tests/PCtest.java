package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import code.ProgramCounter;
import code.PC;

public class PCtest {
	private ProgramCounter pc;

	@Before
	public void setUp() throws Exception {
		pc = new PC();
	}

	@Test
	public void testGetValue() {
		int output = pc.getValue();
		int expected = 0;
		assertEquals(expected, output); //Default address value is 0, therefore 0 should be returned
	}
	
	
	
	
//	public void incrementPC() {
//		nextInstructionPointer++;
//	}
//	
//	public void setPC(int address) { //Used to set PC pointer
//		this.nextInstructionPointer = address;
//	}

}
