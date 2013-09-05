package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import code.Assembler;
import code.AssemblerImpl;
import code.CPUbuilder;
import code.Data;
import code.HaltInstr;
import code.Opcode;
import code.OperandImpl;
import code.TransferInstr;

public class AssemblerTest {
	private Assembler assembler;
	private File testFile;
	private File testFile2;
	private File testFile3;

	@Before
	public void setUp() throws Exception {
		CPUbuilder builder = new CPUbuilder(false);
		assembler = new AssemblerImpl(builder.getLoader());
		testFile = new File("src/assemblyPrograms/assemblerTestProgram.txt");
		testFile2 = new File("src/assemblyPrograms/assemblerTestProgram2.txt");
		testFile3 = new File("src/assemblyPrograms/TransferInstructionDemo.txt");
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
		String expected = "L1:  LOAD var1, r0 #Load r0 with contents " + 
				"of memory address referred to by var1\n     HALT\n" +	"var1: DATA 7\n";
		assertEquals(expected, output);
	}
	
	/*
	 * Checks that comments are correctly omitted when a line of program code represented as a String is broken
	 * up into an array list.
	 */
	@Test
	public void testSplitCodeLine() {
		assembler.selectFile(testFile);
		assembler.readAssemblyFile();
		String testLine = "L0: LOAD var1, r0 #Comments to be omitted.";
		List<String> output = assembler.splitCodeLine(testLine);
		List<String> expected = new ArrayList<String>();
		expected.add("L0:");
		expected.add("LOAD");
		expected.add("var1");
		expected.add("r0");
		
		//assertEquals(expected.size(), output.size());	//This tests correctly but is not as conclusive as the following
		for (int i = 0; i < output.size(); i++) {
			assertEquals(expected.get(i), output.get(i));
		}
		
	}
	
	@Test
	public void testSeparateOperands_OperandArray() { //Test that the operand section of code is added to operandArray
		assembler.selectFile(testFile);
		assembler.readAssemblyFile();
		assembler.separateOperands();
		
		List<String> outputArray = assembler.getOperandArray();
		String output = outputArray.get(0);
		String expected = "var1: DATA 7";
		
		assertEquals(expected, output);		
	}
	
	@Test
	public void testSeparateOperands_OperandArraySize() {//Test that nothing unexpected (like an instruction) is added to operandArray
		assembler.selectFile(testFile);
		assembler.readAssemblyFile();
		assembler.separateOperands();
		
		assertEquals(1, assembler.getOperandArray().size()); //Only one operand declaration in testFile, so size should be 1
	}
	
	@Test
	public void testSeparateOperands_InstructionArray() { //Test that the instruction section of code is added to operandArray
		assembler.selectFile(testFile);
		assembler.readAssemblyFile();
		assembler.separateOperands();
		
		List<String> outputArray = assembler.getInstructionArray();
		String output = outputArray.get(0);
		String expected = "L1:  LOAD var1, r0 #Load r0 with contents of memory address referred to by var1";
		
		assertEquals(expected, output);		
	}
	
	@Test
	public void testSeparateOperands_InstructionArraySize() {//Test that nothing unexpected (i.e. an operand) is added to instructionArray
		assembler.selectFile(testFile);
		assembler.readAssemblyFile();
		assembler.separateOperands();
		
		assertEquals(2, assembler.getInstructionArray().size()); //Two instructions in testFile, so size should be 1
	}
	
	
	@Test
	public void testAssembleOperand() { //Check that the operand is correctly assembled
		//First, read the file, separate the operand and break it into parts ready for assembly
		assembler.selectFile(testFile);
		assembler.readAssemblyFile();
		assembler.separateOperands();
		List<String> operandParts = assembler.splitCodeLine(assembler.getOperandArray().get(0));
		
		Data expected = new OperandImpl(7);
		Data output = assembler.assembleOperand(operandParts); //Create the operand
		
		assertEquals(expected, output);
	}
	
	@Test
	public void testAssembleOperand_MapAssignment() { //Checks that symbol is correctly added to lookupTable map
		assembler.selectFile(testFile);
		assembler.readAssemblyFile();
		assembler.separateOperands();
		List<String> operandParts = assembler.splitCodeLine(assembler.getOperandArray().get(0));
		
		assembler.assembleOperand(operandParts);
		int output = assembler.getLookupTable().get("var1");
		int expected = 2; //var1 should be mapped to 1 (an instruction would be at address 0 in the program code)
		
		assertEquals(expected, output);
	}
	
	
	@Test
	public void testAssembleInstruction() {
		assembler.selectFile(testFile);
		assembler.readAssemblyFile();
		assembler.separateOperands();
		List<String> operandParts = assembler.splitCodeLine(assembler.getOperandArray().get(0));
		List<String> instructionParts = assembler.splitCodeLine(assembler.getInstructionArray().get(0));
		assembler.assembleOperand(operandParts); //Operand must be created for instruction to be able to be created
		
		String expected = new TransferInstr(Opcode.LOAD, 2, 0).toString();
		String output = assembler.assembleInstruction(instructionParts).toString(); //Create the instruction
		
		assertEquals(expected, output);
	}
	
	
	@Test
	public void assembleCodeTest_LOADinstr() { //Test assembly of load instruction with an operand
		assembler.selectFile(testFile);
		assembler.assembleCode();
		Data[] expectedArray = new Data[3];
		expectedArray[2] = new OperandImpl(7);
		expectedArray[1] = new HaltInstr(Opcode.HALT);
		expectedArray[0] = new TransferInstr(Opcode.LOAD, 2, 0);
		
		Data[] outputArray = assembler.getProgramCode();
		
		for (int i = 0; i < outputArray.length; i++) {
			assertEquals(expectedArray[i].toString(), outputArray[i].toString());
		}		
	}
	
	@Test
	public void assembleCodeTest_size() { //Test assembly of load instruction with an operand, this time checking by size
		assembler.selectFile(testFile);
		assembler.assembleCode();
		
		Data[] outputArray = assembler.getProgramCode();		
		assertEquals(3, outputArray.length);
			
	}
	
	/*
	 * Test assembly of a full program containing a variety of operands and instructions. This tests
	 * that labels are correctly removed and also that blank lines are skipped in processing the file
	 * (there is a blank line containing a tab and space characters between operand and instruction 
	 * declarations in the test file).
	 */
	
	@Test
	public void testAssembly() { 
		assembler.selectFile(testFile2);
		assembler.assembleCode();
		
		String[] expectedArray = new String[19];
        expectedArray[0] = "LOAD 14 r0";
        expectedArray[1] = "LOAD 15 r1";
        expectedArray[2] = "ADD r0 r1";
        expectedArray[3] = "STORE r0 16";
        expectedArray[4] = "LOAD 18 r2";
        expectedArray[5] = "LOAD 14 r3";
        expectedArray[6] = "BR 8";
        expectedArray[7] = "SUB r2 r3";
        expectedArray[8] = "ADD r2 r3";
        expectedArray[9] = "STORE r2 17";
        expectedArray[10] = "LOAD 18 rCC";
        expectedArray[11] = "BRNE 13 r2";
        expectedArray[12] = "MUL r2 r3";
        expectedArray[13] = "HALT";
        expectedArray[14] = "#5";
        expectedArray[15] = "#10";
        expectedArray[16] = "#0";
        expectedArray[17] = "#0";
        expectedArray[18] = "#20";
        
        Data[] outputArray = assembler.getProgramCode();
		
		for (int i = 0; i < outputArray.length; i++) {
			assertEquals(expectedArray[i], outputArray[i].toString());
			//System.out.println(outputArray[i]);
		}
	}
	
	@Test
	public void testLoadToLoader() {
		assembler.selectFile(testFile2);
		assembler.assembleCode();
		
		assembler.loadToLoader();
		
		Data[] expected = assembler.getProgramCode();
		Data[] output = ((AssemblerImpl) assembler).getLoader().getProgramCode();
		
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i].toString(), output[i].toString());
		}
	}
	
	
	@Test
	public void testLeadingComments() { //Tests that any initial comments are correctly separated from main code
		assembler.selectFile(testFile3);
		assembler.assembleCode();
		List<String> leadingCommentsOutput = assembler.getLeadingComments();
		List<String> expected = new ArrayList<String>();
		expected.add(0, "# This program demonstrates transfer instructions:");
		expected.add(1, "# LOAD, STORE and MOVE.");
		
		for (int i = 0; i < expected.size(); i++) { //Compare contents of the two lists
			assertEquals(expected.get(i), leadingCommentsOutput.get(i));
		}
		
	}
	
	
	/*
	 * Tests that invalid operand declarations in an assembly file throw an error and prevent further
	 * assembly; to allow such a file to be assembled would cause errors in the simulator. The expected
	 * NumberFormatException is caught by the assembler and displays a pop up to the user alerting them
	 * to the problem. This test asserts that the assembleCode() method returns false (which it should 
	 * in the event of an assembly error). In practice, a return value of false prevents the GUI from
	 * displaying and executing the file, ensuring the user cannot run it.
	 */
	@Test
	public void testBadOperandDeclaration() { //Tests that invalid operand declarations throw an error and prevent assembly
		assembler.selectFile(new File("src/testAssemblyPrograms/badOperandTest.txt"));			
		assertFalse(assembler.assembleCode());
	}
	
	
	@Test
	public void testBadDataDeclaration() { //Checks that omitting DATA keyword causes assembleCode() to return false.
		assembler.selectFile(new File("src/testAssemblyPrograms/badDataDeclaration.txt"));
		assertFalse(assembler.assembleCode());
	}
	
	
	@Test
	public void testFileNotFound() { //Checks that if a file is not found the error is handled (with pop up error message)
		assembler.selectFile(new File("src/aNonExistentFile.txt"));
		assertFalse(assembler.assembleCode());		
	}
	
	
	@Test
	public void testInvalidOpcode() { //Tests that invalid opcodes prevent assembly and provide pop up warning
		assembler.selectFile(new File("src/testAssemblyPrograms/invalidOpcodeDeclaration.txt"));
		assertFalse(assembler.assembleCode());
	}
	
	@Test
	public void testUndeclaredLabelReference() { //Tests that undeclared label references prevent assembly and provide relevant warning
		assembler.selectFile(new File("src/testAssemblyPrograms/badLabel.txt"));
		assertFalse(assembler.assembleCode());
	}
	
	/*
	 * BRE and BRNE instructions share the same error handling code as LOAD instructions, so this test also caters
	 * to errors in these instructions.
	 */
	@Test
	public void testBadRegisterReferenceLOAD() { //Tests invalid register reference in LOAD instruction is caught with warning
		assembler.selectFile(new File("src/testAssemblyPrograms/badRegisterRefLOAD.txt"));
		assertFalse(assembler.assembleCode());
	}
	
	@Test
	public void testBadRegisterReferenceSTORE() { //Tests invalid register reference in STORE instruction is caught with warning
		assembler.selectFile(new File("src/testAssemblyPrograms/badRegisterRefSTORE.txt"));
		assertFalse(assembler.assembleCode());
	}
	
	
	/*
	 * MOVE and arithmetic instructions share error handling code, so this test also caters to arithmetic
	 * instruction errors.
	 */
	@Test
	public void testBadRegisterReferenceMOVE() { //Tests invalid register reference in MOVE instruction is caught with warning
		assembler.selectFile(new File("src/testAssemblyPrograms/badRegisterRefMOVE.txt"));
		assertFalse(assembler.assembleCode());
	}
	
	/*
	 * Arithmetic instructions are not allowed to reference the condition code register, unlike move instructions,
	 * so it is necessary to test this separately.
	 */
	@Test
	public void testBadRegisterReferenceArithmetic() { //Tests invalid rCC reference in arithmetic instruction is caught with warning
		assembler.selectFile(new File("src/testAssemblyPrograms/badRegisterRefArithmetic.txt"));
		assertFalse(assembler.assembleCode());
	}
	
	@Test
	public void testBadBranchDeclaration() { //Tests invalid BR/BRZ instruction is caught with warning
		assembler.selectFile(new File("src/testAssemblyPrograms/badBranch.txt"));
		assertFalse(assembler.assembleCode());
	}

	
	
	
	
	
		
		
	
	//Test error handling (best tested on GUI) file not found, bad assembly language;
	
	//Display code is tested using GUI

}
