package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import code.*;

public class SystemBusControllerTest {
	private CPUbuilder builder;
	private BusController sysBusController;
	private MainMemory memory;
	private Instruction testInstr;
	private MemoryBufferRegister mbr;
	
	@Before
	public void setUp() {
		builder = new CPUbuilder(false);
		sysBusController = builder.getBusController();
		memory = builder.getMemoryModule();
		mbr = builder.getControlUnit().getMBR(); //Get MBR reference		
		
		//Register dummy listeners to prevent null pointer exceptions during testing
		memory.registerListener(new UpdateListener(new TestFrame())); 
		mbr.registerListener(new UpdateListener(new TestFrame()));
		sysBusController.accessControlLine().registerListener(new UpdateListener(new TestFrame()));
		sysBusController.accessControlLine().getAddressBus().registerListener(new UpdateListener(new TestFrame()));
		sysBusController.accessControlLine().getDataBus().registerListener(new UpdateListener(new TestFrame()));
		
		testInstr = new BranchInstr(Opcode.BR, 0);
		
	}
	
	
	@Test
	public void testTransferToMemory() { //Initial test for transferToMemory(); successful transfer should return true
		assertTrue(sysBusController.transferToMemory(10, testInstr));
	}
	
	@Test
	public void testTransferToMemory2() { //A more thorough test; this time checking that specified memory address contains the data
		sysBusController.transferToMemory(20, testInstr);
		Data expected = testInstr;
		Data output = memory.accessAddress(20);
		assertEquals(expected, output);
	}
	
	@Test
	public void testTransferToCPU() {
		assertTrue(sysBusController.transferToCPU(testInstr)); //Successful transfer from memory to CPU returns true	
	}
	

	@Test 
	public void testTransferToCPU2() { //A more thorough test, checking contents of MBR matches data transferred
		sysBusController.transferToCPU(testInstr);
		Data expected = testInstr;
		Data output = mbr.read(); //MBR accessed directly
		assertEquals(expected, output);
		
		
	}
	
	//The GUI updates are tested using the GUI, as this makes more sense than unit testing this functionality
	
	
		

}
