package tests;

import static org.junit.Assert.*;

import code.*;

import org.junit.Test;

public class ControlLineTest {
	private ControlLine cLine = new ControlLineImpl();
	private Instruction testInstr = new TransferInstr(Opcode.STORE, 0, 0);

	@Test
	public void testInitialisation() {
		assertNotNull(cLine);		
	}
	
	@Test
	public void testWriteToBus_MemoryReadOperation() {
		assertTrue(cLine.writeToBus(-1, testInstr)); //-1 to signify memory read		
	}
	
//	@Test
//	public void testWriteToBus_TransferToMemory() { //Tests a memory write
//		fail("Not yet implemented");
//	}
	
//	synchronized public boolean writeToBus(int address, Data data) {
//		if (address == -1) { //Indicates transfer from memory to CPU (memory read)
//			addressLine.put();
//			dataLine.put(data);
//			//Need to invoke memory to read the bus, as memory sits idle
//			return mockMBR.write(data);//This line was wrong; the transfer is to the CPU. Calling memory.notify(address)
//			//will cause an error in memory with address of -1.  Need to load value into MBR.
//		}
//		addressLine.put(address);
//		dataLine.put(data);
//		return memory.notify(address, data);		
//	}

}
