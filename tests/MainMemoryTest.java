package tests;

import static org.junit.Assert.*;

import code.*;

import org.junit.Before;
import org.junit.Test;

public class MainMemoryTest {
	private MainMemory memory; 
	private Instruction testInstr;
	
	@Before
	public void setUp() {
		memory = MemoryModule.getInstance(); //Constructor is private, so getInstance() must be used
		memory.registerListener(new UpdateListener(new CPUframe())); //To prevent null pointer, even though not used in test
		testInstr = new TransferInstr(Opcode.STORE, 0, 0);
		//Next line stores an ADD instruction to address index 7
		memory.writeInstruction(new ArithmeticInstr(Opcode.ADD, 1, 1), 7); //Useful for testing memory read operations
	}
	

	@Test
	public void testSingleton() { //Test that only once instance of memory can be created
		MainMemory anotherMemoryInstance = MemoryModule.getInstance();
		assertEquals(memory, anotherMemoryInstance);
	}
	
	@Test
	public void writeInstructionTest() {
		memory.writeInstruction(testInstr, 0);//Writes STORE instruction to address 0
		Data output = memory.accessAddress(0);
		Data expected = testInstr;
		assertEquals(expected, output);		
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
	public void readNotifyTest() { //Tests notifyRead(int address), to be used by system bus (memory read).
		//Uses instruction stored at address 7 (initialised at start of test class)
		assertTrue(memory.notifyRead(7)); //Should return true for a successful read		
	}		
	
	
	@Test
	public void readBadNotify() { //Test a case that shouldn't work
		assertFalse(memory.notifyRead(100)); //100 is invalid address
	}
	

	
//	public boolean notify(int address) { //Absence of data indicates requested memory read as opposed to write (as in reality)
//		Data dataRead;
//		if (address < 100 && address >=0) {
//			if (MEMORY[address] == null) {
//				dataRead = new OperandImpl(0); //If the contents of address is null, read as 0 (as in reality)				
//			}
//			else {
//				dataRead = MEMORY[address];
//			}
//			systemBus = SystemBus.getInstance(); //Putting this here breaks awkward creation chain, and is of no consequence 
//			//as there is only ever one system bus instance with global access point.
//			
//			//As this is all performed by the same thread, there should be no issue!!
//			//Don't issue this line here!! Return now, perform transferToCPU from control line?
//			//It may be an idea to have the methods memoryRead() and memoryWrite() in SystemBus instead,
//			//which would allow both to be atomic and encapsulate all actions pertaining to those operations.
//			//Will the below line be executable? Bus is waiting on completion of this method!
//			systemBus.transferToCPU(dataRead);//Transfers read data to system bus, subsequently to CPU
//			return true;
//		}
//		//Throw exception if address invalid? This would disrupt program flow (possibly)
//		return false;
//	}


}
