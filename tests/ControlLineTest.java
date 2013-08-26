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
	private MainMemory memory;
	private BusController busController; 
	
	private MemoryBufferRegister mbr; //Reference to control unit's MBR to check system bus delivers correctly
	
	
	@Before
	public void setUp() {
	
		
		CPUbuilder builder = new CPUbuilder(false); //Create non-pipelined CPU
		testInstr = new TransferInstr(Opcode.STORE, 0, 0);
		memory = builder.getMemoryModule();
		busController = builder.getBusController();
		cLine = busController.accessControlLine();
		mbr = builder.getControlUnit().getMBR();
		
		/*
		 * Listeners are added to all classes that use a listener to prevent null exceptions 
		 * during testing (serve no functional purpose here).
		 */		
		mbr.registerListener(new UpdateListener(new TestFrame()));
		builder.getControlUnit().getMAR().registerListener(new UpdateListener(new TestFrame()));
		memory.registerListener(new UpdateListener(new TestFrame()));
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
	public void deliverToMBRTest2() {
		cLine.writeToBus(-1, testInstr); //Need writeToBus() method to provide dummy data (writeToBus calls deliverToMBR()).
		Data expected = testInstr;
		Data output = mbr.read();
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
	
	
	@Test
	public void testWriteToBus_MemoryReadOperation2() { //Tests contents are actually delivered to MBR
		cLine.writeToBus(10, testInstr); //Writes a value to memory
		cLine.writeToBus(10, null); //Issues a read (as Data value is null)
		Data expected = testInstr;
		Data output = mbr.read();
		assertEquals(expected, output);
	}
	
	
	@Test
	public void testWriteToBus_MemoryWriteOperation() {
		assertTrue(cLine.writeToBus(0, testInstr)); //0 is a valid memory address
	}
	
	@Test public void testWriteToBus_MemoryWriteOperation2() { 
		cLine.writeToBus(0, testInstr);
		Data expected = testInstr;
		Data output = memory.accessAddress(0);
		assertEquals(expected, output);
		//Checks contents of memory address is loaded with correct instruction when loaded via system bus
	}

}
