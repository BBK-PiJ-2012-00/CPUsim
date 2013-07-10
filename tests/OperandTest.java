package tests;

import static org.junit.Assert.*;

import code.Operand;
import code.OperandImpl;
import code.Data;

import org.junit.Test;

public class OperandTest {
	Operand intOp;
	Operand floatOp;

	/*
	 * Tests creation of an integer operand, and that it is recognised as such.
	 */
	@Test
	public void testIntOperand1() {
		intOp = new OperandImpl(5);
		boolean output = intOp.isInteger();
		assertTrue(output);
	}
	
	@Test
	public void testIntOperand2() {
		intOp = new OperandImpl(10);
		int output = intOp.unwrapInteger();
		int expected = 10;
		assertEquals(expected, output);
	}
	
	@Test
	public void testIntOperand3() { // Tests that a floating point operand is returned if requested of an integer operand
		intOp = new OperandImpl(13);
		double output = intOp.unwrapFloatingPoint();
		double expected = 13.0;
		assertEquals(expected, output, 13.0);		
	}

}
