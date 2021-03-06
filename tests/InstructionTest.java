package tests;

import code.BranchInstr;
import code.Instruction;
import code.Opcode;
import code.TransferInstr;
import code.ArithmeticInstr;
import code.HaltInstr;

import static org.junit.Assert.*;

import org.junit.Test;

public class InstructionTest {
	Instruction instr;

	@Test
	public void transferInstructionTest() { //Validates getOpcode() method
		instr = new TransferInstr(Opcode.MOVE, 0, 0);
		Opcode expected = Opcode.MOVE;
		Opcode output = instr.getOpcode(); 
		assertEquals(expected, output);
		
	}
	
	
	@Test 
	public void transferInstructionOpcodeTest() { //To test instruction is not created with illegal opcode for format
		try {
			instr = new TransferInstr(Opcode.ADD, 0, 0);
		}
		catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		}
		assertNull(instr);//instr should be null if creation has failed, which it should because of invalid opcode		
	}
	
	
	@Test
	public void transferInstrMachineStringTest() {
		instr = new TransferInstr(Opcode.LOAD, 0, 0);
		String output = instr.toMachineString();
		String expected = "1 0 0";
		assertEquals(expected, output);
		
	}
	
	@Test
	public void transferInstrToStringTest() {
		instr = new TransferInstr(Opcode.STORE, 0, 0);
		String output = instr.toString();
		String expected = "STORE r0 0";
		assertEquals(expected, output);
	}
	
	@Test
	public void transferInstrToStringTest2() { //Test moving to condition code register
		instr = new TransferInstr(Opcode.MOVE, 0, 16);
		String output = instr.toString();
		String expected = "MOVE r0 rCC";
		assertEquals(expected, output);
	}
	
	@Test 
	public void transferInstrGetOpcodeTest() {
		instr = new TransferInstr(Opcode.MOVE, 0, 0);
		Opcode output = instr.getOpcode();
		Opcode expected = Opcode.MOVE;
		assertEquals(expected, output);
	}
	
	@Test
	public void transferInstrGetField1Test() {
		instr = new TransferInstr(Opcode.LOAD, 50, 7);
		int output = instr.getField1();
		int expected = 50;
		assertEquals(expected, output);
	}
	
	@Test
	public void transferInstrGetField2Test() {
		instr = new TransferInstr(Opcode.STORE, 5, 70);
		int output = instr.getField2();
		int expected = 70;
		assertEquals(expected, output);
	}
	
	@Test
	public void transferInstrLOAD_testInvalidRegisterRef() { //Checks LOAD instruction not created with invalid register ref.
		try {
			instr = new TransferInstr(Opcode.LOAD, 50, 70);
		}
		catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		}
		assertNull(instr);
	}
	
	@Test
	public void transferInstrLOAD_testInvalidMemoryRef() { //Checks LOAD instruction not created with invalid memory ref.
		try {
			instr = new TransferInstr(Opcode.LOAD, 500, 7);
		}
		catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		}
		assertNull(instr);
	}
	
	@Test
	public void transferInstrSTORE_testInvalidRegisterRef() { //Checks STORE instruction not created with invalid register ref.
		try {
			instr = new TransferInstr(Opcode.STORE, 50, 17);
		}
		catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		}
		assertNull(instr);
	}
	
	@Test
	public void transferInstrSTORE_testInvalidMemoryRef() { //Checks STORE instruction not created with invalid memory ref.
		try {
			instr = new TransferInstr(Opcode.STORE, 100, 1);
		}
		catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		}
		assertNull(instr);
	}
	
	@Test
	public void transferInstrMOVE_testInvalidSourceRegisterRef() { //Checks MOVE instruction not created with invalid register ref.
		try {
			instr = new TransferInstr(Opcode.MOVE, 50, 3);
		}
		catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		}
		assertNull(instr);
	}
	
	@Test
	public void transferInstrMOVE_testInvalidDestinationRegisterRef() { //Checks MOVE instr. not created with invalid register ref.
		try {
			instr = new TransferInstr(Opcode.MOVE, 5, -1);
		}
		catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		}
		assertNull(instr);
	}
	
	@Test
	public void arithmeticInstrTest() {
		instr = new ArithmeticInstr(Opcode.ADD, 10, 15);
		Opcode output = instr.getOpcode();
		Opcode expected = Opcode.ADD;
		assertEquals(expected, output);
	}
	
	@Test
	public void arithmeticInstrOpcodeTest() {
		try {
			instr = new ArithmeticInstr(Opcode.BR, 10, 20);
		}
		catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		}
		assertNull(instr); //Checks instruction isn't created with invalid opcode for its format.
	}
	
	@Test
	public void arithmeticInstrMachineStringTest() { //Tests toMachineString()
		instr = new ArithmeticInstr(Opcode.SUB, 0, 0);
		String output = instr.toMachineString();
		String expected = "5 0 0";
		assertEquals(expected, output);
		
	}
	
	@Test
	public void arithmeticInstrToStringTest() { //Tests toString()
		instr = new ArithmeticInstr(Opcode.DIV, 0, 5);
		String output = instr.toString();
		String expected = "DIV r0 r5";
		assertEquals(expected, output);
	}
	
	@Test 
	public void arithmeticInstrGetOpcodeTest() { //Tests getOpcode()
		instr = new ArithmeticInstr(Opcode.MUL, 0, 0);
		Opcode output = instr.getOpcode();
		Opcode expected = Opcode.MUL;
		assertEquals(expected, output);
	}
	
	@Test
	public void arithmeticInstrGetField1Test() {
		instr = new ArithmeticInstr(Opcode.ADD, 5, 10);
		int output = instr.getField1();
		int expected = 5;
		assertEquals(expected, output);
	}
	
	@Test
	public void arithmeticInstrGetField2Test() {
		instr = new ArithmeticInstr(Opcode.ADD, 5, 10);
		int output = instr.getField2();
		int expected = 10;
		assertEquals(expected, output);
	}
	
	@Test
	public void arithmeticInstr_testInvalidDestinationRegisterRef() { //Checks instr. not created with invalid register ref.
		try {
			instr = new ArithmeticInstr(Opcode.ADD, 5, 25);
		}
		catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		}
		assertNull(instr);
	}
	
	@Test
	public void arithmeticInstr_testInvalidDestinationSourceRef() { //Checks instr. not created with invalid register ref.
		try {
			instr = new ArithmeticInstr(Opcode.DIV, 75, 2);
		}
		catch (IllegalStateException e) {
			System.out.println(e.getMessage());
			
		}
		assertNull(instr);
	}
	
	@Test
	public void branchInstrTest() { //Tests creation of branch instruction
		instr = new BranchInstr(Opcode.BR, 0);
		assertNotNull(instr);
		
	}
	
	@Test 
	public void branchInstrInvalidOpcodeTest() { //To test instruction is not created with illegal opcode for format
		try {
			instr = new BranchInstr(Opcode.ADD, 0);
		}
		catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		}
		assertNull(instr); //instr should still be null if creation has failed, which it should because of invalid opcode		
	}
	
	@Test
	public void branchInstrMachineStringTest() {
		instr = new BranchInstr(Opcode.BR, 5);
		String output = instr.toMachineString();
		String expected = "8 5";
		assertEquals(expected, output);
		
	}
	
	@Test
	public void branchInstrToStringTest() {
		instr = new BranchInstr(Opcode.BRZ, 99);
		String output = instr.toString();
		String expected = "BRZ 99";
		assertEquals(expected, output);
	}
	
	@Test 
	public void branchInstrGetOpcodeTest() {
		instr = new BranchInstr(Opcode.BR, 0);
		Opcode output = instr.getOpcode();
		Opcode expected = Opcode.BR;
		assertEquals(expected, output);
	}
	
	@Test
	public void branchInstrGetField1Test() {
		instr = new BranchInstr(Opcode.BRZ, 45);
		int output = instr.getField1();
		int expected = 45;
		assertEquals(expected, output);
	}
	
	@Test
	public void branchInstrGetField2Test() {
		instr = new BranchInstr(Opcode.BRZ, 70);
		int output = instr.getField2();
		int expected = -1; //-1 fixed return value for getField2() for branch instructions
		assertEquals(expected, output);
	}
	
	@Test
	public void branchInstr_BREtest() { //BRE/BRNE need their own tests
		instr = new BranchInstr(Opcode.BRE, 10, 5);
		assertNotNull(instr);
	}
	
	@Test
	public void branchInstrInvalidOpcodeTest_BRE() { //Tests instruction isn't created with illegal opcode for format
		try {
			instr = new BranchInstr(Opcode.BRE, 0); //BRE requires 3 parameters; the third representing a register
		}
		catch (IllegalStateException e) {
		System.out.println(e.getMessage());
		}
		assertNull(instr); //instr should be null if creation has failed, which it should because of invalid opcode
	}
	
	//BR/BRNE use same error handling block so no need to test all scenarios individually
	@Test
	public void branchInstr_testInvalidRegisterRef() { //Checks instr. not created with invalid register ref.
		try {
			instr = new BranchInstr(Opcode.BRNE, 19, 25);
		}
		catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		}
		assertNull(instr);
	}
	
	@Test
	public void branchInstr_testInvalidTargetRef() { //Checks instr. not created with invalid memory ref.
		try {
			instr = new BranchInstr(Opcode.BRE, 102, 9);
		}
		catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		}
		assertNull(instr);
	}
	
	@Test
	public void branchInstrBR_testInvalidTargetRef() { //Checks instr. not created with invalid memory ref.
		try {
			instr = new BranchInstr(Opcode.BRE, 100);
		}
		catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		}
		assertNull(instr);
	}
	
		
	@Test
	public void branchInstrMachineStringTest_BRNE() {
		instr = new BranchInstr(Opcode.BRNE, 32, 10);
		String output = instr.toMachineString();
		String expected = "12 20 A"; //hexadecimal output
		assertEquals(expected, output);		
	}
	
	@Test
	public void branchInstrToStringTest_BRE() {
		instr = new BranchInstr(Opcode.BRE, 5, 14);
		String output = instr.toString();
		String expected = "BRE 5 r14";
		assertEquals(expected, output);
	}
	
	@Test 
	public void branchInstrGetOpcodeTest_BRNE() {
		instr = new BranchInstr(Opcode.BRNE, 0, 0);
		Opcode output = instr.getOpcode();
		Opcode expected = Opcode.BRNE;
		assertEquals(expected, output);
	}
	
	@Test
	public void branchInstrGetField1Test_BRNE() {
		instr = new BranchInstr(Opcode.BRNE, 45, 10);
		int output = instr.getField1();
		int expected = 45;
		assertEquals(expected, output);
	}
	
	@Test
	public void branchInstrGetField2Test_BRE() {
		instr = new BranchInstr(Opcode.BRE, 70, 11);
		int output = instr.getField2();
		int expected = 11; 
		assertEquals(expected, output);
	}
	
	
	@Test
	public void branchInstrInvalidFormatSKZ() { //Tests SKZ won't accept fields
		try { 
			instr = new BranchInstr(Opcode.SKZ, 70);
		}
		catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		}
		assertNull(instr); //Instruction should not be instantiated with invalid arguments
	}
	
	
	@Test
	public void branchInstrGetField1Test_SKZ() { //Check SKZ field1 holds -1 (as field is unused).
		instr = new BranchInstr(Opcode.SKZ);
		int output = instr.getField1();
		int expected = -1;
		assertEquals(expected, output);
	}
	
	@Test
	public void branchInstrGetField2Test_SKZ() { //Check SKZ field2 holds -1 (as field is unused).
		
		
		instr = new BranchInstr(Opcode.SKZ);
		int output = instr.getField2();
		int expected = -1; 
		assertEquals(expected, output);
	}
	
	
	@Test
	public void haltInstrTest() {
		instr = new HaltInstr(Opcode.HALT);
		assertNotNull(instr);
	}
	
	@Test
	public void haltInstrInvalidOpcodeTest() {
		try {
			instr = new HaltInstr(Opcode.LOAD);
		}
		catch (IllegalStateException e) {
			e.getMessage();
		}
		assertNull(instr); //Instruction should only be instantiated with HALT opcode
	}
	
	@Test
	public void haltInstrGetField1Test() { //HALT instructions have no fields, so getters should return the default -1
		
		instr = new HaltInstr(Opcode.HALT);
		int expected = -1;
		int output = instr.getField1();
		assertEquals(expected, output);
	}
	
	@Test
	public void haltInstrGetField2Test() {
		instr = new HaltInstr(Opcode.HALT);
		int expected = -1;
		int output = instr.getField2();
		assertEquals(expected, output);
	}
	
	

}
