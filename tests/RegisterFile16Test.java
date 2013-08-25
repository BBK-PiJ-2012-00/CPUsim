package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import code.Data;
import code.Operand;
import code.OperandImpl;
import code.RegisterFile;
import code.RegisterFile16;
import code.ControlUnitImpl;
import code.UpdateListener;

public class RegisterFile16Test {
	RegisterFile registers;
	Operand testOperand;
	

	@Before
	public void setUp() throws Exception {
		registers = new RegisterFile16();
		registers.registerListener(new UpdateListener(new TestFrame())); //To prevent null pointer exception during testing
		testOperand = new OperandImpl(10);
	}
	

	@Test
	public void testRead() {
		assertNull(registers.read(0)); //Should be null as no data held in registers
	}
	
	@Test
	public void testInvalidRead() { //Testing the attempted read of an out of bounds register index
		assertNull(registers.read(16));
	}
	
	@Test
	public void testWrite() {
		registers.write(15, testOperand);
		Data output = registers.read(15);
		Data expected = testOperand;
		assertEquals(expected, output);
	}
	
	@Test 
	public void testInvalidWrite() { //Writing to an invalid index should have no effect
		registers.write(16, testOperand);
		for (int i = 0; i < 16; i++) {
			assertNull(registers.read(i)); //Check that no registers have been wrongly updated in event of false index
		}
	}

}
