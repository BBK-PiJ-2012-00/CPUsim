package tests;

import static org.junit.Assert.*;

import code.Operand;
import code.OperandImpl;

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
		assertEquals(expected, output, 0.0);		
	}
	
	@Test
	public void testIntOperand4() {
		intOp = new OperandImpl(7);
		assertFalse(intOp.isInstruction());
	}
	
	@Test
	public void testIntOperand5() {
		intOp = new OperandImpl(0);
		assertFalse(intOp.isFloatingPoint());
	}
	
	@Test
	public void testFloatingPointOperand1() { //Tests creation of operand that wraps a floating point value
		floatOp = new OperandImpl(1.5);
		boolean output = floatOp.isFloatingPoint();
		assertTrue(output);
	}
	
	@Test
	public void testFloatingPointOperand2() {
		floatOp = new OperandImpl(4.321);
		double output = floatOp.unwrapFloatingPoint();
		double expected = 4.321;
		assertEquals(expected, output, 0.0);
	}
	
	@Test
	public void testFloatingPointOperand3() { //Tests correct return of an integer if requested of a floating point operand
		floatOp = new OperandImpl(6.79);
		int output = floatOp.unwrapInteger();
		int expected = 6;
		assertEquals(expected, output);
	}
	
	@Test
	public void testFloatingPointOperand4() {
		floatOp = new OperandImpl(2.2);
		assertFalse(floatOp.isInstruction());
	}
	
	@Test
	public void testFloatingPointOperand5() {
		floatOp = new OperandImpl(3.4);
		assertFalse(floatOp.isInteger());
	}

}
