package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import code.*;

public class ControlUnitTest {
	private ControlUnit controlUnit;
	Instruction testInstr;
	MainMemory memory;
	InstructionRegister ir;

	@Before
	public void setUp() throws Exception {
		controlUnit = new ControlUnitImpl(false); //False parameter deactivates pipelining
		testInstr = new TransferInstr(Opcode.STORE, 0, 0);
		memory = MemoryModule.getInstance();
		
		
	}

	/*
	 * The result of an instruction fetch is that the IR should contain the instruction held
	 * at the memory address specified by the program counter.
	 * 
	 * PC value 0 by default, so it makes sense to load a test instruction into memory address 0
	 * so that this will be fetched.
	 * 
	 */
	@Test
	public void testInstructionFetch() { //Tests instruction fetch method
		memory.writeInstruction(testInstr, 0);
		controlUnit.instructionFetch();
		Instruction expected = testInstr;
		Instruction output = controlUnit.getIR().read();
		assertEquals(expected, output);		
	}
	
	@Test
	public void testInstructionFetch2() { //Test instruction with PC set to another value
		memory.writeInstruction(testInstr, 19);
		controlUnit.getPC().setPC(19); //Need to manually set PC for testing; this will be set automatically when program loaded
		//via loader into memory; this will send a signal to CPU to set PC to start address.
		controlUnit.instructionFetch();
		Instruction expected = testInstr;
		Instruction output = controlUnit.getIR().read();
		assertEquals(expected, output);		
	}
	
	//A test to see what happens if the fetched item is in fact an Operand and not an Instruction would be useful;
	//implement some exception handling first.
	
	
	/*
	 * Not much to test here; just that the PC is incremented by the end of the method; all this method does
	 * is read the contents of the IR (which has been validated in the fetch test) and extract the integer value of its opcode.
	 * 
	 * Important to note is that instructionDecode() is called by instructionFetch(), so there is no need to explicitly
	 * call it again here.
	 */
	@Test
	public void testInstructionDecode() {
		memory.writeInstruction(testInstr, 0);
		controlUnit.instructionFetch(); //Need to fetch an instruction before decoding
		int expected = 1; //PC starts at 0 by default (unless set otherwise)
		int output = controlUnit.getPC().getValue();
		assertEquals(expected, output);
	}
	

}


//
//public void instructionDecode() { //Returns int value of opcode
//	Instruction instr = ir.read();
//	int opcodeValue = instr.getOpcode().getValue(); //Gets instruction opcode as int value
//	pc.incrementPC(); //Increment PC; done here so that with pipelining, the next instruction can be fetched at this point
//	this.instructionExecute(opcodeValue);
//	
//}
