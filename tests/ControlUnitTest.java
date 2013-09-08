package tests;

/*
 * Much of the testing code in this class will be moved and adjusted after the implementation of
 * Stage classes (even if the methods i.e. instructionFetch() remain all but unchanged).
 */

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import code.*;

public class ControlUnitTest {
	private ControlUnit controlUnit;
	private MainMemory memory;
	private Assembler assembler;
	private Loader loader;
	
	private Instruction testInstrLOAD_2;
	private Instruction testInstrLOAD_4;
	private Instruction testInstrSTORE_7;
	
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
		CPUbuilder builder = new CPUbuilder(false); //Create simulator components
		memory = builder.getMemoryModule();
		memory.registerListener(new UpdateListener(new TestFrame()));
		
		/*
		 * Listeners are registered with control unit registers to prevent null pointer exceptions during testing,
		 * although they serve no purpose here.
		 */
		controlUnit = builder.getControlUnit();
		controlUnit.getPC().registerListener(new UpdateListener(new TestFrame())); 
		controlUnit.getIR().registerListener(new UpdateListener(new TestFrame()));
		controlUnit.getRegisters().registerListener(new UpdateListener(new TestFrame()));
		//controlUnit.getStatusRegister().registerListener(new UpdateListener(new TestFrame()));
		controlUnit.getMBR().registerListener(new UpdateListener(new TestFrame()));
		controlUnit.getMAR().registerListener(new UpdateListener(new TestFrame()));
		
		controlUnit.getFetchDecodeStage().registerListener(new UpdateListener(new TestFrame()));
		controlUnit.getExecuteStage().registerListener(new UpdateListener(new TestFrame()));
	
		loader = builder.getLoader();
		assembler = new AssemblerImpl(loader);
		
		
		testInstrLOAD_2 = new TransferInstr(Opcode.LOAD, 5, 2); //Load contents of addr. 5 to r2
		testInstrLOAD_4 = new TransferInstr(Opcode.LOAD, 6, 4); //Load contents of addr. 6 to r4
		testInstrSTORE_7 = new TransferInstr(Opcode.STORE, 2, 7); //Store contents of r2 to addr. 7
		
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
	 * Test non-pipelined execution.
	 */
	@Test
	public void activateTest() { //Test activate() of ControlUnit, which triggers instruction cycle
		memory.notifyWrite(0, testInstrLOAD_2); //Put LOAD instruction into memory addr 0
		memory.notifyWrite(1, testInstrLOAD_4); //Put LOAD instruction into memory addr 1
		memory.notifyWrite(2, testInstrADD); //Load ADD instruction to memory addr 2
		memory.notifyWrite(3, testInstrSTORE_7); //Load STORE instruction to memory addr 3
		memory.notifyWrite(4, testInstrHALT); //Load HALT instruction to memory addr 4
		memory.notifyWrite(5, new OperandImpl(10)); //Load operand 10 to memory address 5
		memory.notifyWrite(6, new OperandImpl(20)); //Load operand 20 to memory address 6
		
		//Result of this instruction cycle should be that a value of 30 is stored in memory address 4
		controlUnit.activate();
		System.out.println("r2: " + controlUnit.getRegisters().read(2));
		for (int i = 0; i < 8; i++) {
			System.out.println(memory.accessAddress(i));
		}
		
		((MemoryModule) memory).display();
		
		Operand output = (Operand) memory.accessAddress(7);
		Operand expected = new OperandImpl(30);
		
		assertEquals(expected, output);
		
	}
	
	
	@Test
	public void fullTest() {
		memory.clearMemory();
		assembler.selectFile(new File("src/assemblyPrograms/assemblerTestProgram2"));
		assembler.assembleCode();
		assembler.loadToLoader();
		((AssemblerImpl) assembler).getLoader().loadToMemory();
		
		
		
		
		controlUnit.activate();
		
		for (int i = 0; i < assembler.getProgramCode().length; i++) {
			System.out.println(memory.accessAddress(i));
			
		}
	}
	
	//Test a branch instruction
	

	

}

