package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import org.junit.Before;
import org.junit.Test;

import code.ArithmeticInstr;
import code.Assembler;
import code.AssemblerImpl;
import code.BranchInstr;
import code.CPUbuilder;
import code.FetchDecodeStage;
import code.HaltInstr;
import code.IRfile;
import code.Instruction;
import code.InstructionRegister;
import code.MainMemory;
import code.MemoryAddressRegister;
import code.MemoryBufferRegister;
import code.Opcode;
import code.PipelinedFetchDecodeStage;
import code.ProgramCounter;
import code.Register;
import code.RegisterFile;
import code.RegisterFile16;
import code.StandardFetchDecodeStage;
import code.ConditionCodeRegister;
import code.TransferInstr;
import code.UpdateListener;

/*
 * Note that the wait() statements present at certain intervals in the FetchDecodeStage code are commented
 * out for testing (these facilitate the step by step execution on the GUI). Leaving them in would make testing
 * unnecessarily complicated and they can be tested using the GUI.
 * Also, the fireUpdate() methods for event handling should be commented out to make
 * testing simpler (this functionality is tested on the GUI).
 */

public class FetchDecodeStageTest {
	private FetchDecodeStage fetchDecodeStage;
	private FetchDecodeStage pipelinedFDstage;
	private InstructionRegister ir;
	private ProgramCounter pc;
	private RegisterFile genRegisters;
	private Register statusRegister;
	
	private MainMemory memory;	
	
	private Assembler assembler;
	
	private BlockingQueue<Instruction> testFetchToExecuteQueue; //This queue emulates the queue that would normally "sit between" the
	//f/d stage object and execute stage object, and is used to coordinate the threads running in the stages during pipelined
	//execution.
	
	private Instruction testInstrSTORE;
	private Instruction testInstrADD;
	private Instruction testInstrBR;
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
		genRegisters = new RegisterFile16();
		genRegisters.registerListener(new UpdateListener(new TestFrame()));
		statusRegister = new ConditionCodeRegister();
		statusRegister.registerListener(new UpdateListener(new TestFrame()));
		
		builder.getBusController().accessControlLine().getAddressBus().registerListener(new UpdateListener(new TestFrame()));
		builder.getBusController().accessControlLine().getDataBus().registerListener(new UpdateListener(new TestFrame()));
		builder.getBusController().accessControlLine().registerListener(new UpdateListener(new TestFrame()));
		
		
		
		pc = builder.getControlUnit().getPC();
		pc.registerListener(new UpdateListener(new TestFrame()));
		ir = builder.getControlUnit().getIR();
		ir.registerListener(new UpdateListener(new TestFrame()));
		
		fetchDecodeStage = new StandardFetchDecodeStage(builder.getBusController(), ir, pc, genRegisters, statusRegister,
				mbr, mar);
		fetchDecodeStage.registerListener(new UpdateListener(new TestFrame()));
		
		testFetchToExecuteQueue = new SynchronousQueue<Instruction>(); //Allows this to be used in testing
		
		pipelinedFDstage = new PipelinedFetchDecodeStage(builder.getBusController(), new IRfile(), pc, genRegisters, statusRegister,
				mbr, mar, testFetchToExecuteQueue);
		pipelinedFDstage.registerListener(new UpdateListener(new TestFrame()));
		
		memory = builder.getMemoryModule();
		memory.registerListener(new UpdateListener(new TestFrame()));
		
		testInstrSTORE = new TransferInstr(Opcode.STORE, 0, 99); //source r0, destination address 99
		testInstrADD = new ArithmeticInstr(Opcode.ADD, 2, 4); //Add contents of r2 and r4, storing in r2
		testInstrBR = new BranchInstr(Opcode.BR, 10); //Branch to memory address 10
		testInstrHALT = new HaltInstr(Opcode.HALT); //Halt instruction 
		
		assembler = new AssemblerImpl(builder.getLoader());
		assembler.selectFile(new File("src/assemblyPrograms/simpleEquation.txt"));
		assembler.assembleCode();
		assembler.loadToLoader();
		assembler.getLoader().loadToMemory();
		
		//memory.notifyWrite(50, new OperandImpl(1000)); //Load operand (integer) 1000 to memory address 50		
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
		memory.notifyWrite(0, testInstrSTORE);
		fetchDecodeStage.instructionFetch();
		
		Instruction expected = testInstrSTORE;
		Instruction output = ir.read();
		assertEquals(expected, output);		
	}
	
	@Test
	public void testInstructionFetch2() { //Test instruction with PC set to another value
		memory.notifyWrite(19, testInstrSTORE);
		pc.setPC(19); //Need to manually set PC for testing
		fetchDecodeStage.instructionFetch();
		
		Instruction expected = testInstrSTORE;
		Instruction output = ir.read();
		assertEquals(expected, output);		
	}
	

	@Test
	public void testInstructionDecode() { //Test that PC is incremented by the end of the method
		memory.notifyWrite(0, testInstrSTORE);
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
		memory.notifyWrite(0, testInstrBR);
		fetchDecodeStage.instructionFetch(); //Load an instruction into IR
		
		int output = fetchDecodeStage.instructionDecode();		
		int expected = 8; //BR should decode to integer value of 8
		assertEquals(expected, output);
	}
	
	@Test
	public void testInstructionDecode_opcodeValueHALT() { //Test correct opcode value for instruction is returned
		memory.notifyWrite(0, testInstrHALT);
		fetchDecodeStage.instructionFetch(); //Load an instruction into IR
		
		int output = fetchDecodeStage.instructionDecode();		
		int expected = 13; //HALT should decode to integer value of 13
		assertEquals(expected, output);
	}
	
	@Test
	public void testInstructionDecode_opcodeValueADD() { //Test correct opcode value for instruction is returned
		memory.notifyWrite(0, testInstrADD);
		fetchDecodeStage.instructionFetch(); //Load an instruction into IR
		
		int output = fetchDecodeStage.instructionDecode();		
		int expected = 4; //HALT should decode to integer value of 4
		assertEquals(expected, output);
	}
	
	
	/*
	 * PIPELINED FETCH/DECODE STAGE TESTS
	 */
	
	/*
	 * A test to check that interrupting the thread running through the pipelined fetch/decode stage object
	 * dies after being interrupted (not immediately after, but that it ceases executing the fetch and decode
	 * methods).
	 * 
	 * Note that the program simpleEquation.txt is the program to be executed, with its instructions being
	 * fetched and decoded by the f/d stage (this program is loaded into the assembler during setUp() above.
	 */
	@Test
	public void testInterrupt() {
		Thread testThread = new Thread(pipelinedFDstage); //A thread running through pipelined FDstage.
		testThread.start(); //Start the thread, which runs until interrupted (execute stage is responsible for terminating
		Instruction instr;
		int instructionFetchCount = 0; //Record number of instructions fetched
		for (int i = 0; i < 5; i++) { //Allow 5 of the 10 instructions in the file to be fetched
			try {
				instr = testFetchToExecuteQueue.take(); //This thread must take from the queue to enable the f/d stage to continue
				//running; the f/d stage thread waits until another thread (in this case this thread) takes the opcode before fetching
				//the next instruction.
				System.out.println(instr);
				instructionFetchCount++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		testThread.interrupt();
		while(testThread.isAlive()); //The assertEquals statement will never be reached and the test will fail if the thread
									//doesn't terminate (which doesn't happen straight away but the thread should be directed
									//to stop fetching and return from the run() method upon discovery of an interrupt).
		
		assertEquals(5, instructionFetchCount); //Only 5 instructions should have been fetched; after that point, an interrupt
		//was issued and this should halt activity.
		
	}
	
	
	/*
	 * This tests that the pipelined fetch/decode stage object correctly fetches an instruction, decodes it
	 * and passes the correct opcode value into the synchronous queue. The assembler (declared above) is used to supply
	 * a test program (simpleEquation.txt), which contains 10 instruction declarations (operands are ignored here as they
	 * are not relevant to the fetch/decode stage). The opcode of each instruction in the assembly file is compared to
	 * the opcode placed into the queue by the fetch/decode stage.
	 */
	@Test
	public void testQueueUsage() {
		Thread testThread = new Thread(pipelinedFDstage); //A thread running through pipelined FDstage.
		testThread.start(); //Start the thread, which runs until interrupted (execute stage is responsible for terminating
		int opcodeValue = 0; 
		for (int i = 0; i < 10; i ++) { //There are 10 instruction declarations in simpleEquation.txt program used in assembler
			try {
				opcodeValue = testFetchToExecuteQueue.take().getOpcode().getValue(); //Have THIS thread Take from the queue
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Instruction actualInstruction = (Instruction) assembler.getProgramCode()[i];
			assertEquals(actualInstruction.getOpcode().getValue(), opcodeValue); //Compare the opcode of the instruction from the assembly
					//file to the one put into the queue by the fetch/decode stage.
		}
		testThread.interrupt(); //Stops the thread
		while(testThread.isAlive()); //Ensures the thread dies as it should after an interrupt
		System.out.println("Done!"); //This statement will not be reached if the interrupt does not terminate the thread
		//Note that the thread is not strictly terminated but is forced to run to completion without fetching or decoding any more
		//instructions.
	}

}
