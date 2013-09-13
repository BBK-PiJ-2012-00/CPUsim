package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;


import org.junit.Before;
import org.junit.Test;

import code.ArithmeticInstr;
import code.Assembler;
import code.AssemblerImpl;
import code.BranchInstr;
import code.CPUbuilder;
import code.ControlUnit;
import code.Data;
import code.ExecuteStage;
import code.FetchDecodeStage;
import code.HaltInstr;
import code.IR;
import code.IRfile;
import code.Instruction;
import code.InstructionRegister;
import code.MainMemory;
import code.MemoryAddressRegister;
import code.MemoryBufferRegister;
import code.Opcode;
import code.Operand;
import code.OperandImpl;
import code.PC;
import code.PipelinedExecuteStage;
import code.PipelinedFetchDecodeStage;
import code.PipelinedWriteBackStage;
import code.ProgramCounter;
import code.Register;
import code.RegisterFile;
import code.RegisterFile16;
import code.StandardExecuteStage;
import code.StandardFetchDecodeStage;
import code.StandardWriteBackStage;
import code.ConditionCodeRegister;
import code.TransferInstr;
import code.UpdateListener;
import code.WriteBackStage;

/*
 * In order to be able to test ExecuteStage, the wait() statements need to be commented-out of
 * the class code. Wait() is required to implement step by step execution in the GUI.
 */

public class ExecuteStageTest {
	private FetchDecodeStage fetchDecodeStage; //Required for fetching of instructions before execution can be tested
	private WriteBackStage writeBackStage; //Required for completion of arithmetic instructions
	private ExecuteStage executeStage;
	
	private FetchDecodeStage pipelinedFetchDecodeStage;
	private ExecuteStage pipelinedExecuteStage;
	private WriteBackStage pipelinedWriteBackStage;
	private BlockingQueue<Instruction> testFetchToExecuteQueue;
	private BlockingQueue<Instruction> testExecuteToWriteQueue;
	
	
	private InstructionRegister ir;
	private ProgramCounter pc;
	private RegisterFile genRegisters;
	private Register statusRegister;
	
	
	private MainMemory memory;	
	
	private Assembler assembler;
	
	private Instruction testInstrSTORE;
	private Instruction testInstrLOAD;
	private Instruction testInstrMOVE;
	private Instruction testInstrMOVErCC;
	
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
		ControlUnit controlUnit = builder.getControlUnit(); 
		//MAR/MBR need to be referenced from control unit to ensure no duplicates, which would fail the tests; this is because
		//the MAR/MBR are also referenced by the system bus and the exact same objects must be used throughout.
		MemoryAddressRegister mar = controlUnit.getMAR();
		MemoryBufferRegister mbr = controlUnit.getMBR();
		mbr.registerListener(new UpdateListener(new TestFrame()));
		mar.registerListener(new UpdateListener(new TestFrame()));
		builder.getBusController().accessControlLine().registerListener(new UpdateListener(new TestFrame()));
		builder.getBusController().accessControlLine().getAddressBus().registerListener(new UpdateListener(new TestFrame()));
		builder.getBusController().accessControlLine().getDataBus().registerListener(new UpdateListener(new TestFrame()));
		
		pc = new PC();
		pc.registerListener(new UpdateListener(new TestFrame()));
		
		ir = new IR();
		ir.registerListener(new UpdateListener(new TestFrame()));
		
		genRegisters = new RegisterFile16();
		genRegisters.registerListener(new UpdateListener(new TestFrame()));
		
		statusRegister = new ConditionCodeRegister();
		statusRegister.registerListener(new UpdateListener(new TestFrame()));
		
		
		fetchDecodeStage = new StandardFetchDecodeStage(builder.getBusController(), ir, pc, genRegisters, statusRegister,
				mbr, mar);
		fetchDecodeStage.registerListener(new UpdateListener(new TestFrame()));
		writeBackStage = new StandardWriteBackStage(builder.getBusController(), ir, pc, genRegisters, statusRegister, mbr, mar);
		executeStage = new StandardExecuteStage(builder.getBusController(),ir, pc, genRegisters,
				statusRegister, mbr, mar, writeBackStage);
		executeStage.registerListener(new UpdateListener(new TestFrame()));
		
		memory = builder.getMemoryModule();
		memory.registerListener(new UpdateListener(new TestFrame()));
		
		
		InstructionRegister irFile = new IRfile();
		irFile.registerListener(new UpdateListener(new TestFrame()));
		
		testFetchToExecuteQueue = new SynchronousQueue<Instruction>();
		testExecuteToWriteQueue = new SynchronousQueue<Instruction>(); //Prevents null pointer exception
		
		pipelinedFetchDecodeStage = new PipelinedFetchDecodeStage(builder.getBusController(), irFile, pc, genRegisters, 
				statusRegister, mbr, mar, testFetchToExecuteQueue);
		pipelinedFetchDecodeStage.registerListener(new UpdateListener(new TestFrame()));
		
		pipelinedWriteBackStage = new PipelinedWriteBackStage(builder.getBusController(), irFile, pc, genRegisters, 
				statusRegister, mbr, mar, testExecuteToWriteQueue);
		
		pipelinedExecuteStage = new PipelinedExecuteStage(builder.getBusController(), irFile, pc, genRegisters, statusRegister, 
				mbr, mar, testFetchToExecuteQueue, testExecuteToWriteQueue, pipelinedFetchDecodeStage, pipelinedWriteBackStage);
		pipelinedExecuteStage.registerListener(new UpdateListener(new TestFrame()));
		
		testInstrSTORE = new TransferInstr(Opcode.STORE, 0, 99); //source r0, destination address 99
		testInstrLOAD = new TransferInstr(Opcode.LOAD, 50, 0); //Load contents of address 50 to register 0
		testInstrMOVE = new TransferInstr(Opcode.MOVE, 0, 15); //Move contents of r0 to r15
		testInstrMOVErCC = new TransferInstr(Opcode.MOVE, 0, 16); //Move contents of r0 to rCC (referenced by 16)
		
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
		
		//Load an assembly file into memory to use for testing
		assembler = new AssemblerImpl(builder.getLoader());
		
	}


	@Test
	public void testForward() { //Tests forward() method (forwards result of arithmetic operations to write back)
		ir.loadIR(testInstrDIV); //Load instruction to IR (should store operand in r3)
		executeStage.forward(new OperandImpl(300));
		
		Operand output = (Operand) genRegisters.read(3); //300 should be in r3 as result of calling forward()
		Operand expected = new OperandImpl(300);
		
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
		memory.notifyWrite(0, testInstrLOAD); //load test instruction into address 0
		fetchDecodeStage.instructionFetch(); //This should result in r0 containing 1000
		int opcode = fetchDecodeStage.instructionDecode(); //Returns opcode value, to be passed to execute stage
		executeStage.instructionExecute(opcode);
		
		Operand output = (Operand) genRegisters.read(0);
		Operand expected = new OperandImpl(1000);
		assertEquals(expected, output);	
	}
	
	
	@Test
	public void testInstructionExecuteSTORE() { //Test execution of STORE instruction
		Operand operand = new OperandImpl(5000);
		//Firstly, load an operand (5000) into r0
		genRegisters.write(0, operand);
		//Now load a store instruction into memory address 0, ready for fetch
		memory.notifyWrite(0, testInstrSTORE);
		fetchDecodeStage.instructionFetch();
		int opcode = fetchDecodeStage.instructionDecode();
		executeStage.instructionExecute(opcode);
		//Should result in operand 5000 being stored at address 99
		
		Data expected = operand;
		Data output = memory.accessAddress(99);
		assertEquals(expected, output);		
	}
	
	
	@Test
	public void testInstructionExecuteMOVE() { //Test execution of MOVE instruction
		Operand operand = new OperandImpl(5000);
		//Load operand to r0
		genRegisters.write(0, operand);
		//Load MOVE instruction to memory address 0
		memory.notifyWrite(0, testInstrMOVE);
		//Fetch and execute the instruction; operand should end up in r15 (see testInstrMOVE in setup)
		fetchDecodeStage.instructionFetch();
		int opcode = fetchDecodeStage.instructionDecode();
		executeStage.instructionExecute(opcode);
		
		Operand expected = operand;
		Operand output = (Operand) genRegisters.read(15);
		assertEquals(expected, output);
	}
	
	
	@Test
	public void testInstructionExecuteMOVE2() { //Tests moving from register to condition code register
		Operand operand = new OperandImpl(5000);
		//Load operand to r0
		genRegisters.write(0, operand);
		//Load MOVE instruction to memory address 0
		memory.notifyWrite(0, testInstrMOVErCC);
		//Fetch and execute the instruction; operand should end up in rCC (see testInstrMOVErCC in setup)
		fetchDecodeStage.instructionFetch();
		int opcode = fetchDecodeStage.instructionDecode();
		executeStage.instructionExecute(opcode);
		
		assertEquals(operand, statusRegister.read());//r0 should be null after move of operand to r15
	}
	
	
	
	@Test
	public void testInstructionExecuteADD() { //Test execution of ADD instruction
		//testInstrADD -> Add contents of r2 and r4, storing in r2
		//Load operands into r2 and r4, for use by testInstrADD detailed in setUp.
		genRegisters.write(2, new OperandImpl(5));
		genRegisters.write(4, new OperandImpl(7));
		//Put the ADD instruction into memory for fetching
		memory.notifyWrite(0, testInstrADD);
		fetchDecodeStage.instructionFetch();
		int opcode = fetchDecodeStage.instructionDecode();
		
		executeStage.instructionExecute(opcode);
		
		Operand expected = new OperandImpl(12); //12 should be present in r2 (5 + 7)
		Operand output = (Operand) genRegisters.read(2);
		assertEquals(expected, output);
	}
	
	
	@Test
	public void testInstructionExecuteSUB() { //Test execution of SUB instruction
		//Load operands into r9 and r10, for use by testInstrSUB
		genRegisters.write(9, new OperandImpl(50));
		genRegisters.write(10, new OperandImpl(25));
		//Put the SUB instruction into memory for fetching
		memory.notifyWrite(0, testInstrSUB);
		fetchDecodeStage.instructionFetch();
		int opcode = fetchDecodeStage.instructionDecode();
		
		executeStage.instructionExecute(opcode);
		
		Operand expected = new OperandImpl(25); //25 should be present in r9 (50 - 25)
		Operand output = (Operand) genRegisters.read(9);
		assertEquals(expected, output);
	}
	
	
	@Test
	public void testInstructionExecuteDIV() { //Test execution of DIV instruction
		//testInstrDIV -> Divide contents of r3 by contents of r12, storing in r3
		//Load operands into r3 and r12, for use by testInstrDIV
		genRegisters.write(3, new OperandImpl(40));
		genRegisters.write(12, new OperandImpl(10));
		//Put the DIV instruction into memory for fetching
		memory.notifyWrite(0, testInstrDIV);
		fetchDecodeStage.instructionFetch();
		int opcode = fetchDecodeStage.instructionDecode();
		
		executeStage.instructionExecute(opcode);
		
		Operand expected = new OperandImpl(4); //4 should be present in r3 (40 / 10)
		Operand output = (Operand) genRegisters.read(3);
		assertEquals(expected, output);
	}
	
	
	@Test
	public void testInstructionExecuteMUL() { //Test execution of MUL instruction
		//testInstrMUL -> Multiply contents of r5 by contents of r8, storing in r5
		//Load operands into r5 and r8, for use by testInstrMUL
		genRegisters.write(5, new OperandImpl(7));
		genRegisters.write(8, new OperandImpl(11));
		//Put the MUL instruction into memory for fetching
		memory.notifyWrite(0, testInstrMUL);
		fetchDecodeStage.instructionFetch();
		int opcode = fetchDecodeStage.instructionDecode();
		
		executeStage.instructionExecute(opcode);
		
		Operand expected = new OperandImpl(77); //77 should be present in r5 (7 * 11)
		Operand output = (Operand) genRegisters.read(5);
		assertEquals(expected, output);
	}
	
	
	@Test
	public void testInstructionExecuteBR() { //Test BR instruction execution
		//testInstrBR -> Branch to memory address 10
		//Load testInstrBR to memory address 0 for fetching
		memory.notifyWrite(0, testInstrBR);
		fetchDecodeStage.instructionFetch();
		int opcode = fetchDecodeStage.instructionDecode();
		
		executeStage.instructionExecute(opcode);
		
		int expected = 10; //Expect PC to now point to memory address 10
		int output = pc.getValue();
		assertEquals(expected, output);
	}
	
	
	@Test
	public void testInstructionExecuteBRZ_branchTaken() { //Test BRZ execution
		//testInstrBRZ -> Branch to memory address 37
		memory.notifyWrite(0, testInstrBRZ); //Load memory address 0 with branch instruction
		statusRegister.write(new OperandImpl(0)); //Set status register to hold 0

		fetchDecodeStage.instructionFetch();
		int opcode = fetchDecodeStage.instructionDecode();
		
		executeStage.instructionExecute(opcode);
		
		int expected = 37; //PC should hold 37, as the branch should be taken
		int output = pc.getValue();
		assertEquals(expected, output);
	}
	
	
	@Test
	public void testInstructionExecuteBRZ_branchNotTaken() { //Test BRZ execution
		//testInstrBRZ -> Branch to memory address 37
		memory.notifyWrite(0, testInstrBRZ); //Load memory address 0 with branch instruction
		statusRegister.write(new OperandImpl(3)); //Set status register to hold 3
		fetchDecodeStage.instructionFetch();
		int opcode = fetchDecodeStage.instructionDecode();
		
		executeStage.instructionExecute(opcode);
		
		int expected = 1; //PC 1, as branch should not be taken
		int output = pc.getValue();
		assertEquals(expected, output);
	}
	
	
	@Test
	public void testInstructionExecuteSKZ() { //Test SKZ execution
		memory.notifyWrite(0, testInstrSKZ); //Load memory address 0 with branch instruction
		statusRegister.write(new OperandImpl(0)); //Set status register to hold 0
		fetchDecodeStage.instructionFetch(); //Fetch and execute SKZ instruction
		int opcode = fetchDecodeStage.instructionDecode();
		
		executeStage.instructionExecute(opcode);
		
		int expected = 2; //PC should = 2, as branch should be taken (meaning PC is incremented)
		int output = pc.getValue();
		assertEquals(expected, output);
	}
	
	@Test
	public void testInstructionExecuteSKZ_branchNotTaken() { //Test SKZ execution
		memory.notifyWrite(0, testInstrSKZ); //Load memory address 0 with branch instruction
		statusRegister.write(new OperandImpl(1)); //Set status register to hold 1
		fetchDecodeStage.instructionFetch(); //Fetch and execute SKZ instruction
		int opcode = fetchDecodeStage.instructionDecode();
		
		executeStage.instructionExecute(opcode);
		
		int expected = 1; //PC should = 1, as branch should not be taken
		int output = pc.getValue();
		assertEquals(expected, output);
	}
	
	
	@Test
	public void testInstructionExecuteBRE_branchTaken() { //Test BRE execution
		//testInstrBRE -> Branch to address 92 if contents of r1 equals contents of status reg
		memory.notifyWrite(0, testInstrBRE); //Load memory address 0 with branch instruction
		statusRegister.write(new OperandImpl(23)); //Set status register to hold 23
		genRegisters.write(1, new OperandImpl(23)); //Set r1 to hold 23
		fetchDecodeStage.instructionFetch(); //Fetch and execute BRE instruction
		int opcode = fetchDecodeStage.instructionDecode();
		
		executeStage.instructionExecute(opcode);
		
		int expected = 92; //PC should = 92, as branch should be taken
		int output = pc.getValue();
		assertEquals(expected, output);		
	}
	
	
	@Test
	public void testInstructionExecuteBRE_branchNotTaken() { //Test BRE execution
		//testInstrBRE -> Branch to address 92 if contents of r1 equals contents of status reg
		memory.notifyWrite(0, testInstrBRE); //Load memory address 0 with branch instruction
		statusRegister.write(new OperandImpl(23)); //Set status register to hold 23
		genRegisters.write(1, new OperandImpl(20)); //Set r1 to hold 20
		fetchDecodeStage.instructionFetch(); //Fetch and execute BRE instruction
		int opcode = fetchDecodeStage.instructionDecode();
		
		executeStage.instructionExecute(opcode);
		
		int expected = 1; //PC should = 1, as branch should not be taken
		int output = pc.getValue();
		assertEquals(expected, output);		
	}	
	
	
	
	@Test
	public void testInstructionExecuteBRNE_branchTaken() { //Test BRNE execution
		//testInstrBRNE -> Branch to addr. 77 if contents of r6 doesn't equal contents of s. reg.
		memory.notifyWrite(0, testInstrBRNE); //Load test Instr to address 0
		statusRegister.write(new OperandImpl(14)); //Set status register to hold 14
		genRegisters.write(6, new OperandImpl(11)); //Set r6 to hold 11
		fetchDecodeStage.instructionFetch(); //Fetch and execute BRNE instruction
		int opcode = fetchDecodeStage.instructionDecode();
		
		executeStage.instructionExecute(opcode);
		
		int expected = 77; //PC should hold 77 (branch should be taken)
		int output = pc.getValue();
		assertEquals(expected, output);
	}
	
	
	@Test
	public void testInstructionExecuteBRNE_branchNotTaken() { //Test BRNE execution
		//testInstrBRNE -> Branch to addr. 77 if contents of r6 doesn't equal contents of s. reg.
		memory.notifyWrite(0, testInstrBRNE); //Load test Instr to address 0
		statusRegister.write(new OperandImpl(14)); //Set status register to hold 14
		genRegisters.write(6, new OperandImpl(14)); //Set r6 to hold 14
		fetchDecodeStage.instructionFetch(); //Fetch and execute BRNE instruction
		int opcode = fetchDecodeStage.instructionDecode();
		
		executeStage.instructionExecute(opcode);
		
		int expected = 1; //PC should hold 1 (branch should not be taken)
		int output = pc.getValue();
		assertEquals(expected, output);
	}
	
	
	@Test
	public void testInstructionHALT_returnsFalse() { //Check HALT instruction returns false
		memory.notifyWrite(0, testInstrHALT);
		fetchDecodeStage.instructionFetch(); //Fetch and execute HALT instruction
		int opcode = fetchDecodeStage.instructionDecode();
		
		boolean result = executeStage.instructionExecute(opcode);
		
		assertFalse(result);
	}
	
	//Test loading condition code register
	@Test
	public void testInstructionLOAD_statusRegister() {
		memory.notifyWrite(0, new TransferInstr(Opcode.LOAD, 50, 16)); //16 refers to status register
		fetchDecodeStage.instructionFetch();
		int opcode = fetchDecodeStage.instructionDecode();
		executeStage.instructionExecute(opcode);
		
		Operand expected = new OperandImpl(1000); //Value of operand stored in memory address 50
		Operand output = statusRegister.read();
		
		assertEquals(expected, output);
	}
	
	
	/*
	 * PIPELINED EXECUTE STAGE TESTS
	 */
	
	/*
	 * This tests that a BR instruction correctly flushes the pipeline (the f/d stage). The test
	 * file (testBRflush.txt) contains a BR instruction, meaning that the LOAD instruction directly
	 * following it should not be executed. This LOAD instruction puts the value #33 from memory into
	 * r1, and if the pipeline is flushed correctly, the contents of r1 should be null at the end of
	 * the program.
	 */
	@Test
	public void testFlushBR() { //Tests f/d stage is flushed when BR instruction encountered.	
		assembler.selectFile(new File("src/testAssemblyPrograms/testBRflush.txt"));
		assembler.assembleCode();
		assembler.loadToLoader();
		assembler.getLoader().loadToMemory();
		
		pipelinedExecuteStage.run(); 		
		Data genRegistersR1 = pipelinedExecuteStage.getGenRegisters().read(1); //Retrieve contents of r1
		Operand r1Contents = (Operand) genRegistersR1; //Cast to operand
		assertNull(r1Contents); //r1 contents should be null, as the BR instruction should ensure the operand value #33
								//is never loaded into r1 via LOAD instruction 
	}
	
	
	/*
	 * This tests that the branch target of a BR instruction is correctly executed; in this case, the 
	 * target is a LOAD instruction that places #12 into r5.
	 */
	@Test
	public void testFlushBR2() { //Checks that branch target is executed
		assembler.selectFile(new File("src/testAssemblyPrograms/testBRflush.txt"));
		assembler.assembleCode();
		assembler.loadToLoader();
		assembler.getLoader().loadToMemory();
		
		pipelinedExecuteStage.run(); 	
		Data genRegistersR5 = pipelinedExecuteStage.getGenRegisters().read(5); //Retrieve contents of r5
		Operand r5Contents = (Operand) genRegistersR5; //Cast to operand
		assertEquals(12, r5Contents.unwrapInteger()); //Check r5 contains #12
	}
	
	
	@Test
	public void testFlushBRZ() {
		assembler.selectFile(new File("src/testAssemblyPrograms/testBRZflush.txt"));
		assembler.assembleCode();
		assembler.loadToLoader();
		assembler.getLoader().loadToMemory();
		
		pipelinedExecuteStage.run(); 		
		Data genRegistersR1 = pipelinedExecuteStage.getGenRegisters().read(1); //Retrieve contents of r1
		Operand r1Contents = (Operand) genRegistersR1; //Cast to operand
		assertNull(r1Contents); //r1 contents should be null, as the BRZ instruction should ensure the operand value 33
								//is never loaded into r1 via LOAD instruction 	
		
	}
	
	
	@Test
	public void testFlushBRZ2() { //Checks branch target is executed
		assembler.selectFile(new File("src/testAssemblyPrograms/testBRZflush.txt"));
		assembler.assembleCode();
		assembler.loadToLoader();
		assembler.getLoader().loadToMemory();
		
		pipelinedExecuteStage.run(); 		
		Data genRegistersR5 = pipelinedExecuteStage.getGenRegisters().read(5); //Retrieve contents of r5
		Operand r5Contents = (Operand) genRegistersR5; //Cast to operand
		assertEquals(112, r5Contents.unwrapInteger()); //Check r5 contains #112
	}
	

	@Test
	public void testFlushSKZ() {
		assembler.selectFile(new File("src/testAssemblyPrograms/testSKZflush.txt"));
		assembler.assembleCode();
		assembler.loadToLoader();
		assembler.getLoader().loadToMemory();
		
		pipelinedExecuteStage.run(); 		
		Data genRegistersR4 = pipelinedExecuteStage.getGenRegisters().read(4); //Retrieve contents of r1
		Operand r4Contents = (Operand) genRegistersR4; //Cast to operand
		assertNull(r4Contents); //r4 contents should be null, as the SKZ instruction should ensure the operand value #55
								//is never loaded into r4 via LOAD instruction 	
		
	}
	
	
	@Test
	public void testFlushSKZ2() { //Checks instruction after skipped instruction is executed
		assembler.selectFile(new File("src/testAssemblyPrograms/testSKZflush.txt"));
		assembler.assembleCode();
		assembler.loadToLoader();
		assembler.getLoader().loadToMemory();
		
		System.out.println("ASSEMBLER CONTS.: " + Arrays.toString(assembler.getProgramCode()));
		
		
		pipelinedExecuteStage.run(); 		
		Data genRegistersR7 = pipelinedExecuteStage.getGenRegisters().read(7); //Retrieve contents of r5
		Operand r7Contents = (Operand) genRegistersR7; //Cast to operand
		assertEquals(117, r7Contents.unwrapInteger()); //Check r5 contains #12
		
	}
	
	@Test
	public void testFlushBRE() {
		assembler.selectFile(new File("src/testAssemblyPrograms/testBREflush.txt"));
		assembler.assembleCode();
		assembler.loadToLoader();
		assembler.getLoader().loadToMemory();
		
		pipelinedExecuteStage.run(); 		
		Data genRegistersR1 = pipelinedExecuteStage.getGenRegisters().read(1); //Retrieve contents of r1
		Operand r1Contents = (Operand) genRegistersR1; //Cast to operand
		assertNull(r1Contents); //r1 contents should be null, as the BRE instruction should ensure the operand value #44
								//is never loaded into r1 via LOAD instruction 	
		
	}
	
	
	@Test
	public void testFlushBRE2() { //Checks branch target is executed
		assembler.selectFile(new File("src/testAssemblyPrograms/testBREflush.txt"));
		assembler.assembleCode();
		assembler.loadToLoader();
		assembler.getLoader().loadToMemory();		
		
		pipelinedExecuteStage.run(); 		
		Data genRegistersR5 = pipelinedExecuteStage.getGenRegisters().read(5); //Retrieve contents of r5
		Operand r5Contents = (Operand) genRegistersR5; //Cast to operand
		assertEquals(112, r5Contents.unwrapInteger()); //Check r5 contains #112
		
	}
	
	@Test
	public void testFlushBRNE() {
		assembler.selectFile(new File("src/testAssemblyPrograms/testBRNEflush.txt"));
		assembler.assembleCode();
		assembler.loadToLoader();
		assembler.getLoader().loadToMemory();
		
		pipelinedExecuteStage.run(); 		
		Data genRegistersR15 = pipelinedExecuteStage.getGenRegisters().read(15); //Retrieve contents of r15
		Operand r15Contents = (Operand) genRegistersR15; //Cast to operand
		assertNull(r15Contents); //r15 contents should be null, as the BRNE instruction should ensure the operand value #99
								//is never loaded into r15 via LOAD instruction 	
		
	}
	
	
	@Test
	public void testFlushBRNE2() { //Checks branch target is executed
		assembler.selectFile(new File("src/testAssemblyPrograms/testBRNEflush.txt"));
		assembler.assembleCode();
		assembler.loadToLoader();
		assembler.getLoader().loadToMemory();		
		
		pipelinedExecuteStage.run(); 		
		Data genRegistersR11 = pipelinedExecuteStage.getGenRegisters().read(11); //Retrieve contents of r11
		Operand r11Contents = (Operand) genRegistersR11; //Cast to operand
		assertEquals(88, r11Contents.unwrapInteger()); //Check r11 contains #88
		
	}
	
	//Note that a test file incorporating all kinds of instruction is tested in the control unit test class
	//to ensure that all stages work together properly.
	//Also, the same code is used in both pipelined and standard version to execute instruction types, so
	//re-testing these individually is not necessary.



}

