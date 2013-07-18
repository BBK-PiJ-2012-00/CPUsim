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
		//memory.writeInstruction(new ArithmeticInstr(Opcode.SUB, 0, 100), 7); //Adds an arithmetic instruction to memory address 7, useful for testing mem. read
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
	
	/*
	 * This test relies on a method in SystemBusController accessControlLine(), which exists only for testing
	 * this scenario. Once complete, this method will be commented out/deleted, so this test will fail in the future.
	 * 
	 * MBR may become a singleton in the future, at which point this test can be re-written accordingly.
	 */
	@Test 
	public void testTransferToCPU2() { //A more thorough test, checking contents of MBR matches data transferred
		sysBusController.transferToCPU(testInstr);
		ControlLine cLine = ((SystemBusController) sysBusController).accessControlLine(); //MBR accessed only via control line
		Data expected = testInstr;
		Data output = ((ControlLineImpl) cLine).accessMockMBR();
		assertEquals(expected, output);
		
		
	}

		

}
