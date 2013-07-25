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
	
	private Instruction testInstrBR;
	private Instruction testInstrBRZ;
	private Instruction testInstrSKZ;
	private Instruction testInstrBRE;
	
	

	@Before
	public void setUp() throws Exception {
		memory = MemoryModule.getInstance();
		controlUnit = new ControlUnitImpl(false); //False parameter deactivates pipelining
		
		testInstrSTORE = new TransferInstr(Opcode.STORE, 0, 99); //source r0, destination address 99
		testInstrLOAD = new TransferInstr(Opcode.LOAD, 50, 0); //Load contents of address 50 to register 0
		testInstrMOVE = new TransferInstr(Opcode.MOVE, 0, 15); //Move contents of r0 to r15
		
		testInstrADD = new ArithmeticInstr(Opcode.ADD, 2, 4); //Add contents of r2 and r4, storing in r2
		testInstrSUB = new ArithmeticInstr(Opcode.SUB, 9, 10); //Sub contents of r10 from r9, storing in r9
		testInstrDIV = new ArithmeticInstr(Opcode.DIV, 3, 12); //Divide contents of r3 by contents of r12, storing in r3
		testInstrMUL = new ArithmeticInstr(Opcode.MUL, 5, 8); //Multiply contents of r5 by contents of r8, storing in r5
		
		testInstrBR = new BranchInstr(Opcode.BR, 10); //Branch to memory address 10
		testInstrBRZ = new BranchInstr(Opcode.BRZ, 37); //Branch to memory address 37
		testInstrSKZ = new BranchInstr(Opcode.SKZ); //Skip if zero
		testInstrBRE = new BranchInstr(Opcode.BRE, 92, 1); //Branch to address 92 if contents of r1 equals contents of status reg
		
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
	
	@Test
	public void testInstructionExecuteDIV() { //Test execution of DIV instruction
		//testInstrDIV = new ArithmeticInstr(Opcode.DIV, 3, 12); //Divide contents of r3 by contents of r12, storing in r3
		//Load operands into r3 and r12, for use by testInstrDIV
		controlUnit.getRegisters().write(3, new OperandImpl(40));
		controlUnit.getRegisters().write(12, new OperandImpl(10));
		//Put the DIV instruction into memory for fetching
		memory.notify(0, testInstrDIV);
		controlUnit.instructionFetch();
		
		int expected = 4; //4 should be present in r3 (40 / 10)
		Operand outputOp = (Operand) controlUnit.getRegisters().read(3);
		int output = outputOp.unwrapInteger();
		assertEquals(expected, output);
	}
	
	@Test
	public void testInstructionExecuteMUL() { //Test execution of MUL instruction
		//testInstrMUL = new ArithmeticInstr(Opcode.MUL, 5, 8); //Multiply contents of r5 by contents of r8, storing in r5
		//Load operands into r5 and r8, for use by testInstrMUL
		controlUnit.getRegisters().write(5, new OperandImpl(7));
		controlUnit.getRegisters().write(8, new OperandImpl(11));
		//Put the MUL instruction into memory for fetching
		memory.notify(0, testInstrMUL);
		controlUnit.instructionFetch();
		
		int expected = 77; //77 should be present in r5 (7 * 11)
		Operand outputOp = (Operand) controlUnit.getRegisters().read(5);
		int output = outputOp.unwrapInteger();
		assertEquals(expected, output);
	}
	
	
	@Test
	public void testInstructionExecuteBR() { //Test BR instruction execution
		//testInstrBR = new BranchInstr(Opcode.BR, 10); //Branch to memory address 10
		//Load testInstrBR to memory address 0 for fetching
		memory.notify(0, testInstrBR);
		controlUnit.instructionFetch();
		
		int expected = 10; //Expect PC to now point to memory address 10
		int output = controlUnit.getPC().getValue();
		assertEquals(expected, output);
	}
	
	@Test
	public void testInstructionExecuteBRZ_branchTaken() { //Test BRZ execution
		//testInstrBRZ = new BranchInstr(Opcode.BRZ, 37); //Branch to memory address 37
		memory.notify(0, testInstrBRZ); //Load memory address 0 with branch instruction
		controlUnit.getStatusRegister().write(new OperandImpl(0)); //Set status register to hold 0
		controlUnit.instructionFetch(); //Fetch and execute BRZ instruction
		
		int expected = 37; //PC should hold 37, as the branch should be taken
		int output = controlUnit.getPC().getValue();
		assertEquals(expected, output);
	}
	
	@Test
	public void testInstructionExecuteBRZ_branchNotTaken() { //Test BRZ execution
		//testInstrBRZ = new BranchInstr(Opcode.BRZ, 37); //Branch to memory address 37
		memory.notify(0, testInstrBRZ); //Load memory address 0 with branch instruction
		controlUnit.getStatusRegister().write(new OperandImpl(3)); //Set status register to hold 3
		controlUnit.instructionFetch(); //Fetch and execute BRZ instruction
		
		int expected = 1; //PC 1, as branch should not be taken
		int output = controlUnit.getPC().getValue();
		assertEquals(expected, output);
	}
	
	@Test
	public void testInstructionExecuteSKZ() { //Test SKZ execution
		memory.notify(0, testInstrSKZ); //Load memory address 0 with branch instruction
		controlUnit.getStatusRegister().write(new OperandImpl(0)); //Set status register to hold 0
		controlUnit.instructionFetch(); //Fetch and execute SKZZ instruction
		
		int expected = 2; //PC should = 2, as branch should be taken (meaning PC is incremented)
		int output = controlUnit.getPC().getValue();
		assertEquals(expected, output);
	}
	
	@Test
	public void testInstructionExecuteSKZ_branchNotTaken() { //Test SKZ execution
		memory.notify(0, testInstrSKZ); //Load memory address 0 with branch instruction
		controlUnit.getStatusRegister().write(new OperandImpl(1)); //Set status register to hold 1
		controlUnit.instructionFetch(); //Fetch and execute SKZ instruction
		
		int expected = 1; //PC should = 1, as branch should not be taken
		int output = controlUnit.getPC().getValue();
		assertEquals(expected, output);
	}
	
	@Test
	public void testInstructionExecuteBRE_branchTaken() { //Test BRE execution
		//testInstrBRE = new BranchInstr(Opcode.BRE, 92, 1); //Branch to address 92 if contents of r1 equals contents of status reg
		memory.notify(0, testInstrBRE); //Load memory address 0 with branch instruction
		controlUnit.getStatusRegister().write(new OperandImpl(23)); //Set status register to hold 23
		controlUnit.getRegisters().write(1, new OperandImpl(23)); //Set r1 to hold 23
		controlUnit.instructionFetch();
		
		int expected = 92; //PC should = 92, as branch should be taken
		int output = controlUnit.getPC().getValue();
		assertEquals(expected, output);		
	}
	
	@Test
	public void testInstructionExecuteBRE_branchNotTaken() { //Test BRE execution
		//testInstrBRE = new BranchInstr(Opcode.BRE, 92, 1); //Branch to address 92 if contents of r1 equals contents of status reg
		memory.notify(0, testInstrBRE); //Load memory address 0 with branch instruction
		controlUnit.getStatusRegister().write(new OperandImpl(23)); //Set status register to hold 23
		controlUnit.getRegisters().write(1, new OperandImpl(20)); //Set r1 to hold 20
		controlUnit.instructionFetch();
		
		int expected = 1; //PC should = 1, as branch should not be taken
		int output = controlUnit.getPC().getValue();
		assertEquals(expected, output);
		
	}
	
	
//	//A BRE instruction (branch if status reg. contents = contents of register ref. in instruction)
//	 int genRegRef = ir.read().getField2(); //Reference to register referred to in instruction
//	 if (statusRegister.read().equals((Operand) genRegisters.read(genRegRef))) { //If equal
//		 pc.setPC(ir.read().getField1()); //Set PC to equal address in field1 of instruction in ir						 
//	 }
//	 break; //If not equal, do nothing
	
	
	

}

