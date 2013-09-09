package tests;

/*
 * This tests that the stages (both pipelined and standard) work as expected when used via the 
 * control unit class, as will be the case in the finished simulator. Note that the wait() statements
 * must be commented out/removed from all Stage classes and the system bus classes in order to run
 * these tests.
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
	
	private ControlUnit pControlUnit; //Pipelined versions
	private MainMemory pMemory;
	private Assembler pAssembler;
	
	

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
		controlUnit.getMBR().registerListener(new UpdateListener(new TestFrame()));
		controlUnit.getMAR().registerListener(new UpdateListener(new TestFrame()));
		controlUnit.getStatusRegister().registerListener(new UpdateListener(new TestFrame()));
		
		controlUnit.getFetchDecodeStage().registerListener(new UpdateListener(new TestFrame()));
		controlUnit.getExecuteStage().registerListener(new UpdateListener(new TestFrame()));
		
		builder.getBusController().accessControlLine().registerListener(new UpdateListener(new TestFrame()));
		builder.getBusController().accessControlLine().getAddressBus().registerListener(new UpdateListener(new TestFrame()));
		builder.getBusController().accessControlLine().getDataBus().registerListener(new UpdateListener(new TestFrame()));
		
		
		/*
		 * For pipelined execution
		 */
		CPUbuilder pBuilder = new CPUbuilder(true); //Create simulator components
		pMemory = pBuilder.getMemoryModule();
		pMemory.registerListener(new UpdateListener(new TestFrame()));
		
		/*
		 * Listeners are registered with control unit registers to prevent null pointer exceptions during testing,
		 * although they serve no purpose here.
		 */
		pControlUnit = pBuilder.getControlUnit();
		pControlUnit.getPC().registerListener(new UpdateListener(new TestFrame())); 
		pControlUnit.getIR().registerListener(new UpdateListener(new TestFrame()));
		pControlUnit.getRegisters().registerListener(new UpdateListener(new TestFrame()));
		pControlUnit.getMBR().registerListener(new UpdateListener(new TestFrame()));
		pControlUnit.getMAR().registerListener(new UpdateListener(new TestFrame()));
		pControlUnit.getStatusRegister().registerListener(new UpdateListener(new TestFrame()));
		
		pControlUnit.getFetchDecodeStage().registerListener(new UpdateListener(new TestFrame()));
		pControlUnit.getExecuteStage().registerListener(new UpdateListener(new TestFrame()));
		
		pBuilder.getBusController().accessControlLine().registerListener(new UpdateListener(new TestFrame()));
		pBuilder.getBusController().accessControlLine().getAddressBus().registerListener(new UpdateListener(new TestFrame()));
		pBuilder.getBusController().accessControlLine().getDataBus().registerListener(new UpdateListener(new TestFrame()));
	
		
		assembler = new AssemblerImpl(builder.getLoader());
		pAssembler = new AssemblerImpl(pBuilder.getLoader());
		
		

	}
	
	
	
	/*
	 * Executes the fullProgramTest file in standard execution mode. After executing this file
	 * the following should be true:
	 * r12 holds value 4
	 * r14 holds value 10
	 * r6 holds value 30
	 * r7 holds value 2
	 * rCC holds value 0
	 * r5 holds null
	 * Memory address referenced by storePoint in file holds value 4.
	 * 
	 * The following seven tests assert that this information is true.
	 */
	@Test
	public void fullTestStandard1() {
		memory.clearMemory();
		assembler.selectFile(new File("src/testAssemblyPrograms/fullProgramTest.txt"));
		assembler.assembleCode();
		assembler.loadToLoader();
		assembler.getLoader().loadToMemory();		
		
		controlUnit.activate();
		
		int result = ((Operand) controlUnit.getRegisters().read(12)).unwrapInteger();
		
		assertEquals(4, result);
	}
	
	@Test
	public void fullTestStandard2() {
		memory.clearMemory();
		assembler.selectFile(new File("src/testAssemblyPrograms/fullProgramTest.txt"));
		assembler.assembleCode();
		assembler.loadToLoader();
		assembler.getLoader().loadToMemory();		
		
		controlUnit.activate();
		
		int result = ((Operand) controlUnit.getRegisters().read(14)).unwrapInteger();
		
		assertEquals(10, result);
	}
	
	@Test
	public void fullTestStandard3() {
		memory.clearMemory();
		assembler.selectFile(new File("src/testAssemblyPrograms/fullProgramTest.txt"));
		assembler.assembleCode();
		assembler.loadToLoader();
		assembler.getLoader().loadToMemory();		
		
		controlUnit.activate();
		
		int result = ((Operand) controlUnit.getRegisters().read(6)).unwrapInteger();
		
		assertEquals(30, result);
	}
	
	@Test
	public void fullTestStandard4() {
		memory.clearMemory();
		assembler.selectFile(new File("src/testAssemblyPrograms/fullProgramTest.txt"));
		assembler.assembleCode();
		assembler.loadToLoader();
		assembler.getLoader().loadToMemory();		
		
		controlUnit.activate();
		
		int result = ((Operand) controlUnit.getRegisters().read(7)).unwrapInteger();
		
		assertEquals(2, result);
	}
	
	
	@Test
	public void fullTestStandard5() {
		memory.clearMemory();
		assembler.selectFile(new File("src/testAssemblyPrograms/fullProgramTest.txt"));
		assembler.assembleCode();
		assembler.loadToLoader();
		assembler.getLoader().loadToMemory();		
		
		controlUnit.activate();
		
		int result = ((Operand) controlUnit.getStatusRegister().read()).unwrapInteger();
		
		assertEquals(0, result);
	}
	
	@Test
	public void fullTestStandard6() {
		memory.clearMemory();
		assembler.selectFile(new File("src/testAssemblyPrograms/fullProgramTest.txt"));
		assembler.assembleCode();
		assembler.loadToLoader();
		assembler.getLoader().loadToMemory();		
		
		controlUnit.activate();
		
		Operand result = (Operand) controlUnit.getRegisters().read(5);
		
		assertNull(result);
	}
	
	@Test
	public void fullTestStandard7() {
		memory.clearMemory();
		assembler.selectFile(new File("src/testAssemblyPrograms/fullProgramTest.txt"));
		assembler.assembleCode();
		assembler.loadToLoader();
		assembler.getLoader().loadToMemory();		
		
		controlUnit.activate();
		
		Data dataResult = memory.accessAddress(assembler.getLookupTable().get("storePoint"));
		int result = ((Operand) dataResult).unwrapInteger();
		
		assertEquals(4, result);
	}
	
	
	/*
	 * The following tests repeat the previous 7, only in pipelined execution.
	 */
	
	@Test
	public void fullTestStandard1Pipelined() {
		pMemory.clearMemory();
		pAssembler.selectFile(new File("src/testAssemblyPrograms/fullProgramTest.txt"));
		pAssembler.assembleCode();
		pAssembler.loadToLoader();
		pAssembler.getLoader().loadToMemory();		
		
		pControlUnit.activate();
		
		int result = ((Operand) pControlUnit.getRegisters().read(12)).unwrapInteger();
		
		assertEquals(4, result);
	}
	
	@Test
	public void fullTestStandard2Pipelined() {
		pMemory.clearMemory();
		pAssembler.selectFile(new File("src/testAssemblyPrograms/fullProgramTest.txt"));
		pAssembler.assembleCode();
		pAssembler.loadToLoader();
		pAssembler.getLoader().loadToMemory();		
		
		pControlUnit.activate();
		
		int result = ((Operand) pControlUnit.getRegisters().read(14)).unwrapInteger();
		
		assertEquals(10, result);
	}
	
	@Test
	public void fullTestStandard3Pipelined() {
		pMemory.clearMemory();
		pAssembler.selectFile(new File("src/testAssemblyPrograms/fullProgramTest.txt"));
		pAssembler.assembleCode();
		pAssembler.loadToLoader();
		pAssembler.getLoader().loadToMemory();		
		
		pControlUnit.activate();
		
		int result = ((Operand) pControlUnit.getRegisters().read(6)).unwrapInteger();
		
		assertEquals(30, result);
	}
	
	@Test
	public void fullTestStandard4Pipelined() {
		pMemory.clearMemory();
		pAssembler.selectFile(new File("src/testAssemblyPrograms/fullProgramTest.txt"));
		pAssembler.assembleCode();
		pAssembler.loadToLoader();
		pAssembler.getLoader().loadToMemory();		
		
		pControlUnit.activate();
		
		int result = ((Operand) pControlUnit.getRegisters().read(7)).unwrapInteger();
		
		assertEquals(2, result);
	}
	
	
	@Test
	public void fullTestStandard5Pipelined() {
		pMemory.clearMemory();
		pAssembler.selectFile(new File("src/testAssemblyPrograms/fullProgramTest.txt"));
		pAssembler.assembleCode();
		pAssembler.loadToLoader();
		pAssembler.getLoader().loadToMemory();		
		
		pControlUnit.activate();
		
		int result = ((Operand) pControlUnit.getStatusRegister().read()).unwrapInteger();
		
		assertEquals(0, result);
	}
	
	@Test
	public void fullTestStandard6Pipelined() {
		pMemory.clearMemory();
		pAssembler.selectFile(new File("src/testAssemblyPrograms/fullProgramTest.txt"));
		pAssembler.assembleCode();
		pAssembler.loadToLoader();
		pAssembler.getLoader().loadToMemory();		
		
		pControlUnit.activate();
		
		Operand result = (Operand) pControlUnit.getRegisters().read(5);
		
		assertNull(result);
	}
	
	@Test
	public void fullTestStandard7Pipelined() {
		pMemory.clearMemory();
		pAssembler.selectFile(new File("src/testAssemblyPrograms/fullProgramTest.txt"));
		pAssembler.assembleCode();
		pAssembler.loadToLoader();
		pAssembler.getLoader().loadToMemory();		
		
		pControlUnit.activate();
		
		Data dataResult = pMemory.accessAddress(pAssembler.getLookupTable().get("storePoint"));
		int result = ((Operand) dataResult).unwrapInteger();
		
		assertEquals(4, result);
	}
	
	
	

	

}

