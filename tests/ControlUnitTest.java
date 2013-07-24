package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import code.*;

public class ControlUnitTest {
	private ControlUnit controlUnit;
	private MainMemory memory;	
	
	private Instruction testInstrSTORE;
	private Instruction testInstrLOAD;
	private Instruction testInstrMOVE;
	
	private Instruction testInstrADD;
	private Instruction testInstrSUB;
	private Instruction testInstrDIV;
	private Instruction testInstrMUL;
	
	

	@Before
	public void setUp() throws Exception {
		memory = MemoryModule.getInstance();
		controlUnit = new ControlUnitImpl(false); //False parameter deactivates pipelining
		
		testInstrSTORE = new TransferInstr(Opcode.STORE, 0, 99); //source r0, destination address 99
		testInstrLOAD = new TransferInstr(Opcode.LOAD, 50, 0); //Load contents of address 50 to register 0
		testInstrMOVE = new TransferInstr(Opcode.MOVE, 0, 15); //Move contents of r0 to r15
		
		testInstrADD = new ArithmeticInstr(Opcode.ADD, 2, 4); //Add contents of r2 and r4, storing in r2
		testInstrSUB = new ArithmeticInstr(Opcode.SUB, 9, 10); //Sub contents of r10 from r9, storing in r9
		
		memory.notify(50, new OperandImpl(1000)); //Load operand (integer) 1000 to memory address 50		
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
		memory.writeInstruction(testInstrSTORE, 0);
		controlUnit.instructionFetch();
		Instruction expected = testInstrSTORE;
		Instruction output = controlUnit.getIR().read();
		assertEquals(expected, output);		
	}
	
	@Test
	public void testInstructionFetch2() { //Test instruction with PC set to another value
		memory.writeInstruction(testInstrSTORE, 19);
		controlUnit.getPC().setPC(19); //Need to manually set PC for testing; this will be set automatically when program loaded
		//via loader into memory; this will send a signal to CPU to set PC to start address.
		controlUnit.instructionFetch();
		Instruction expected = testInstrSTORE;
		Instruction output = controlUnit.getIR().read();
		assertEquals(expected, output);		
	}
	
	//A test to see what happens if the fetched item is in fact an Operand and not an Instruction would be useful;
	//implement some exception handling first.
	
	
	/*
	 * Not much to test here; just that the PC is incremented by the end of the method; all this method does
	 * is read the contents of the IR (which has been validated in the fetch test) and extract the integer value of its opcode,
	 * which is tested in the InstructionTest class.
	 * 
	 * Important to note is that instructionDecode() is called by instructionFetch(), so there is no need to explicitly
	 * call it again here.
	 */
	@Test
	public void testInstructionDecode() {
		memory.writeInstruction(testInstrSTORE, 0);
		controlUnit.instructionFetch(); //Need to fetch an instruction before decoding
		int expected = 1; //PC starts at 0 by default (unless set otherwise)
		int output = controlUnit.getPC().getValue();
		assertEquals(expected, output);
	}
	
	
	/*
	 * A test operand has been loaded into memory address 50 in the setup. 
	 * testInstrLOAD is a LOAD instruction which loads the operand at memory address 50 to register 0
	 * So it must be checked that register 0 contains the operand (1000) contained at address 50, for the successful
	 * execution of a LOAD instruction.
	 */
	@Test
	public void testInstructionExecuteLOAD() { //Test execution of LOAD instruction
		//Load a LOAD instruction into memory address, and prompt a fetch which will call decode, and then execute
		memory.notify(0, testInstrLOAD); //load test instruction into address 0
		controlUnit.instructionFetch(); //This should result in r0 containing 1000; fetch calls decode, which calls execute
		Data dataOutput = controlUnit.getRegisters().read(0);
		Operand output = (Operand) dataOutput;
		assertEquals(1000, output.unwrapInteger());	
	}
	
	@Test
	public void testInstructionExecuteSTORE() { //Test execution of STORE instruction
		Operand operand = new OperandImpl(5000);
		//Firstly, load an operand (5000) into r0
		controlUnit.getRegisters().write(0, operand);
		//Now load a store instruction into memory address 0, ready for fetch
		memory.notify(0, testInstrSTORE);
		controlUnit.instructionFetch(); //Should result in operand 5000 being stored at address 99
		Data expected = operand;
		Data output = memory.accessAddress(99);
		assertEquals(expected, output);		
	}
	
	@Test
	public void testInstructionExecuteMOVE() { //Test execution of MOVE instruction
		Operand operand = new OperandImpl(5000);
		//Load operand to r0
		controlUnit.getRegisters().write(0, operand);
		//Load MOVE instruction to memory address 0
		memory.notify(0, testInstrMOVE);
		//Fetch and execute the instruction; operand should end up in r15 (see testInstrMOVE in setup)
		controlUnit.instructionFetch();
		Data expected = operand;
		Data output = controlUnit.getRegisters().read(15);
		assertEquals(expected, output);
	}
	
	@Test
	public void testInstructionExecuteMOVE2() {//Test execution of MOVE instruction; check source register reset to null after move
		Operand operand = new OperandImpl(5000);
		//Load operand to r0
		controlUnit.getRegisters().write(0, operand);
		//Load MOVE instruction to memory address 0
		memory.notify(0, testInstrMOVE);
		//Fetch and execute the instruction; operand should end up in r15 (see testInstrMOVE in setup)
		controlUnit.instructionFetch();
		assertNull(controlUnit.getRegisters().read(0));//r0 should be null after move of operand to r15
	}
	
	/*
	 * Test for instructionWriteBack() method; this forms the final stage of an arithmetic operation,
	 * so should be tested before instructionExecute() is tested for arithmetic operations.
	 */
	@Test
	public void testInstructionWriteBack() {
		Operand operand = new OperandImpl(400); //This acts as a result operand, pending storage in a register
		//Load an instruction into IR (this instruction stores result in r2
		controlUnit.getIR().loadIR(testInstrADD);
		controlUnit.instructionWriteBack(operand); //This should store the operand in r2
		Data expected = operand;
		Data output = controlUnit.getRegisters().read(2);
		assertEquals(expected, output);
	}
	
	@Test
	public void testInstructionExecuteADD() { //Test execution of ADD instruction
		//testInstrADD = new ArithmeticInstr(Opcode.ADD, 2, 4); //Add contents of r2 and r4, storing in r2
		//Load operands into r2 and r4, for use by testInstrADD detailed in setUp.
		controlUnit.getRegisters().write(2, new OperandImpl(5));
		controlUnit.getRegisters().write(4, new OperandImpl(7));
		//Put the ADD instruction into memory for fetching
		memory.notify(0, testInstrADD);
		controlUnit.instructionFetch();
		int expected = 12; //12 should be present in r2 (5 + 7)
		Operand outputOp = (Operand) controlUnit.getRegisters().read(2);
		int output = outputOp.unwrapInteger();
		assertEquals(expected, output);
	}
	
	@Test
	public void testInstructionExecuteSUB() { //Test execution of SUB instruction
		//Load operands into r9 and r10, for use by testInstrSUB
		controlUnit.getRegisters().write(9, new OperandImpl(50));
		controlUnit.getRegisters().write(10, new OperandImpl(25));
		//Put the SUB instruction into memory for fetching
		memory.notify(0, testInstrSUB);
		controlUnit.instructionFetch();
		int expected = 25; //25 should be present in r9 (50 - 25)
		Operand outputOp = (Operand) controlUnit.getRegisters().read(9);
		int output = outputOp.unwrapInteger();
		assertEquals(expected, output);
	}

}

