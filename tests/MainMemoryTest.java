package tests;

import static org.junit.Assert.*;

import code.*;

import org.junit.Before;
import org.junit.Test;

public class MainMemoryTest {
	private MainMemory memory; 
	private MemoryBufferRegister mbr;
	private Instruction testInstr;
	
	@Before
	public void setUp() {
		/*
		 * Listeners are added to all classes that use a listener to prevent null exceptions 
		 * during testing (serve no functional purpose here).
		 */	
		CPUbuilder builder = new CPUbuilder(false);
		memory = builder.getMemoryModule();
		mbr = builder.getControlUnit().getMBR();		
		
		mbr.registerListener(new UpdateListener(new TestFrame()));
		builder.getBusController().accessControlLine().registerListener(new UpdateListener(new TestFrame()));
		builder.getBusController().accessControlLine().getAddressBus().registerListener(new UpdateListener(new TestFrame()));
		builder.getBusController().accessControlLine().getDataBus().registerListener(new UpdateListener(new TestFrame()));
		
		memory.registerListener(new UpdateListener(new TestFrame()));
		
		testInstr = new TransferInstr(Opcode.STORE, 0, 0);
		
		//Next line stores an ADD instruction to address index 7
		memory.notifyWrite(7, new ArithmeticInstr(Opcode.ADD, 1, 1)); //Useful for testing memory read operations
	}

	
	@Test
	public void notifyWriteTest() { //Tests notifyWrite(int address, Data data) method, to be used by SystemBus to prompt memory write
		assertTrue(memory.notifyWrite(99, testInstr)); //Successful write should return true
	}
	
	@Test
	public void notifyWriteTest2() { //Tests notifyWrite(int address, Data data) method, to be used by SystemBus to prompt memory write
		memory.notifyWrite(99, testInstr);
		Data expected = testInstr;
		Data output = memory.accessAddress(99);
		assertEquals(expected, output); //Assets that testInstr is written to address 99
	}
	
	@Test
	public void notifyWriteTestFalseAddress() { //Tests invalid memory address; should return false
		assertFalse(memory.notifyWrite(100, testInstr));		
	}
	
	
	@Test
	public void notifyReadTest() { //Tests notifyRead(int address), to be used by system bus (memory read).
		//Uses instruction stored at address 7 (initialised at start of test class)
		assertTrue(memory.notifyRead(7)); //Should return true for a successful read		
	}		
	
	
	@Test
	public void badNotifyRead() { //Test a case that shouldn't work
		assertFalse(memory.notifyRead(100)); //100 is invalid address
	}
	
	
	@Test
	public void testLoadMemory() {
		Data[] testArray = new Data[2];
		testArray[0] = testInstr;
		Instruction testInstr2 = new BranchInstr(Opcode.BRZ, 5);
		testArray[1] = testInstr2;
		
		memory.loadMemory(testArray);
		
		for (int i = 0; i < 2; i++) {
			assertEquals(testArray[i], memory.accessAddress(i));
		}
		
	}

}
