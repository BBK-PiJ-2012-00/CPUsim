package code;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
	
	private FetchDecodeStage fetchDecodeStage;
	private ExecuteStage executeStage;
	private WriteBackStage writeBackStage;
	
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
		
		if (!pipeliningMode) { //If not pipelined, create standard stages
			fetchDecodeStage = new StandardFetchDecodeStage(ir, pc);
			writeBackStage = new StandardWriteBackStage(ir, genRegisters);
			executeStage = new StandardExecuteStage(ir, pc, genRegisters, statusRegister, writeBackStage);			
		}
		
		if (pipeliningMode) { //Queues only required if pipelining enabled
			fetchToExecuteQueue = new SynchronousQueue<Integer>();
		}
		
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public void activate() { 
		this.active = true;
		this.launch();
	}
	
	public void deactivate() {
		this.active = false;
	}
	
	private void launch() { //The method that kick starts execution of a program, and manages it
		if (!pipeliningMode) { 
			while (active) {	
				
				//fetchDecodeStage.instructionFetch();
				//int opcode = fetchDecodeStage.instructionDecode();
				
				fetchDecodeStage.run();
				int opcode = fetchDecodeStage.getOpcodeValue();
				executeStage.setOpcodeValue(opcode);
				executeStage.run();
				this.active = executeStage.isActive();
			
				//this.active = executeStage.instructionExecute(opcode); //Returns false if HALT encountered
				
				
				
				//executeStage has a reference to, and calls if necessary, writeBackStage (only for arithmetic operations).
			}
		}
	}
	
	/*
	 * If the main thread running through this method controls worker threads in the run method, prompting it
	 * to pause at every stage, step by step execution could be implemented.  Calling next simply wakes the 
	 * worker thread in launch, which puts itself to sleep at selected intervals.
	 */
	public void next() {
		
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

	
	public synchronized FetchDecodeStage getFetchDecodeStage() {
		return this.fetchDecodeStage;
	}

	@Override
	public ExecuteStage getExecuteStage() {
		return this.executeStage;
	}
	
	
	
	
	
	
	
	
	
	
//	public class FetchDecodeStage implements InstructionCycleStage {	
//		private boolean running;
//		private int opcodeValue;
//		
//		public FetchDecodeStage() {
//			this.running = true;
//		}
//		
//		public void run() {
//			running = true;
//			
//			while (running) { //Fetching/decoding must be in a loop, as this continues until HALT instruction
//			
//				instructionFetch();	
//				opcodeValue = instructionDecode();
//				
//				try {
//					fetchToExecuteQueue.put(opcodeValue); //Program will wait until executeStage attempts take()
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				if (opcodeValue == 13) { //A HALT instruction; another fetch after this should not be attempted
//					running = false;
//				}
//			}
//		}	
//		
//	}
//		
//		
//	public class ExecuteStage implements InstructionCycleStage {
//		private int opcodeValue;
//		private boolean running;//For pipelining mode; prompts the thread to keep attempting to retrieve an opcode from the queue
//		//while execution of a program continues
//		
//		public ExecuteStage() {
//			this.running = true;
//		}
//		
//		public void run() {
//			
//			while (running) {
//				
//				try {
//					opcodeValue = fetchToExecuteQueue.take();
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				instructionExecute(opcodeValue);
//				if (active == false) { //This would be the case after execution of HALT instruction
//					running = false; //Don't attempt to execute any further instructions
//				}
//				//Need to coordinate this stage with writeBack stage for arithmetic instructions
//			}
//			
//		}			
//			
//	}
//		
	
	
	


}
