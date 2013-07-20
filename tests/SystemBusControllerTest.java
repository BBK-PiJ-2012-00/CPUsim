package tests;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;

import code.*;

public class SystemBusControllerTest {
	private BusController sysBusController;
	private MainMemory memory;
	private Instruction testInstr;
	
	@Before
	public void setUp() {
		sysBusController = SystemBusController.getInstance();
		memory = MemoryModule.getInstance();
		testInstr = new BranchInstr(Opcode.BR, 0);
	}

	@Test
	public void testIsSingleton() { //Check that only once instance of SystemBusController can be created
		BusController anotherController = SystemBusController.getInstance();
		assertEquals(sysBusController, anotherController); //Tests whether the two references refer to the same object
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
		Data output = MBR.getInstance().read(); //MBR accessed directly
		assertEquals(expected, output);
		
		
	}

		

}
