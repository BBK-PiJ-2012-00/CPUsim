package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import code.ALU;
import code.ArithmeticInstr;
import code.BranchInstr;
import code.Data;
import code.ExecuteStage;
import code.FetchDecodeStage;
import code.HaltInstr;
import code.IR;
import code.Instruction;
import code.InstructionRegister;
import code.MainMemory;
import code.MemoryModule;
import code.Opcode;
import code.Operand;
import code.OperandImpl;
import code.PC;
import code.ProgramCounter;
import code.Register;
import code.RegisterFile;
import code.RegisterFile16;
import code.StandardExecuteStage;
import code.StandardFetchDecodeStage;
import code.StandardWriteBackStage;
import code.StatusRegister;
import code.TransferInstr;
import code.WriteBackStage;

public class ExecuteStageTest {
	private FetchDecodeStage fetchDecodeStage; //Required for fetching of instructions before execution can be tested
	private WriteBackStage writeBackStage; //Required for completion of arithmetic instructions
	private ExecuteStage executeStage;
	
	private InstructionRegister ir;
	private ProgramCounter pc;
	private RegisterFile genRegisters;
	private Register statusRegister;
	
	
	private MainMemory memory;	
	
	private Instruction testInstrSTORE;
	private Instruction testInstrLOAD;
	private Instruction testInstrMOVE;
	
	private Instruction testInstrADD;
	private Instruction testInstrSUB;
	private Instruction testInstrDIV;
	private Instruction testInstrMUL;
	
	private Instruction testInstrBR;
	private Instruction testInstrBRZ;
	private Instruction testInstrSKZ;
	private Instruction testInstrBRE;
	private Instruction testInstrBRNE;
	
	private Instruction testInstrHALT;
	
	

	@Before
	public void setUp() throws Exception {
		pc = new PC();
		ir = new IR();
		genRegisters = new RegisterFile16();
		statusRegister = new StatusRegister();
		
		
		fetchDecodeStage = new StandardFetchDecodeStage(ir, pc);
		writeBackStage = new StandardWriteBackStage(ir, genRegisters);
		executeStage = new StandardExecuteStage(ir, pc, genRegisters, statusRegister, writeBackStage);
		
		memory = MemoryModule.getInstance();
		
		testInstrSTORE = new TransferInstr(Opcode.STORE, 0, 99); //source r0, destination address 99
		testInstrLOAD = new TransferInstr(Opcode.LOAD, 50, 0); //Load contents of address 50 to register 0
		testInstrMOVE = new TransferInstr(Opcode.MOVE, 0, 15); //Move contents of r0 to r15
		
		testInstrADD = new ArithmeticInstr(Opcode.ADD, 2, 4); //Add contents of r2 and r4, storing in r2
		testInstrSUB = new ArithmeticInstr(Opcode.SUB, 9, 10); //Sub contents of r10 from r9, storing in r9
		testInstrDIV = new ArithmeticInstr(Opcode.DIV, 3, 12); //Divide contents of r3 by contents of r12, storing in r3
		testInstrMUL = new ArithmeticInstr(Opcode.MUL, 5, 8); //Multiply contents of r5 by contents of r8, storing in r5
		
		testInstrBR = new BranchInstr(Opcode.BR, 10); //Branch to memory address 10
		testInstrBRZ = new BranchInstr(Opcode.BRZ, 37); //Branch to memory address 37
		testInstrSKZ = new BranchInstr(Opcode.SKZ); //Skip if zero
		testInstrBRE = new BranchInstr(Opcode.BRE, 92, 1); //Branch to address 92 if contents of r1 equals contents of status reg
		testInstrBRNE = new BranchInstr(Opcode.BRNE, 77, 6);//Branch to addr. 77 if contents of r6 doesn't equal contents of s. reg.
		
		testInstrHALT = new HaltInstr(Opcode.HALT); //Halt instruction 
		
		memory.notify(50, new OperandImpl(1000)); //Load operand (integer) 1000 to memory address 50		
	}


	@Test
	public void testForward() { //Tests forward() method (forwards result of arithmetic operations to write back)
		ir.loadIR(testInstrDIV); //Load instruction to IR (should store operand in r3)
		executeStage.forward(new OperandImpl(300));
		
		Operand output = (Operand) genRegisters.read(3); //300 should be in r3 as result of calling forward()
		Operand expected = new OperandImpl(300);
		
		assertEquals(expected, output);		
	}
	
	/*
	 * A test operand has been loaded into memory address 50 in the setup. 
	 * testInstrLOAD is a LOAD instruction which loads the operand at memory address 50 to register 0
	 * So it must be checked that register 0 contains the operand (1000) contained at address 50, for the successful
	 * execution of a LOAD instruction.
	 */
	@Test
	public void testInstructionExecuteLOAD() { //Test execution of LOAD instruction
		//Load a LOAD instruction into memory address, and prompt a fetch which will call decode, and then execute
		memory.notify(0, testInstrLOAD); //load test instruction into address 0
		fetchDecodeStage.instructionFetch(); //This should result in r0 containing 1000
		int opcode = fetchDecodeStage.instructionDecode(); //Returns opcode value, to be passed to execute stage
		executeStage.instructionExecute(opcode);
		
		Operand output = (Operand) genRegisters.read(0);
		Operand expected = new OperandImpl(1000);
		assertEquals(expected, output);	
	}
	
	
	@Test
	public void testInstructionExecuteSTORE() { //Test execution of STORE instruction
		Operand operand = new OperandImpl(5000);
		//Firstly, load an operand (5000) into r0
		genRegisters.write(0, operand);
		//Now load a store instruction into memory address 0, ready for fetch
		memory.notify(0, testInstrSTORE);
		fetchDecodeStage.instructionFetch();
		int opcode = fetchDecodeStage.instructionDecode();
		executeStage.instructionExecute(opcode);
		//Should result in operand 5000 being stored at address 99
		
		Data expected = operand;
		Data output = memory.accessAddress(99);
		assertEquals(expected, output);		
	}
	
	
	
//					
//			case 2: //A STORE instruction
//					mar.write(ir.read().getField2()); //Load mar with destination (memory address)
//					mbr.write(genRegisters.read(ir.read().getField1())); //Write to mbr the data held in genRegisters at index
//					//given by field1(source) of instruction held in IR.
//					systemBus.transferToMemory(mar.read(), mbr.read()); //Transfer contents of mbr to address specified in mar
//					break;
//					
//			case 3: //A MOVE instruction (moving data between registers)
//					genRegisters.write(ir.read().getField2(), genRegisters.read(ir.read().getField1()));
//					//Write to the destination specified in field2 of instr held in ir, the instr held in the register
//					//specified by field1 of the instruction in the ir.
//					genRegisters.write(ir.read().getField1(), null); //Complete the move by resetting register source
//					break;
//					
//					
//					
//			case 4: //An ADD instruction (adding contents of one register to second (storing in the first).
//					Operand op1 = (Operand) genRegisters.read(ir.read().getField1()); //access operand stored in first register
//					Operand op2 = (Operand) genRegisters.read(ir.read().getField2());//access operand stored in second register
//					Operand result = ALU.AdditionUnit(op1, op2); //Have ALU perform operation
//					writeBackStage.receive(result); //Call write back stage to store result of addition
//					break;
//			
//			case 5: //A SUB instruction (subtracting contents of one register from a second (storing in the first).
//					op1 = (Operand) genRegisters.read(ir.read().getField1()); //access first operand
//					op2 = (Operand) genRegisters.read(ir.read().getField2()); //access second operand
//					result = ALU.SubtractionUnit(op1, op2);
//					writeBackStage.receive(result);;
//					break;				
//					
//			case 6: //A DIV instruction (dividing contents of one register by contents of another (storing in the first).
//					op1 = (Operand) genRegisters.read(ir.read().getField1()); //access first operand
//					op2 = (Operand) genRegisters.read(ir.read().getField2()); //access second operand
//					result = ALU.DivisionUnit(op1, op2); //op1 / op2
//					writeBackStage.receive(result);;
//					break;					
//					
//			case 7: //A MUL instruction (multiplying contents of one register by contents of another (storing result in first).
//					op1 = (Operand) genRegisters.read(ir.read().getField1()); //access first operand
//					op2 = (Operand) genRegisters.read(ir.read().getField2()); //access second operand
//					result = ALU.MultiplicationUnit(op1, op2); //op1 * op2
//					writeBackStage.receive(result);;
//					break;
//					
//					
//					
//			case 8: //A BR instruction (unconditional branch to memory location in instruction field 1).
//					pc.setPC(ir.read().getField1());
//					//A branch instruction updates PC to new memory location
//					break;
//				
//			case 9: //A BRZ instruction (branch if value in status register is zero).
//					if (statusRegister.read().unwrapInteger() == 0) {
//						pc.setPC(ir.read().getField1()); //If statusRegister holds 0, set PC to new address held in instruction
//					}
//					break; //If not 0, do nothing
//					
//			case 10: //A SKZ instruction (skip the next instruction (increment PC by one) if status register holds 0).
//					 if (statusRegister.read().unwrapInteger() == 0) {
//						 pc.incrementPC();
//					 }
//					 break; //If not 0, do nothing
//					 
//			case 11: //A BRE instruction (branch if status reg. contents = contents of register ref. in instruction)
//					 int genRegRef = ir.read().getField2(); //Reference to register referred to in instruction
//					 if (statusRegister.read().equals((Operand) genRegisters.read(genRegRef))) { //If equal
//						 pc.setPC(ir.read().getField1()); //Set PC to equal address in field1 of instruction in ir						 
//					 }
//					 break; //If not equal, do nothing
//					 
//			case 12: //A BRNE instruction (branch if status reg. contents != contents of register ref. in instruction)
//					 genRegRef = ir.read().getField2(); //Reference to register referred to in instruction
//					 if (!(statusRegister.read().equals((Operand) genRegisters.read(genRegRef)))) { //If not equal
//						 pc.setPC(ir.read().getField1()); //Set PC to equal address in field1 of instruction in ir						 
//					 }
//					 break; //If equal, do nothing
//					 
//			
//					 
//			case 13: //A HALT instruction (stops instruction cycle). For clarity, resets all registers.
//					 pc.setPC(0);
//					 statusRegister.write(null);
//					 ir.loadIR(null);	
//					 return false; //Signals end of instruction cycle		
//		}
//		return true;
//	}

}

