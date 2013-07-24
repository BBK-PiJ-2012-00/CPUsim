package tests;

import code.BranchInstr;
import code.Instruction;
import code.Opcode;
import code.TransferInstr;
import code.ArithmeticInstr;

import static org.junit.Assert.*;

import org.junit.Test;

public class InstructionTest {
	Instruction instr;

	@Test
	public void transferInstructionTest() {
		instr = new TransferInstr(Opcode.MOVE, 0, 0);
		Opcode expected = Opcode.MOVE;
		Opcode output = instr.getOpcode(); //validates getOpcode() method
		assertEquals(expected, output);
		
	}
	
	@Test 
	public void transferInstructionOpcodeTest() { //To test instruction is not created with illegal opcode for format
		try {
			instr = new TransferInstr(Opcode.ADD, 0, 0);
		}
		catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		}
		assertNull(instr); //instr should still be null if creation has failed, which it should because of invalid opcode		
	}
	
	@Test
	public void transferInstrMachineStringTest() {
		instr = new TransferInstr(Opcode.LOAD, 0, 0);
		String output = instr.toMachineString();
		String expected = "1 0 0";
		assertEquals(expected, output);
		
	}
	
	@Test
	public void transferInstrToStringTest() {
		instr = new TransferInstr(Opcode.STORE, 0, 0);
		String output = instr.toString();
		String expected = "STORE 0 0";
		assertEquals(expected, output);
	}
	
	@Test 
	public void transferInstrGetOpcodeTest() {
		instr = new TransferInstr(Opcode.MOVE, 0, 0);
		Opcode output = instr.getOpcode();
		Opcode expected = Opcode.MOVE;
		assertEquals(expected, output);
	}
	
	@Test
	public void transferInstrGetField1Test() {
		instr = new TransferInstr(Opcode.LOAD, 50, 70);
		int output = instr.getField1();
		int expected = 50;
		assertEquals(expected, output);
	}
	
	@Test
	public void transferInstrGetField2Test() {
		instr = new TransferInstr(Opcode.STORE, 50, 70);
		int output = instr.getField2();
		int expected = 70;
		assertEquals(expected, output);
	}
	
	@Test
	public void arithmeticInstrTest() {
		instr = new ArithmeticInstr(Opcode.ADD, 10, 15);
		Opcode output = instr.getOpcode();
		Opcode expected = Opcode.ADD;
		assertEquals(expected, output);
	}
	
	@Test
	public void arithmeticInstrOpcodeTest() {
		try {
			instr = new ArithmeticInstr(Opcode.BR, 10, 20);
		}
		catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		}
		assertNull(instr); //Checks instruction isn't created with invalid opcode for its format.
	}
	
	@Test
	public void arithmeticInstrMachineStringTest() { //Tests toMachineString()
		instr = new ArithmeticInstr(Opcode.SUB, 0, 0);
		String output = instr.toMachineString();
		String expected = "5 0 0";
		assertEquals(expected, output);
		
	}
	
	@Test
	public void arithmeticInstrToStringTest() { //Tests toString()
		instr = new ArithmeticInstr(Opcode.DIV, 0, 0);
		String output = instr.toString();
		String expected = "DIV 0 0";
		assertEquals(expected, output);
	}
	
	@Test 
	public void arithmeticInstrGetOpcodeTest() { //Tests getOpcode()
		instr = new ArithmeticInstr(Opcode.MUL, 0, 0);
		Opcode output = instr.getOpcode();
		Opcode expected = Opcode.MUL;
		assertEquals(expected, output);
	}
	
	@Test
	public void arithmeticInstrGetField1Test() {
		instr = new ArithmeticInstr(Opcode.ADD, 5, 10);
		int output = instr.getField1();
		int expected = 5;
		assertEquals(expected, output);
	}
	
	@Test
	public void arithmeticInstrGetField2Test() {
		instr = new ArithmeticInstr(Opcode.ADD, 5, 10);
		int output = instr.getField2();
		int expected = 10;
		assertEquals(expected, output);
	}
	
	@Test
	public void branchInstrTest() { //Tests creation of branch instruction
		instr = new BranchInstr(Opcode.BR, 0);
		assertNotNull(instr);
		
	}
	
	@Test 
	public void branchInstrInvalidOpcodeTest() { //To test instruction is not created with illegal opcode for format
		try {
			instr = new BranchInstr(Opcode.ADD, 0);
		}
		catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		}
		assertNull(instr); //instr should still be null if creation has failed, which it should because of invalid opcode		
	}
	
	@Test
	public void branchInstrMachineStringTest() {
		instr = new BranchInstr(Opcode.BRE, 0);
		String output = instr.toMachineString();
		String expected = "10 0";
		assertEquals(expected, output);
		
	}
	
	@Test
	public void branchInstrToStringTest() {
		instr = new BranchInstr(Opcode.BRNE, 0);
		String output = instr.toString();
		String expected = "BRNE 0";
		assertEquals(expected, output);
	}
	
	@Test 
	public void branchInstrGetOpcodeTest() {
		instr = new BranchInstr(Opcode.BR, 0);
		Opcode output = instr.getOpcode();
		Opcode expected = Opcode.BR;
		assertEquals(expected, output);
	}
	
	@Test
	public void branchInstrGetField1Test() {
		instr = new BranchInstr(Opcode.BRZ, 45);
		int output = instr.getField1();
		int expected = 45;
		assertEquals(expected, output);
	}
	
	@Test
	public void branchInstrGetField2Test() {
		instr = new BranchInstr(Opcode.SKZ, 70);
		int output = instr.getField2();
		int expected = -1; //-1 fixed return value for getField2() for branch instructions
		assertEquals(expected, output);
	}
	

}
