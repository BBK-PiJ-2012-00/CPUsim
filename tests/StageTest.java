package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import org.junit.Before;
import org.junit.Test;

import code.Assembler;
import code.AssemblerImpl;
import code.CPUbuilder;
import code.ControlUnit;
import code.ExecuteStage;
import code.FetchDecodeStage;
import code.IR;
import code.IRfile;
import code.Instruction;
import code.InstructionRegister;
import code.MainMemory;
import code.MemoryAddressRegister;
import code.MemoryBufferRegister;
import code.Operand;
import code.PC;
import code.PipelinedExecuteStage;
import code.PipelinedFetchDecodeStage;
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

/*
 * Most Stage functionality is tested in tests more specific to each stage; this class is to test
 * that the accessMemory() prevents read/write conflicts with the MAR and MBR registers when the simulator
 * is launched in pipelined mode.  The accessMemory() method is used in pipelined
 * execution to encapsulate operations that require memory access, preventing multiple threads from accessing
 * memory at the same time and thus protecting the integrity of the MAR and MBR registers.
 * 
 * This is tested by using an assembly file that would cause errors if instructions were allowed to be fetched at the same
 * time as LOAD or STORE operations were taking place (errors ranging from null pointers to class cast exceptions 
 * caused by the fetch stage treating operands loaded into the MBR by a STORE or LOAD instruction as instructions), to the wrong 
 * operands ending up in the wrong registers).  This is not a system bus issue but rather
 * stems from, for example, an address being placed into the MAR in order to instigate an instruction
 * fetch, but before the address in the MAR can be passed to the system bus, the execution of a LOAD instruction
 * by the other thread in the execute stage causes an operand address to be written to the MAR.  As a result,
 * an operand is fetched back into the MBR from memory and the fetch stage attempts to treat this as instruction,
 * causing an error.
 * 
 *  The assembly file used with this test consists of multiple successive LOAD and STORE instructions that would cause
 *  conflicts if their execution is not controlled by the accessMemory() method.
 */
public class StageTest {
	private FetchDecodeStage fdStage;
	private ExecuteStage exStage;
	private WriteBackStage wbStage;	
	
	private RegisterFile genRegisters;		
	
	private MainMemory memory;	
	
	private Assembler assembler;
	

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
		
		ProgramCounter pc = new PC();
		pc.registerListener(new UpdateListener(new TestFrame()));
		
		InstructionRegister irFile = new IRfile();
		irFile.registerListener(new UpdateListener(new TestFrame()));
		
		genRegisters = new RegisterFile16();
		genRegisters.registerListener(new UpdateListener(new TestFrame()));
		
		Register statusRegister = new StatusRegister();
		statusRegister.registerListener(new UpdateListener(new TestFrame()));
		
		memory = builder.getMemoryModule();
		memory.registerListener(new UpdateListener(new TestFrame()));
		
		BlockingQueue<Instruction> testFetchToExecuteQueue = new SynchronousQueue<Instruction>();
		BlockingQueue<Operand> testExecuteToWriteQueue = new SynchronousQueue<Operand>();
		
		fdStage = new PipelinedFetchDecodeStage(builder.getBusController(), irFile, pc, genRegisters, 
				statusRegister, mbr, mar, testFetchToExecuteQueue);
		fdStage.registerListener(new UpdateListener(new TestFrame()));
		
		exStage = new PipelinedExecuteStage(builder.getBusController(), irFile, pc, genRegisters, statusRegister, 
				wbStage, mbr, mar, testFetchToExecuteQueue, testExecuteToWriteQueue, fdStage);
		exStage.registerListener(new UpdateListener(new TestFrame()));

		
		assembler = new AssemblerImpl(builder.getLoader());
		assembler.selectFile(new File("src/testAssemblyPrograms/conflictTest.txt"));
		assembler.assembleCode();
		assembler.loadToLoader();
		assembler.getLoader().loadToMemory();
		
		/*
		 * After execution of conflictTest.txt program, the following should be true if no conlicts arise:
		 * r0 should contain value 1
		 * r3 should contain value 22
		 * location1 should hold value 1
		 * r8 should contain value 34
		 * location2 should hold value 22
		 * 
		 * The tests that follow ensure this is the case.
		 */	
		
		
	}

	@Test
	public void testAccessMemoryR0contents() {
		exStage.run();
		
		Operand output = (Operand) genRegisters.read(0);
		int expected = 1;
		
		assertEquals(expected, output.unwrapInteger());		
	}
	
	
	@Test
	public void testAccessMemoryR3contents() {
		exStage.run();
		
		Operand output = (Operand) genRegisters.read(3);
		int expected = 22;
		
		assertEquals(expected, output.unwrapInteger());		
	}
	
	@Test
	public void testAccessMemoryLocation1contents() {
		exStage.run();
		
		int location1 = assembler.getLookupTable().get("location1"); //Look up physical address referred to by location1
		Operand output = (Operand) memory.accessAddress(location1);
		int expected = 1;
		
		assertEquals(expected, output.unwrapInteger());		
	}
	
	
	@Test
	public void testAccessMemoryR8contents() {
		exStage.run();
		
		Operand output = (Operand) genRegisters.read(8);
		int expected = 34;
		
		assertEquals(expected, output.unwrapInteger());		
	}
	
	@Test
	public void testAccessMemoryLocation2contents() {
		exStage.run();
		
		int location2 = assembler.getLookupTable().get("location2"); //Look up physical address referred to by location1
		Operand output = (Operand) memory.accessAddress(location2);
		int expected = 22;
		
		assertEquals(expected, output.unwrapInteger());		
	}

}
