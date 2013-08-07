package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import code.Data;
import code.Loader;
import code.MainMemory;
import code.MemoryModule;

public class LoaderTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		fail("Not yet implemented");
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
	