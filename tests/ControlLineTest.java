package tests;

import static org.junit.Assert.*;

import code.ControlLine;
import code.ControlLineImpl;
import code.AddressLine;
import code.AddressLineImpl;
import code.Data;
import code.DataLine;
import code.DataLineImpl;

import org.junit.Test;

public class ControlLineTest {
	private ControlLine cLine = new ControlLineImpl();

	@Test
	public void testInitialisation() {
		assertNotNull(cLine);
		
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
//			return memory.notify(address);
//		}
//		addressLine.put(address);
//		dataLine.put(data);
//		return memory.notify(address, data);		
//	}

}
