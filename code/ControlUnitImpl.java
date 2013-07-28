package code;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;


/*
 * Take methods out ouf the stages; standard mode will not use the stages at all, and just work 
 * through the methods. In pipelining mode, the stages are created, and these stages call methods
 * from the main ControlUnitImpl container class. This allows code reuse, and simplifies things.
 * 
 * Clear registers after use; i.e. MBR should hold nothing once instruction passed to bus / ir
 */
public class ControlUnitImpl implements ControlUnit {
	private boolean pipeliningMode;
	private boolean active; //True while there are still instructions to fetch and execute; HALT instruction being decoded
							//should set this to false, stopping execution. 
	
	private ProgramCounter pc;	
	private InstructionRegister ir;	//An instruction register file / cache (containing at least 2 registers) for pipelining mode?
	private MemoryBufferRegister mbr;
	private MemoryAddressRegister mar;	
	private RegisterFile genRegisters;
	private Register statusRegister;
	
	private BusController systemBus;
	
	private InstructionCycleStage fetchDecodeStage;
	private InstructionCycleStage executeStage;
	private InstructionCycleStage writeStage;
	
	private BlockingQueue<Integer> fetchToExecuteQueue; //Queues that facilitate coordination of stages of instruction cycle
	private BlockingQueue executeToWriteQueue; //Only during pipelining
	
	public ControlUnitImpl(boolean pipeliningMode) {
		this.pipeliningMode = pipeliningMode;
		
		systemBus = SystemBusController.getInstance();
		
		mbr = MBR.getInstance();
		mar = MAR.getInstance();
		genRegisters = new RegisterFile16();
		pc = new PC();
		ir = new IR();
		statusRegister = new StatusRegister();
		
		fetchDecodeStage = new FetchDecodeStage();
		executeStage = new ExecuteStage();
		
		if (pipeliningMode) { //Queues only required if pipelining enabled
			fetchToExecuteQueue = new SynchronousQueue<Integer>();
		}
		
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public void activate() { //Should be called when initial address is loaded into PC
		this.active = true;
	}
	
	public void launch() { //The method that kick starts execution of a program, and manages it
		if (!pipeliningMode) { 
			while (active) {
				this.instructionFetch(); //Fetches instruction, then calls decode, which calls execute etc.
				int opcode = this.instructionDecode();
				this.instructionExecute(opcode);
			}
		}
		

	}
	
	public void instructionFetch() {
		mar.write(pc.getValue()); //Write address value in PC to MAR.
		systemBus.transferToMemory(mar.read(), null); //Transfer address from MAR to system bus, prompting read
		mar.write(0);//Reset MAR (but is it more confusing to place 0 there, as 0 is valid memory address?
		
		//A Data item should now be in MBR
		ir.loadIR((Instruction) mbr.read()); //Cast required as mbr holds type data, IR type Instruction; May need to handle exception
		
		mbr.write(null); //Clear MBR to reflect that instruction has moved to IR (should it be reset earlier, to better reflect
		//movement?)
		
		//this.instructionDecode();
	}
	//Fetch ends with instruction being loaded into IR.
	
	
	//Should this also retrieve references to operand locations? (and pass them as parameters to instructionExecute())
	public int instructionDecode() { //Returns int value of opcode
		Instruction instr = ir.read();
		int opcodeValue = instr.getOpcode().getValue(); //Gets instruction opcode as int value
		pc.incrementPC(); //Increment PC; done here so that with pipelining, the next instruction can be fetched at this point
		
		return opcodeValue;
		
		//this.instructionExecute(opcodeValue);
		//Add interim references to operand locations, to pass to execute stage?
		
	}
	
	public void instructionExecute(int opcode) {
		switch (opcode) {
		
			case 1: //A LOAD instruction
					mar.write(ir.read().getField1()); //Load mar with source address of instruction in IR
					//Request a read from memory via system bus, with address contained in mar
					systemBus.transferToMemory(mar.read(), null);
					
					mar.write(0); //Reset MAR
					
					//Transfer data in mbr to destination field in instruction in ir (field2).
					genRegisters.write(ir.read().getField2(), mbr.read());//getField2() gives reg. destination index, mbr.read()
					//gives the operand to be moved from mbr to genRegisters at index given in getField2().
					
					mbr.write(null); //Reset MBR
					//Reset IR?
					break;
					
			case 2: //A STORE instruction
					mar.write(ir.read().getField2()); //Load mar with destination (memory address)
					mbr.write(genRegisters.read(ir.read().getField1())); //Write to mbr the data held in genRegisters at index
					//given by field1(source) of instruction held in IR.
					systemBus.transferToMemory(mar.read(), mbr.read()); //Transfer contents of mbr to address specified in mar
					break;
					
			case 3: //A MOVE instruction (moving data between registers)
					genRegisters.write(ir.read().getField2(), genRegisters.read(ir.read().getField1()));
					//Write to the destination specified in field2 of instr held in ir, the instr held in the register
					//specified by field1 of the instruction in the ir.
					genRegisters.write(ir.read().getField1(), null); //Complete the move by resetting register source
					break;
					
					
					
			case 4: //An ADD instruction (adding contents of one register to second (storing in the first).
					Operand op1 = (Operand) genRegisters.read(ir.read().getField1()); //access operand stored in first register
					Operand op2 = (Operand) genRegisters.read(ir.read().getField2());//access operand stored in second register
					Operand result = ALU.AdditionUnit(op1, op2); //Have ALU perform operation
					this.instructionWriteBack(result); //Call write back stage to store result of addition
					break;
			
			case 5: //A SUB instruction (subtracting contents of one register from a second (storing in the first).
					op1 = (Operand) genRegisters.read(ir.read().getField1()); //access first operand
					op2 = (Operand) genRegisters.read(ir.read().getField2()); //access second operand
					result = ALU.SubtractionUnit(op1, op2);
					this.instructionWriteBack(result);
					break;				
					
			case 6: //A DIV instruction (dividing contents of one register by contents of another (storing in the first).
					op1 = (Operand) genRegisters.read(ir.read().getField1()); //access first operand
					op2 = (Operand) genRegisters.read(ir.read().getField2()); //access second operand
					result = ALU.DivisionUnit(op1, op2); //op1 / op2
					this.instructionWriteBack(result);
					break;					
					
			case 7: //A MUL instruction (multiplying contents of one register by contents of another (storing result in first).
					op1 = (Operand) genRegisters.read(ir.read().getField1()); //access first operand
					op2 = (Operand) genRegisters.read(ir.read().getField2()); //access second operand
					result = ALU.MultiplicationUnit(op1, op2); //op1 * op2
					this.instructionWriteBack(result);
					break;
					
					
					
			case 8: //A BR instruction (unconditional branch to memory location in instruction field 1).
					pc.setPC(ir.read().getField1());
					//A branch instruction updates PC to new memory location
					break;
				
			case 9: //A BRZ instruction (branch if value in status register is zero).
					if (statusRegister.read().unwrapInteger() == 0) {
						pc.setPC(ir.read().getField1()); //If statusRegister holds 0, set PC to new address held in instruction
					}
					break; //If not 0, do nothing
					
			case 10: //A SKZ instruction (skip the next instruction (increment PC by one) if status register holds 0).
					 if (statusRegister.read().unwrapInteger() == 0) {
						 pc.incrementPC();
					 }
					 break; //If not 0, do nothing
					 
			case 11: //A BRE instruction (branch if status reg. contents = contents of register ref. in instruction)
					 int genRegRef = ir.read().getField2(); //Reference to register referred to in instruction
					 if (statusRegister.read().equals((Operand) genRegisters.read(genRegRef))) { //If equal
						 pc.setPC(ir.read().getField1()); //Set PC to equal address in field1 of instruction in ir						 
					 }
					 break; //If not equal, do nothing
					 
			case 12: //A BRNE instruction (branch if status reg. contents != contents of register ref. in instruction)
					 genRegRef = ir.read().getField2(); //Reference to register referred to in instruction
					 if (!(statusRegister.read().equals((Operand) genRegisters.read(genRegRef)))) { //If not equal
						 pc.setPC(ir.read().getField1()); //Set PC to equal address in field1 of instruction in ir						 
					 }
					 break; //If equal, do nothing
					 
			
					 
			case 13: //A HALT instruction (stops instruction cycle). For clarity, resets all registers.
					 pc.setPC(0);
					 statusRegister.write(null);
					 ir.loadIR(null);	
					 active = false; //Signals end of instruction cycle
					 break;		
		}
		return;
	}
	

	
		//what about data loaded into MBR that is data (operand) as opposed to instruction; loaded straight to a register
		//http://comminfo.rutgers.edu/~muresan/201_JavaProg/11CPU/Lecture11.pdf
		//have methods to represent storeExecuteCycle, loadExecuteCycle etc, depending on decode of fetched instruction
		//This is instruction fetch -> only covers fetching of an instruction, not its execution; fetching data from memory
		//will be part of executing a LOAD instruction, which occurs in execute. Decode determines nature of instruction; 
		//instructionDecode() method.
	
	
	//Writing of results to register file
	public void instructionWriteBack(Operand result) { //Not required in every cycle
		//It is implicit in the nature of arithmetic instructions that the result is stored in the register
		//referenced in the first field of the instruction after the opcode (field1)
		genRegisters.write(ir.read().getField1(), result);
	}
	
	
	
	
	
	
	
	public class FetchDecodeStage implements InstructionCycleStage {	
		private boolean running;
		
		public void run() {
			running = true;
			
			while (running) { //Fetching/decoding must be in a loop, as this continues until HALT instruction
			
				instructionFetch();	
				int opcode = instructionDecode();
				
				try {
					fetchToExecuteQueue.put(opcode); //Program will wait until executeStage attempts take()
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (opcode == 13) { //A HALT instruction; another fetch after this should not be attempted
					running = false;
				}
			}
		}	
		
	}
		
		
	public class ExecuteStage implements InstructionCycleStage {
		private int opcodeValue;
		private boolean active;//For pipelining mode; prompts the thread to keep attempting to retrieve an opcode from the queue
		//while execution of a program continues
		
		public void run() {
			if (pipeliningMode) {
				while (active) {
					this.accessBlockingQueue();
					
				}
				
			}
			else { //For standard mode
				
			}
			
		}
		
		public void accessBlockingQueue() {
			while (opcodeValue == 0) { //No opcode with value of 0; thread attempts to take from queue until it receives an opcode 
				try {
					opcodeValue = fetchToExecuteQueue.take();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return;
		}
		
		public void receiveOpcode(int opcodeValue) {
			this.opcodeValue = opcodeValue;
			this.run(); //Enter main execution of this stge
		}
			
			
	}
		

		
		
	
	//Stages represented by methods --> these methods should be embedded within objects of type Stage
	//This allows for pipelining to be more easily implemented later.
	
	public InstructionRegister getIR() { //For testing
		return this.ir;
	}
	
	public ProgramCounter getPC() {
		return this.pc;
	}
	
	public RegisterFile getRegisters() {
		return this.genRegisters;
	}
	
	public Register getStatusRegister() {
		return this.statusRegister;
	}
	
	
	


}
