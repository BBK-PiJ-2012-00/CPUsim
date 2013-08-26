package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import code.ArithmeticInstr;
import code.BranchInstr;
import code.BusController;
import code.CPUbuilder;
import code.ControlUnit;
import code.ControlUnitImpl;
import code.FetchDecodeStage;
import code.HaltInstr;
import code.IR;
import code.Instruction;
import code.InstructionRegister;
import code.MAR;
import code.MBR;
import code.MainMemory;
import code.MemoryAddressRegister;
import code.MemoryBufferRegister;
import code.MemoryModule;
import code.Opcode;
import code.OperandImpl;
import code.PC;
import code.ProgramCounter;
import code.StandardFetchDecodeStage;
import code.TransferInstr;
import code.UpdateListener;

/*
 * Better than ControlUnitTest; can test each stage separately and more in-depth.
 */

/*
 * TEST MOVE INSTRUCTION FROM CC TO GEN REG AND VICE VERSA
 */

public class FetchDecodeStageTest {
	private FetchDecodeStage fetchDecodeStage;
	private InstructionRegister ir;
	private ProgramCounter pc;
	
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
	private Instruction testInstrBRNE;
	
	private Instruction testInstrHALT;
	
	

	@Before
	public void setUp() throws Exception {
		/*
		 * Listeners are added to all classes that use a listener to prevent null exceptions 
		 * during testing (serve no functional purpose here).
		 */
		CPUbuilder builder = new CPUbuilder(false);
		MemoryAddressRegister mar = builder.getControlUnit().getMAR();
		mar.registerListener(new UpdateListener(new TestFrame()));
		MemoryBufferRegister mbr = builder.getControlUnit().getMBR();
		mbr.registerListener(new UpdateListener(new TestFrame()));
		
		
		pc = builder.getControlUnit().getPC();
		pc.registerListener(new UpdateListener(new TestFrame()));
		ir = builder.getControlUnit().getIR();
		ir.registerListener(new UpdateListener(new TestFrame()));
		
		
		fetchDecodeStage = new StandardFetchDecodeStage(builder.getBusController(), mar, mbr, ir, pc);
		fetchDecodeStage.registerListener(new UpdateListener(new TestFrame()));
		
		memory = builder.getMemoryModule();
		memory.registerListener(new UpdateListener(new TestFrame()));
		
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
		testInstrBRNE = new BranchInstr(Opcode.BRNE, 77, 6);//Branch to addr. 77 if contents of r6 doesn't equal contents of s. reg.
		
		testInstrHALT = new HaltInstr(Opcode.HALT); //Halt instruction 
		
		memory.notifyWrite(50, new OperandImpl(1000)); //Load operand (integer) 1000 to memory address 50		
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
		fetchDecodeStage.instructionFetch();
		
		Instruction expected = testInstrSTORE;
		Instruction output = ir.read();
		assertEquals(expected, output);		
	}
	
	@Test
	public void testInstructionFetch2() { //Test instruction with PC set to another value
		memory.writeInstruction(testInstrSTORE, 19);
		pc.setPC(19); //Need to manually set PC for testing; this will be set automatically when program loaded
		//via loader into memory; this will send a signal to CPU to set PC to start address.
		fetchDecodeStage.instructionFetch();
		
		Instruction expected = testInstrSTORE;
		Instruction output = ir.read();
		assertEquals(expected, output);		
	}
	
	//A test to see what happens if the fetched item is in fact an Operand and not an Instruction would be useful;
	//implement some exception handling first.
	
	
	@Test
	public void testInstructionDecode() { //Test that PC is incremented by the end of the method
		memory.writeInstruction(testInstrSTORE, 0);
		fetchDecodeStage.instructionFetch(); //Need to fetch an instruction before decoding
		fetchDecodeStage.instructionDecode();
		
		int expected = 1; //PC starts at 0 by default (unless set otherwise)
		int output = pc.getValue();
		assertEquals(expected, output);
	}
	
	/*
	 * Not necessary to test every opcode, as opcode integer values have been tested in InstructionTest.
	 */
	@Test
	public void testInstructionDecode_opcodeValueBR() { //Test correct opcode value for instruction is returned
		memory.writeInstruction(testInstrBR, 0);
		fetchDecodeStage.instructionFetch(); //Load an instruction into IR
		
		int output = fetchDecodeStage.instructionDecode();		
		int expected = 8; //BR should decode to integer value of 8
		assertEquals(expected, output);
	}
	
	@Test
	public void testInstructionDecode_opcodeValueHALT() { //Test correct opcode value for instruction is returned
		memory.writeInstruction(testInstrHALT, 0);
		fetchDecodeStage.instructionFetch(); //Load an instruction into IR
		
		int output = fetchDecodeStage.instructionDecode();		
		int expected = 13; //HALT should decode to integer value of 13
		assertEquals(expected, output);
	}
	
	@Test
	public void testInstructionDecode_opcodeValueADD() { //Test correct opcode value for instruction is returned
		memory.writeInstruction(testInstrADD, 0);
		fetchDecodeStage.instructionFetch(); //Load an instruction into IR
		
		int output = fetchDecodeStage.instructionDecode();		
		int expected = 4; //HALT should decode to integer value of 4
		assertEquals(expected, output);
	}
	
	//Tests for pipelined version to follow once implemented


}
