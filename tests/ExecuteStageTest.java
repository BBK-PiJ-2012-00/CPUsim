package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import code.ArithmeticInstr;
import code.BranchInstr;
import code.ExecuteStage;
import code.FetchDecodeStage;
import code.HaltInstr;
import code.IR;
import code.Instruction;
import code.InstructionRegister;
import code.MainMemory;
import code.MemoryModule;
import code.Opcode;
import code.OperandImpl;
import code.PC;
import code.ProgramCounter;
import code.Register;
import code.RegisterFile;
import code.StandardFetchDecodeStage;
import code.TransferInstr;
import code.WriteBackStage;

public class ExecuteStageTest {
	private FetchDecodeStage fetchDecodeStage; //Required for fetching of instructions before execution can be tested
	private WriteBackStage writeBackStage; //Required for completion of arithmetic instructions
	private ExecuteStage executeStage;
	
	private InstructionRegister ir;
	private ProgramCounter pc;
	private RegisterFile genRegisters;
	private Register statusRegister;
	
	
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
		pc = new PC();
		ir = new IR();
		
		fetchDecodeStage = new StandardFetchDecodeStage(ir, pc);
		
		memory = MemoryModule.getInstance();
		
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
		
		memory.notify(50, new OperandImpl(1000)); //Load operand (integer) 1000 to memory address 50		
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
