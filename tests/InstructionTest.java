package tests;

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
	public void transferInstructionOpcodeTest() { //To test instruction not created with illegal opcode for format
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
	public void arithmeticInstrTest() {
		instr = new ArithmeticInstr(Opcode.ADD, 10, 20);
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
		

}
