package tests;

import code.Instruction;
import code.Opcode;
import code.TransferInstr;

import static org.junit.Assert.*;

import org.junit.Test;

public class InstructionTest {

	@Test
	public void TransferInstructionTest() {
		Instruction instr = new TransferInstr(Opcode.MOVE, 0, 0);
		Opcode expected = Opcode.MOVE;
		Opcode output = instr.getOpcode();
		assertEquals(expected, output);
		
	}
	
	@Test 
	public void TransferInstructionOpcodeTest() { //To test instruction not created with illegal opcode for format
		Instruction instr = null; 
		try {
			instr = new TransferInstr(Opcode.ADD, 0, 0);
		}
		catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		}
		assertNull(instr);		
	}

}
