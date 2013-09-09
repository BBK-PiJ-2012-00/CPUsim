package tests;

import static org.junit.Assert.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import org.junit.Before;
import org.junit.Test;

import code.ArithmeticInstr;
import code.CPUbuilder;
import code.ControlUnit;
import code.ExecuteStage;
import code.FetchDecodeStage;
import code.IR;
import code.IRfile;
import code.Instruction;
import code.InstructionRegister;
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
import code.StatusRegister;
import code.UpdateListener;
import code.WriteBackStage;

public class WriteBackStageTest {
	private WriteBackStage writeBackStage;
	private WriteBackStage pipelinedWriteBackStage;
	private BlockingQueue<Instruction> testExecuteToWriteQueue;
	
	private InstructionRegister ir;
	private InstructionRegister irFile;
	private RegisterFile genRegisters;
	
	private Instruction testInstrADD;
	private Instruction testInstrSUB;
	private Instruction testInstrDIV;
	private Instruction testInstrMUL;
		

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
		
		ir = new IR();
		ir.registerListener(new UpdateListener(new TestFrame()));
		
		genRegisters = new RegisterFile16();
		genRegisters.registerListener(new UpdateListener(new TestFrame()));
		
		ProgramCounter pc = new PC();
		pc.registerListener(new UpdateListener(new TestFrame()));
		
		Register statusRegister = new StatusRegister();
		statusRegister.registerListener(new UpdateListener(new TestFrame()));	
		
		builder.getMemoryModule().registerListener(new UpdateListener(new TestFrame()));
		
		
		irFile = new IRfile();
		irFile.registerListener(new UpdateListener(new TestFrame()));
		
		BlockingQueue<Instruction> testFetchToExecuteQueue = new SynchronousQueue<Instruction>();
		testExecuteToWriteQueue = new SynchronousQueue<Instruction>(); //Prevents null pointer exception
		
		FetchDecodeStage pipelinedFetchDecodeStage = new PipelinedFetchDecodeStage(builder.getBusController(), irFile, pc, 
				genRegisters, statusRegister, mbr, mar, testFetchToExecuteQueue);
		pipelinedFetchDecodeStage.registerListener(new UpdateListener(new TestFrame()));
		
		ExecuteStage pipelinedExecuteStage = new PipelinedExecuteStage(builder.getBusController(), irFile, pc, genRegisters, 
				statusRegister,	mbr, mar, testFetchToExecuteQueue, testExecuteToWriteQueue, pipelinedFetchDecodeStage, writeBackStage);
		pipelinedExecuteStage.registerListener(new UpdateListener(new TestFrame()));
		
		pipelinedWriteBackStage = new PipelinedWriteBackStage(builder.getBusController(), irFile, pc, genRegisters, statusRegister,
				mbr, mar, testExecuteToWriteQueue);
		
		writeBackStage = new StandardWriteBackStage(builder.getBusController(), ir, pc, genRegisters, statusRegister,
				mbr, mar);	
		
		testInstrADD = new ArithmeticInstr(Opcode.ADD, 2, 4); //Add contents of r2 and r4, storing in r2
		testInstrSUB = new ArithmeticInstr(Opcode.SUB, 9, 10); //Sub contents of r10 from r9, storing in r9
		testInstrDIV = new ArithmeticInstr(Opcode.DIV, 3, 12); //Divide contents of r3 by contents of r12, storing in r3
		testInstrMUL = new ArithmeticInstr(Opcode.MUL, 5, 8); //Multiply contents of r5 by contents of r8, storing in r5
	}

	@Test
	public void testWriteBack() {
		ir.loadIR(testInstrADD); //Load an instruction into IR
		writeBackStage.instructionWriteBack(new OperandImpl(300)); //Supply test operand 300
		//The result of the above line of code is that 300 should be written into r2 (see testInstrADD)
		
		Operand output = (Operand) genRegisters.read(2);
		Operand expected = new OperandImpl(300);
		
		assertEquals(expected, output);
	}
	
	@Test
	public void testReceive() { //Tests receive() method of standard write back stage (as opposed to pipelined), which calls
		//instructionWriteBack() method tested above
		
		ir.loadIR(testInstrSUB); //Load an instruction into IR
		writeBackStage.instructionWriteBack(new OperandImpl(101)); //Supply test operand 300
		//The result of the above line of code is that 300 should be written into r9 (see testInstrSUB)
		
		Operand output = (Operand) genRegisters.read(9);
		Operand expected = new OperandImpl(101);
		
		assertEquals(expected, output);
	}
	
	
	/*
	 * PIPELINED MODE TESTS
	 */
	
	@Test
	public void testPipelinedWriteBack() {
		Thread t = new Thread(pipelinedWriteBackStage);
		t.start();
		pipelinedWriteBackStage.receive(new OperandImpl(22)); //Supply stage with a result value
		try {
			testExecuteToWriteQueue.put(testInstrDIV); //Put test instruction into queue for passing to wbStage
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(500); //Let t run for half a second
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t.interrupt(); //Signal the thread to stop (handle an interrupt)
		while(t.isAlive()); //Wait for thread to die; ensures thread actually dies or assert is never reached
		assertEquals(22, ((Operand) genRegisters.read(3)).unwrapInteger()); //If successful, #22 should be stored in r3.
		
	}

	
	


}
