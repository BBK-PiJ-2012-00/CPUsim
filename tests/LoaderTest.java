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

}



	
//	private Data[] programCode; //An array to represent the program to be loaded into memory
//	
//	public void load(Data[] assembledCode) { //For loading code from assmebler to loader
//		this.programCode = assembledCode;
//	}
//	
//	public void loadToMemory() {
//		//this.programCode = codeBuffer.toArray(new Data[codeBuffer.size()]); //Convert codeBuffer to standard array
//		memory.loadMemory(programCode);		
//	}
//	
//	public Data[] getProgramCode() {
//		return this.programCode;
//	}
	