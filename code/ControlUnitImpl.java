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
							//sets this to false, stopping execution. 
	
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
	private BlockingQueue<Operand> executeToWriteQueue; //Only during pipelining
	
	public ControlUnitImpl(boolean pipeliningMode, MemoryBufferRegister mbr, BusController systemBus) {
		this.pipeliningMode = pipeliningMode;
		
		this.systemBus = systemBus;
		
		this.mbr = mbr;
		mar = new MAR();
		genRegisters = new RegisterFile16();
		pc = new PC();
		statusRegister = new StatusRegister();
		
		if (!pipeliningMode) { //If not pipelined, create standard stages
			ir = new IR();
			fetchDecodeStage = new StandardFetchDecodeStage(this.systemBus, mar, this.mbr, ir, pc);
			writeBackStage = new StandardWriteBackStage(ir, genRegisters);
			executeStage = new StandardExecuteStage(this.systemBus, ir, pc, genRegisters, statusRegister, writeBackStage,
					this.mbr, mar);	
		}
		
		if (pipeliningMode) { //Queues only required if pipelining enabled
			ir = new IRfile();
			fetchToExecuteQueue = new SynchronousQueue<Integer>(); //Integer representation of opcode
			executeToWriteQueue = new SynchronousQueue<Operand>(); //Operand to reflect that result of arithmetic is passed
			
			fetchDecodeStage = new PipelinedFetchDecodeStage(this.systemBus, mar, this.mbr, ir, pc, fetchToExecuteQueue);
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
		pc.setPC(0); //Initialise PC to 0 for GUI display
		if (!pipeliningMode) { 
			while (active) {				
				
				fetchDecodeStage.run();
				int opcode = fetchDecodeStage.getOpcodeValue();
				if (opcode == -1) { //fetchDecodeStage.getOpcodeValue() returns -1 if interrupted, meaning SwingWorker.cancel()
					//has been called from CPUframe. Execution should not continue as a result.
					this.active = false;
				}
				else {
					executeStage.setOpcodeValue(opcode);
					executeStage.run();//Note that writeBackStage is called from executeStage if necessary
					this.active = executeStage.isActive();
				}				
			}
		}
		
		else if (pipeliningMode) {
			/*
			 * The main thread should start the three stage threads from there. They will have to manage
			 * themselves from within their respective run() methods.  Returning from a run method should
			 * halt execution, possibly using a boolean value to assist like above, as this is what will happen 
			 * when an interrupt is issued to a wait() if reset is clicked.  
			 */
			
			/*
			 * A better, more manageable solution might be to have the main thread operating the f/d stage,
			 * and have that thread spawn worker threads to perform execute and write back. That way, managing
			 * execution and interrupts and flushing will be much simpler.
			 */
			
//			Thread fetchThread = new Thread(fetchDecodeStage);
//			fetchThread.start();
//			
//			Thread executeThread = new Thread(executeStage);
//			executeThread.start();
//			
//			Thread writeBackThread = new Thread(writeBackStage);
//			writeBackThread.start();
			
			while (active) {
				
				executeStage.run(); //This manages the fetch and write back stages
				
			}
			
			
			
			//What happens if one stage exits due to an interrupt?
			
//			fetchDecodeStage.run();
//			int opcode = fetchDecodeStage.getOpcodeValue();
//			if (opcode == -1) { //fetchDecodeStage.getOpcodeValue() returns -1 if interrupted, meaning SwingWorker.cancel()
//				//has been called from CPUframe. Execution should not continue as a result.
//				this.active = false;
//			}
//			else {
//				//Get fetchDecodeStage to add opcode to the queue
//				//execute stage should be activated above so that it is attempting a take
//				//while (!fetchDecodeStage.forward()); //Keep attempting forward until executeStage takes from queue
//				boolean forwardSuccessful = fetchDecodeStage.forward();
//				executeStage.setOpcodeValue(opcode);
//				executeStage.run();//Note that writeBackStage is called from executeStage if necessary
//				this.active = executeStage.isActive();
//			}				
		}
	}
	
	public void clearRegisters() {
		pc.setPC(0);
		ir.loadIR(null);
		mar.write(-1); // -1 triggers clear
		mbr.write(null);
		for (int i = 0; i < 16; i++) {
			genRegisters.write(i, null); //Set each general purpose register to null, clearing them			
		}
		statusRegister.write(null);		
	}
	
	public void resetStages() { //Used when restarting an assembly program
		if (!pipeliningMode) { //If not pipelined, create standard stages
			fetchDecodeStage = new StandardFetchDecodeStage(this.systemBus, mar, this.mbr, ir, pc);
			writeBackStage = new StandardWriteBackStage(ir, genRegisters);
			executeStage = new StandardExecuteStage(this.systemBus, ir, pc, genRegisters, statusRegister, writeBackStage,
					this.mbr, mar);	
		}
	}
	
	
	
	
	

	
	
	
	

		
	
	
	public InstructionRegister getIR() { 
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

	
	public FetchDecodeStage getFetchDecodeStage() {
		return this.fetchDecodeStage;
	}

	@Override
	public ExecuteStage getExecuteStage() {
		return this.executeStage;
	}

	@Override
	public WriteBackStage getWriteBackStage() {
		return this.writeBackStage;
	}

	@Override
	public MemoryBufferRegister getMBR() {
		return this.mbr;
	}

	@Override
	public MemoryAddressRegister getMAR() {
		return this.mar;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	


}
