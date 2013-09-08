
package tests;

import static org.junit.Assert.*;

import code.IRfile;
import code.InstructionRegister;
import code.IR;
import code.Instruction;
import code.TransferInstr;
import code.Opcode;
import code.UpdateListener;

import org.junit.Before;
import org.junit.Test;

public class IRtest {
	private InstructionRegister ir;
	private InstructionRegister irFile;
	private Instruction testInstr;
	
	@Before
	public void setUp() {
		ir = new IR();
		irFile = new IRfile();
		ir.registerListener(new UpdateListener(new TestFrame())); //To prevent null pointer exception
		irFile.registerListener(new UpdateListener(new TestFrame()));
		testInstr = new TransferInstr(Opcode.STORE, 0, 0);
	}	
	

	@Test
	public void testRead() {
		assertNull(ir.read()); //read() should return null because contents field is uninitialised.
	}	
	
	
	@Test
	public void testLoadIR() {
		ir.loadIR(testInstr);
		Instruction output = ir.read(); //Tests read()
		Instruction expected = testInstr;
		assertEquals(expected, output);
	}
	
	@Test
	public void testClearIR() { 
		ir.loadIR(testInstr);
		ir.clear();
		assertNull(ir.read()); //IR contents should be null after clear() operation
	}
	
	
	/*
	 * IRfile tests.
	 */
	
	@Test
	public void testReadIRfile() {
		irFile.loadIR(testInstr); //Load testInstr to index 0
		Instruction output = irFile.read(); //Read() returns index 0
		Instruction expected = testInstr;
		
		assertEquals(expected, output);
	}
	
	@Test
	public void testReadIRfileIndex() { //Also tests loading to index
		irFile.loadIR(2, testInstr); //Load testInstr to index 2
		Instruction output = irFile.read(2); //Read() returns index 2
		Instruction expected = testInstr;
		
		assertEquals(expected, output);		
	}
	
	@Test
	public void testClearIndex() {
		irFile.loadIR(1, testInstr); //Load testInstr to index 1
		irFile.clear(1);
		
		assertNull(irFile.read(1)); //Index 1 should be null after clear()		
	}
	
	@Test
	public void testClearAll() {
		irFile.loadIR(0, testInstr); //Load testInstr to index 0
		irFile.loadIR(1, testInstr); //Load testInstr to index 1
		irFile.loadIR(2, testInstr); //Load testInstr to index 2
		irFile.clear(); //Should clear all indexes
		
		for (int i = 0; i < 3; i++) {
			assertNull(irFile.read(i)); //Indexes should be null after clear()	
		}
	}
	
	
	/*
	 * Test index-specifying methods on standard IR (check that indexes are ignored).
	 */
	

	@Test
	public void testReadIndex() {
		ir.loadIR(testInstr); //Load testInstr into standard IR
		Instruction output = ir.read(2); //Use indexed read on IR
		Instruction expected = testInstr;
		
		assertEquals(expected, output); //Tests that index is ignored on standard IR and doesn't cause error.
	}	
	
	
	@Test
	public void testLoadIRIndexed() { //Tests using index on standard IR works and doesn't cause error
		ir.loadIR(1, testInstr);
		Instruction output = ir.read(2);
		Instruction expected = testInstr;
		assertEquals(expected, output);
	}
	
	@Test
	public void testClearIRIndexed() { 
		ir.loadIR(testInstr);
		ir.clear(1);
		assertNull(ir.read()); //IR contents should be null after indexed clear() (index should be ignored on standard IR).
	}
	
	
	


}
