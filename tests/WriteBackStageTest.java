package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import code.ArithmeticInstr;
import code.IR;
import code.Instruction;
import code.InstructionRegister;
import code.Opcode;
import code.Operand;
import code.OperandImpl;
import code.RegisterFile;
import code.RegisterFile16;
import code.StandardWriteBackStage;
import code.UpdateListener;
import code.WriteBackStage;

public class WriteBackStageTest {
	private WriteBackStage writeBackStage;
	
	private InstructionRegister ir;
	private RegisterFile genRegisters;
	
	private Instruction testInstrADD;
	private Instruction testInstrSUB;
	private Instruction testInstrDIV;
	private Instruction testInstrMUL;
		

	@Before
	public void setUp() throws Exception {
		ir = new IR();
		ir.registerListener(new UpdateListener(new TestFrame())); //Dummy listeners instantiated to prevent null pointer exception
		genRegisters = new RegisterFile16();
		genRegisters.registerListener(new UpdateListener(new TestFrame()));
		
		writeBackStage = new StandardWriteBackStage(ir, genRegisters);	
		
		testInstrADD = new ArithmeticInstr(Opcode.ADD, 2, 4); //Add contents of r2 and r4, storing in r2
		testInstrSUB = new ArithmeticInstr(Opcode.SUB, 9, 10); //Sub contents of r10 from r9, storing in r9
		testInstrDIV = new ArithmeticInstr(Opcode.DIV, 3, 12); //Divide contents of r3 by contents of r12, storing in r3
		testInstrMUL = new ArithmeticInstr(Opcode.MUL, 5, 8); //Multiply contents of r5 by contents of r8, storing in r5
	}

	@Test
	public void testWriteBack() {
		ir.loadIR(testInstrADD); //Load an instruction into IR
		writeBackStage.instructionWriteBack(new OperandImpl(300)); //Supply test operand 300
		//The result of the above line of code is that 300 should be written into r2 (see testInstrADD)
		
		Operand output = (Operand) genRegisters.read(2);
		Operand expected = new OperandImpl(300);
		
		assertEquals(expected, output);
	}
	
	@Test
	public void testReceive() { //Tests receive() method of standard write back stage (as opposed to pipelined), which calls
		//instructionWriteBack() method tested above
		
		ir.loadIR(testInstrSUB); //Load an instruction into IR
		writeBackStage.instructionWriteBack(new OperandImpl(101)); //Supply test operand 300
		//The result of the above line of code is that 300 should be written into r9 (see testInstrSUB)
		
		Operand output = (Operand) genRegisters.read(9);
		Operand expected = new OperandImpl(101);
		
		assertEquals(expected, output);
	}
	
	//Need to test pipelined version once implemented

	
	


}
