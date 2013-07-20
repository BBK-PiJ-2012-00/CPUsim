package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import code.*;

public class MBRtest {
	private MemoryBufferRegister mbr;
	private Instruction testInstr;
	
	@Before
	public void setUp() {
		mbr = MBR.getInstance();
		testInstr = new TransferInstr(Opcode.MOVE, 0, 0);
	}
	
	@Test
	public void testIsSingleton() { //Test only once object of the class can ever be created
		MemoryBufferRegister anotherMBR = MBR.getInstance();
		assertEquals(mbr.hashCode(), anotherMBR.hashCode()); //Same objects have the same hashcode
	}

	@Test
	public void testWrite() {
		assertTrue(mbr.write(testInstr)); //Successful write should return true.		
	}
	
	@Test
	public void testRead() {
		mbr.write(testInstr);
		Data expected = testInstr;
		Data output = mbr.read();
		assertEquals(expected, output);		
	}


}
