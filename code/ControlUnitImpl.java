package code;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;


/*
 * Take methods out ouf the stages; standard mode will not use the stages at all, and just work 
 * through the methods. In pipelining mode, the stages are created, and these stages call methods
 * from the main ControlUnitImpl container class. This allows code reuse, and simplifies things.
 */
public class ControlUnitImpl implements ControlUnit {
	private boolean pipeliningMode;
	private boolean active; //True while there are still instructions to fetch and execute; HALT instruction being decoded
							//should set this to false, stopping execution. (HALT is never executed; only decoded, and 
							//never passed onto execution stage.
	
	private ProgramCounter pc;	
	private InstructionRegister ir;	
	private MemoryBufferRegister mbr;
	private MemoryAddressRegister mar;	
	private RegisterFile genRegisters;
	
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
		
		fetchDecodeStage = new FetchDecodeStage();
		executeStage = new ExecuteStage();
		
		if (pipeliningMode) { //Queues only required if pipelining enabled
			fetchToExecuteQueue = new SynchronousQueue<Integer>();
		}
		
	}
	
	public void execute() { //The method that kick starts execution of a program, and manages it
		if (!pipeliningMode) { //Consider HALT instruction and how to terminate execution
			this.instructionFetch(); //Fetches instruction, then calls decode, which calls execute etc.
			
		}
		

	}
	
	public void instructionFetch() {
		mar.write(pc.getValue()); //Write address value in PC to MAR.
		systemBus.transferToMemory(mar.read(), null); //Transfer address from MAR to system bus, prompting read
		//A Data item should now be in MBR
		ir.loadIR((Instruction) mbr.read()); //Cast required as mbr holds type data, IR type Instruction; May need to handle exception
		this.instructionDecode();
	}
	//Fetch ends with instruction being loaded into IR.
	
	
	
	public void instructionDecode() { //Returns int value of opcode
		Instruction instr = ir.read();
		int opcodeValue = instr.getOpcode().getValue(); //Gets instruction opcode as int value
		pc.incrementPC(); //Increment PC; done here so that with pipelining, the next instruction can be fetched at this point
		this.instructionExecute(opcodeValue);
		
	}
	
	public void instructionExecute(int opcode) {
		switch (opcode) {
		
			case 1: //A LOAD instruction
					mar.write(ir.read().getField1()); //Load mar with source address of instruction in IR
					//Request a read from memory via system bus, with address contained in mar
					systemBus.transferToMemory(mar.read(), null);
					//Transfer data in mbr to destination field in instruction in ir (field2).
					genRegisters.write(ir.read().getField2(), mbr.read());//getField2() gives reg. destination index, mbr.read()
					//gives the operand to be moved from mbr to genRegisters at index given in getField2().
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
				
	//If pipelining mode enabled, don't use blocking queue to pass to next stage (won't work for a single thread)
		}
		//Can call other, private methods depending on instruction opcode
		//case switch statement: if arithmetic, call arithmeticInstrExecute(), etc
	}
	

//	ADD(4), SUB(5), DIV(6), MUL(7),
//	BR(8), BRZ(9), BRE(10), BRNE(11),
//	SK(12), ISZ(13);
		//what about data loaded into MBR that is data (operand) as opposed to instruction; loaded straight to a register
		//http://comminfo.rutgers.edu/~muresan/201_JavaProg/11CPU/Lecture11.pdf
		//have methods to represent storeExecuteCycle, loadExecuteCycle etc, depending on decode of fetched instruction
		//This is instruction fetch -> only covers fetching of an instruction, not its execution; fetching data from memory
		//will be part of executing a LOAD instruction, which occurs in execute. Decode determines nature of instruction; 
		//instructionDecode() method.
	
	public void instructionStore() { //Not required in every cycle
		
	}
	
	public class FetchDecodeStage implements InstructionCycleStage {
		//BlockingQueue field -> can hold type Boolean as a marker
	
		
		public void run() {
			//this.instructionFetch();	
			//this.instructionDecode();
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
	
	
	


}
