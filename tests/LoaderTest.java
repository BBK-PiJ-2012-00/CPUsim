package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import code.Data;
import code.Loader;
import code.LoaderImpl;
import code.MainMemory;
import code.MemoryModule;
import code.Instruction;
import code.TransferInstr;
import code.ArithmeticInstr;
import code.Opcode;

public class LoaderTest {
	private Loader loader;
	private MainMemory memory;
	private Data[] testProgram;	

	@Before
	public void setUp() throws Exception {
		loader = new LoaderImpl();
		memory = MemoryModule.getInstance();
		testProgram = new Data[3];
		testProgram[0] = new TransferInstr(Opcode.LOAD, 0, 0);
		testProgram[1] = new TransferInstr(Opcode.LOAD, 1, 1);
		testProgram[2] = new ArithmeticInstr(Opcode.DIV, 1, 0);
		
	}

	@Test
	public void testLoad() { 
		loader.load(testProgram);
		
		Data[] expected = testProgram;
		Data[] output = loader.getProgramCode();
		
		for (int i = 0; i < testProgram.length; i++) {
			assertEquals(expected[i].toString(), output[i].toString());
		}
	}

	
	@Test
	public void testLoadToMemory() {
		loader.load(testProgram);
		loader.loadToMemory();
		
		for (int i = 0; i < testProgram.length; i++) {
			assertEquals(testProgram[i].toString(), memory.accessAddress(i).toString());
		}
	}
	
}
	

	