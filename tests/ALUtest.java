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
		op1 = new OperandImpl(5);
		op2 = new OperandImpl(10);
		op3 = new OperandImpl(20000000);
		op4 = new OperandImpl(50000000);
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
