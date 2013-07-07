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

}
