package tests;

import static org.junit.Assert.*;

import code.*;

import org.junit.Test;

public class MainMemoryTest {
	private MainMemory memory = MemoryModule.getInstance(); //Constructor is private, so getInstance() must be used
	private Instruction testInstr;

	@Test
	public void testSingleton() { //Test that only once instance of memory can be created
		MainMemory anotherMemoryInstance = MemoryModule.getInstance();
		assertEquals(memory, anotherMemoryInstance);
	}
	
	@Test
	public void writeInstructionTest() {
		testInstr = new TransferInstr(Opcode.STORE, 0, 0);
		memory.writeInstruction(testInstr, 0);//Writes STORE instruction to address 0
		Data output = memory.accessAddress(0);
		Data expected = testInstr;
		assertEquals(expected, output);		
	}
	
//	
//	public boolean notify(int address, Data data) { //Method to prompt memory to receive data from system bus (write)
//		//No checking of address being empty or not; up to programmer
//		if (address < 100 && address >= 0) {
//			MEMORY[address] = data;
//			return true;
//		}
//		//Throw an exception or simply return false?
//		return false;
//	}
//	
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
