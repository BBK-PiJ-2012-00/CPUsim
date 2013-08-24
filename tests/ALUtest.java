package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import code.*;

public class ALUtest {
	Operand op1;
	Operand op2;
	Operand op3;
	Operand op4;

	@Before
	public void setUp() throws Exception {
		ALU.registerListener(new UpdateListener(new TestFrame())); //To prevent null pointers during testing
		
		op1 = new OperandImpl(5);
		op2 = new OperandImpl(10);
		op3 = new OperandImpl(20000000);
		op4 = new OperandImpl(50000000);
	}

	@Test
	public void testAddition() {
		Operand expected = new OperandImpl(15);
		Operand output = ALU.AdditionUnit(op1, op2);
		assertEquals(expected.unwrapInteger(), output.unwrapInteger());
	}
	
	@Test
	public void testSubtraction() {
		Operand expected = new OperandImpl(-5);
		Operand output = ALU.SubtractionUnit(op1, op2);
		assertEquals(expected.unwrapInteger(), output.unwrapInteger());
	}
	
	@Test
	public void testDivision() {
		Operand expected = new OperandImpl(2000000);
		Operand output = ALU.DivisionUnit(op3, op2);
		assertEquals(expected.unwrapInteger(), output.unwrapInteger());
	}
	
	@Test
	public void testMultiplication() {
		Operand expected = new OperandImpl(500000000);
		Operand output = ALU.MultiplicationUnit(op4, op2);
		assertEquals(expected.unwrapInteger(), output.unwrapInteger());
	}

}
