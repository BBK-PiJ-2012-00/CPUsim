package tests;

import static org.junit.Assert.*;

import code.InstructionRegister;
import code.IR;
import code.Instruction;
import code.TransferInstr;
import code.Opcode;

import org.junit.Before;
import org.junit.Test;

public class IRtest {
	private InstructionRegister ir;
	private Instruction testInstr;
	
	@Before
	public void setUp() {
		ir = new IR();
		testInstr = new TransferInstr(Opcode.STORE, 0, 0);
	}	
	

	@Test
	public void testRead() {
		assertNull(ir.read()); //read() should return null because contents field is uninitialised.
	}
	
	@Test
	public void testLoadIR() {
		ir.loadIR(testInstr);
		Instruction output = ir.read();
		Instruction expected = testInstr;
		assertEquals(expected, output);
	}

}
