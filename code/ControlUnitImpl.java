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
	
	public void execute() {
		//InstructionCycleStage fetchStage = new FetchDecodeStage();

	}
	
	public class FetchDecodeStage implements InstructionCycleStage {
		//BlockingQueue field -> can hold type Boolean as a marker
	
		
		public void run() {
			this.instructionFetch();	
			this.instructionDecode();
		}
		
		
		public void instructionFetch() {
			mar.write(pc.getValue()); //Write address value in PC to MAR.
			systemBus.transferToMemory(mar.read(), null); //Transfer address from MAR to system bus, prompting read
			//A Data item should now be in MBR
			ir.loadIR((Instruction) mbr.read()); //Cast required as mbr holds type data, IR type Instruction; May need to handle exception
		} //Fetch ends with instruction being loaded into IR.
		
		
		public void instructionDecode() {
			Instruction instr = ir.read();
			int opcodeValue = instr.getOpcode().getValue(); //Gets instruction opcode as int value
			
			switch (opcodeValue) {
			
				case 1: //A LOAD instruction: 1) Source address portion of instruction in IR loaded to MAR
						if (pipeliningMode) { //Pass opcode value via queue to next stage
							fetchToExecuteQueue.add(opcodeValue); //"Passes" opcode value to queue for next stage's thread to take
						}
						else {
							executeStage.receiveOpcode(opcodeValue);
						}
						break;
						
			//If pipelining mode enabled, don't use blocking queue to pass to next stage (won't work for a single thread)
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
		
//		LOAD(1), STORE(2), MOVE(3),
//		ADD(4), SUB(5), DIV(6), MUL(7),
//		BR(8), BRZ(9), BRE(10), BRNE(11),
//		SK(12), ISZ(13);
			//what about data loaded into
			//MBR that is data (operand) as opposed to instruction? Is this loaded straight to a register?
			//http://comminfo.rutgers.edu/~muresan/201_JavaProg/11CPU/Lecture11.pdf
			//have methods to represent storeExecuteCycle, loadExecuteCycle etc, depending on decode of fetched instruction
			//This is instruction fetch -> only covers fetching of an instruction, not its execution; fetching data from memory
			//will be part of executing a LOAD instruction, which occurs in execute. Decode determines nature of instruction; 
			//instructionDecode() method.
		
		
	
	//Stages represented by methods --> these methods should be embedded within objects of type Stage
	//This allows for pipelining to be more easily implemented later.
	
	public void instructionFetch() {
		//Code for instruction fetch stage
	}
	
	public void instructionDecode() {
		
	}
	
	public void instructionExecute() {
		//Can call other, private methods depending on instruction opcode
		//case switch statement: if arithmetic, call arithmeticInstrExecute(), etc
	}
	
	public void instructionStore() { //Not required in every cycle
		
	}
	


}
