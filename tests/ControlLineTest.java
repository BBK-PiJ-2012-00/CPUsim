package tests;

import static org.junit.Assert.*;

import code.*;

import org.junit.Before;
import org.junit.Test;

/*
 * Needs testing with multiple threads for pipelining.
 */
public class ControlLineTest {
	private ControlLine cLine;
	private Instruction testInstr;
	private MainMemory testMemory;
	private BusController busController; 
	
	@Before
	public void setUp() {
		testInstr = new TransferInstr(Opcode.STORE, 0, 0);
		testMemory = MemoryModule.getInstance();
		busController = SystemBusController.getInstance();
		cLine = ((SystemBusController) busController).accessControlLine(); //This solves problem of duplicate MBR modules being
		//created with the creation of more than one ControlLine; MBR is created upon creation of ControlLine, which would be created
		//twice if instantiated in its own right in this test class; therefore, the solution is to access the same ControlLine
		//object through the busController, which is required during these tests.
	}

	@Test
	public void testInitialisation() {
		assertNotNull(cLine);		
	}
	
	@Test
	public void deliverToMBRTest() {
		assertFalse(cLine.deliverToMBR()); //Should return false as no data has been placed on data line to pass to MBR
		//A true return value is tested as part of writeToBus tests below
	}
	
	@Test
	public void deliverToMBRTest2() { //Uses method accessMockMBR() that exists only for testing; will be deleted/commented out.
		cLine.writeToBus(-1, testInstr); //Need writeToBus() method to provide dummy data (writeToBus calls deliverToMBR()).
		Data expected = testInstr;
		Data output = ((ControlLineImpl) cLine).accessMockMBR();
		assertEquals(expected, output);		
	}
	
	@Test
	public void deliverToMemoryTest() {
		assertFalse(cLine.deliverToMemory(true)); //Should return false as no address or data has been specified
		//A true return value is tested as part of writeToBus tests below
	}
	
	/*
	 * Tests a complete memory read, both the first and second stages; during the first stage of a read the address 
	 * location that holds the value to be read is delivered to main memory from the MAR via the control line. The second
	 * stage encompasses the value held at the address location specified by the address line being loaded onto the data
	 * line and being delivered to the MBR.
	 */
	@Test
	public void testWriteToBus_MemoryReadOperation() {
		assertTrue(cLine.writeToBus(-1, testInstr)); //-1 to signify memory read, with delivery to CPU(MBR)
	}
	
	/*
	 * Will not work when test method accessMockMBR() of ControlLine is commented out/deleted after completion
	 * of testing.
	 */
	@Test
	public void testWriteToBus_MemoryReadOperation2() { //Tests contents are actually delivered to MBR
		cLine.writeToBus(10, testInstr); //Writes a value to memory
		cLine.writeToBus(10, null); //Issues a read (as Data value is null)
		Data expected = testInstr;
		Data output = ((ControlLineImpl) cLine).accessMockMBR();
		assertEquals(expected, output);
	}
	
	
	@Test
	public void testWriteToBus_MemoryWriteOperation() {
		assertTrue(cLine.writeToBus(0, testInstr)); //0 is a valid memory address
	}
	
	@Test public void testWriteToBus_MemoryWriteOperation2() { 
		cLine.writeToBus(0, testInstr);
		Data expected = testInstr;
		Data output = testMemory.accessAddress(0);
		assertEquals(expected, output);
		//Checks contents of memory address is loaded with correct instruction when loaded via system bus
	}

	
	
	//If MBR is a singleton, its contents can be checked for read operation
	
	
	
	
//	synchronized public boolean writeToBus(int address, Data data) { //This method now is responsibly only for writing to bus, with accessing lines
//		//delegated to deliverTo...() methods below. Better encapsulation and separation of concerns, as well as better use of bus lines.
//		if (address == -1) { //Indicates transfer from memory to CPU (memory read)
//			//addressLine.put(); //addressLine.put() can perhaps be got rid of -> if address field in AddressLine is null/0, this signifies delivery
//			//to CPU as opposed to memory (and is true to reality).
//			dataLine.put(data); //This is functionally redundant, but will be useful for GUI animation of bus lines
//			//Need to invoke memory to read the bus, as memory sits idle
//			return this.deliverToMBR(); //Complete read operation. 
//		}
//		//Memory write code:
//		addressLine.put(address);
//		dataLine.put(data);
//		return this.deliverToMemory();		
//	}
//	
//	public boolean deliverToMBR() { //Prompts dataLine to load its value into MBR, completing memory read operation
//		return mockMBR.write(dataLine.read());		
//	}
//	
//	public boolean deliverToMemory() { //Prompts dataLine to load value into memory, completing memory write operation
//		return memory.notify(addressLine.read(), dataLine.read());
//	}

}
