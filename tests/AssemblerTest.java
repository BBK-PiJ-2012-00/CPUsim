package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import code.Assembler;
import code.AssemblerImpl;
import code.Data;
import code.Opcode;
import code.OperandImpl;
import code.TransferInstr;

public class AssemblerTest {
	private Assembler assembler;
	private String testFile;

	@Before
	public void setUp() throws Exception {
		assembler = new AssemblerImpl();
		testFile = "src/assemblyPrograms/assemblerTestProgram.txt";
		assembler.selectFile(testFile);
	}

	/*
	 * Tests file is correctly read, and that each line is added to the arrayList<String> field.
	 */
	@Test
	public void testFileRead() {
		assembler.readAssemblyFile();
		String output = "";
		for (int i = 0; i < ((AssemblerImpl) assembler).getProgramString().size(); i++) {
			output += ((AssemblerImpl) assembler).getProgramString().get(i) + "\n";
			//System.out.println(((AssemblerImpl) assembler).getProgramString().get(i));
		}
		String expected = "<Label> <Instruction/Variable> <Comments>\nL1:  LOAD r0, var1 #Load r1 with contents " + 
				"of memory address referred to by var1\n" +	"var1: DATA 7\n";
		assertEquals(expected, output);
	}
	
	/*
	 * Checks that comments are correctly omitted when a line of program code represented as a String is broken
	 * up into an array list.
	 */
	@Test
	public void testSplitCodeLine() {
		assembler.readAssemblyFile();
		String testLine = "L0: LOAD r0, var1 #Comments to be omitted.";
		List<String> output = assembler.splitCodeLine(testLine);
		List<String> expected = new ArrayList<String>();
		expected.add("L0:");
		expected.add("LOAD");
		expected.add("r0");
		expected.add("var1");
		
		//assertEquals(expected.size(), output.size());	//This tests correctly but is not as conclusive as the following
		for (int i = 0; i < output.size(); i++) {
			assertEquals(expected.get(i), output.get(i));
		}
		
	}
	
	@Test
	public void testSeparateOperands_OperandArray() { //Test that the operand section of code is added to operandArray
		assembler.readAssemblyFile();
		assembler.separateOperands();
		
		List<String> outputArray = assembler.getOperandArray();
		String output = outputArray.get(0);
		String expected = "var1: DATA 7";
		
		assertEquals(expected, output);		
	}
	
	@Test
	public void testSeparateOperands_OperandArraySize() {//Test that nothing unexpected (like an instruction) is added to operandArray
		assembler.readAssemblyFile();
		assembler.separateOperands();
		
		assertEquals(1, assembler.getOperandArray().size()); //Only one operand declaration in testFile, so size should be 1
	}
	
	@Test
	public void testSeparateOperands_InstructionArray() { //Test that the instruction section of code is added to operandArray
		assembler.readAssemblyFile();
		assembler.separateOperands();
		
		List<String> outputArray = assembler.getInstructionArray();
		String output = outputArray.get(0);
		String expected = "L1:  LOAD r0, var1 #Load r1 with contents of memory address referred to by var1";
		
		assertEquals(expected, output);		
	}
	
	@Test
	public void testSeparateOperands_InstructionArraySize() {//Test that nothing unexpected (i.e. an operand) is added to instructionArray
		assembler.readAssemblyFile();
		assembler.separateOperands();
		
		assertEquals(1, assembler.getInstructionArray().size()); //Only one instruction in testFile, so size should be 1
	}
	
	
	@Test
	public void testAssembleOperand() { //Check that the operand is correctly assembled
		//First, read the file, separate the operand and break it into parts ready for assembly
		assembler.readAssemblyFile();
		assembler.separateOperands();
		List<String> operandParts = assembler.splitCodeLine(assembler.getOperandArray().get(0));
		
		Data expected = new OperandImpl(7);
		Data output = assembler.assembleOperand(operandParts); //Create the operand
		
		assertEquals(expected, output);
	}
	
	@Test
	public void testAssembleOperand_MapAssignment() { //Checks that symbol is correctly added to lookupTable map
		assembler.readAssemblyFile();
		assembler.separateOperands();
		List<String> operandParts = assembler.splitCodeLine(assembler.getOperandArray().get(0));
		
		assembler.assembleOperand(operandParts);
		int output = assembler.getLookupTable().get("var1");
		int expected = 1; //var1 should be mapped to 1 (an instruction would be at address 0 in the program code)
		
		assertEquals(expected, output);
	}
	
	
	@Test
	public void testAssembleInstruction() {
		assembler.readAssemblyFile();
		assembler.separateOperands();
		List<String> operandParts = assembler.splitCodeLine(assembler.getOperandArray().get(0));
		List<String> instructionParts = assembler.splitCodeLine(assembler.getInstructionArray().get(0));
		assembler.assembleOperand(operandParts); //Operand must be created for instruction to be able to be created
		
		String expected = new TransferInstr(Opcode.LOAD, 0, 1).toString();
		String output = assembler.assembleInstruction(instructionParts, 0).toString(); //Create the instruction
		
		assertEquals(expected, output);
	}
	
	
	@Test
	public void assembleCodeTest_LOADinstr() { //Test assembly of load instruction with an operand
		assembler.readAssemblyFile();
		assembler.assembleCode();
		Data[] expectedArray = new Data[2];
		expectedArray[1] = new OperandImpl(7);
		expectedArray[0] = new TransferInstr(Opcode.LOAD, 0, 1);
		
		Data[] outputArray = assembler.getProgramCode();
		
		for (int i = 0; i < outputArray.length; i++) {
			assertEquals(expectedArray[i].toString(), outputArray[i].toString());
		}		
	}
	
	@Test
	public void assembleCodeTest_size() { //Test assembly of load instruction with an operand, this time checking by size
		assembler.readAssemblyFile();
		assembler.assembleCode();
		
		Data[] outputArray = assembler.getProgramCode();		
		assertEquals(2, outputArray.length);
			
	}
	
	

	
//	@Test
//	public void testCreateData() {		
//		assembler.selectFile(testFile);
//		assembler.readAssemblyFile();
//		String testLine = "L0: LOAD r0, [0] #Comments to be omitted.";
//		List<String> splitCode = assembler.splitCodeLine(testLine);
//		Data output = assembler.createData(splitCode, 0);
//		Data expected = new TransferInstr(Opcode.LOAD, 0, 0);
//		
//		assertEquals(expected.toString(), output.toString());
//		
//	}



}
