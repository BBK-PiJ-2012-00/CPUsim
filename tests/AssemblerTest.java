package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import code.Assembler;
import code.AssemblerImpl;

public class AssemblerTest {
	private Assembler assembler;
	private String testFile;

	@Before
	public void setUp() throws Exception {
		assembler = new AssemblerImpl();
		testFile = "src/assemblyPrograms/assemblerTestProgram.txt";
	}

	/*
	 * Tests file is correctly read, and that each line is added to the arrayList<String> field.
	 */
	@Test
	public void testFileRead() {
		assembler.selectFile(testFile);
		assembler.readAssemblyFile();
		String output = "";
		for (int i = 0; i < ((AssemblerImpl) assembler).getProgramString().size(); i++) {
			output += ((AssemblerImpl) assembler).getProgramString().get(i) + "\n";
			//System.out.println(((AssemblerImpl) assembler).getProgramString().get(i));
		}
		String expected = "<Label> <Instruction/Variable> <Comments>\nL1:  LOAD r0, [0] #Load r1 with contents of memory address 0\n";
		assertEquals(expected, output);
	}
	
	@Test
	public void testParseCode() {
		assembler.selectFile(testFile);
		assembler.readAssemblyFile();
		((AssemblerImpl) assembler).parseCode();
		
	}

}
