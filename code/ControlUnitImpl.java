package code;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.locks.ReentrantLock;

public class ControlUnitImpl implements ControlUnit {
	private boolean pipeliningMode;
	private boolean active; //True while there are still instructions to fetch and execute; HALT instruction being decoded
							//sets this to false, stopping execution in standard mode. Not used for pipelined mode. 
	
	private ProgramCounter pc;	
	private InstructionRegister ir;
	private MemoryBufferRegister mbr;
	private MemoryAddressRegister mar;	
	private RegisterFile genRegisters;
	private Register statusRegister;
	
	private BusController systemBus;
	
	private FetchDecodeStage fetchDecodeStage;
	private ExecuteStage executeStage;
	private WriteBackStage writeBackStage;
	
	private BlockingQueue<Instruction> fetchToExecuteQueue;//Queues that facilitate coordination of stages of pipelined execution
	private BlockingQueue<Instruction> executeToWriteQueue; 
	
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

			fetchDecodeStage = new StandardFetchDecodeStage(this.systemBus, ir, pc, genRegisters, statusRegister, this.mbr, mar);
			writeBackStage = new StandardWriteBackStage(this.systemBus, ir, pc, genRegisters, statusRegister, this.mbr, mar);
			executeStage = new StandardExecuteStage(this.systemBus, ir, pc, genRegisters, statusRegister,
					this.mbr, mar, writeBackStage);	
		}
		
		if (pipeliningMode) { 
			ir = new IRfile();
			
			//Queues only required if pipelining is enabled; they are used to coordinate the stages by making each 
			//stage wait until it receives the instruction from the previous stage before executing. This also
			//allows instructions to be passed from one index of the IRfile to the next in a controlled manner.
			fetchToExecuteQueue = new SynchronousQueue<Instruction>();
			executeToWriteQueue = new SynchronousQueue<Instruction>();
			
			fetchDecodeStage = new PipelinedFetchDecodeStage(this.systemBus, ir, pc, genRegisters, statusRegister,
					this.mbr, mar, fetchToExecuteQueue);
			
			writeBackStage = new PipelinedWriteBackStage(this.systemBus, ir, pc, genRegisters, statusRegister,
					this.mbr, mar, executeToWriteQueue);
			
			executeStage = new PipelinedExecuteStage(this.systemBus, ir, pc, genRegisters, statusRegister,
					this.mbr, mar, fetchToExecuteQueue, executeToWriteQueue, fetchDecodeStage, writeBackStage);
			
		}
		
	}
	
	
	@Override
	public void activate() { 
		this.active = true;
		this.launch();
	}
	
	
	private void launch() { //The method that kick starts execution of a program and manages it in standard mode
		pc.setPC(0); //Initialise PC to 0 for GUI display
		if (!pipeliningMode) { 
			while (active) {
				System.out.println("In active while loop for non pipelined mode");
				
				fetchDecodeStage.run();
				int opcode = fetchDecodeStage.getOpcodeValue();
				if (opcode == -1) { //fetchDecodeStage.getOpcodeValue() returns -1 if interrupted, meaning SwingWorker.cancel()
					//has been called from CPUframe ("reset" clicked on GUI). Execution should not continue as a result.
					this.active = false;
				}
				else {
					executeStage.setOpcodeValue(opcode);
					executeStage.run();//Note that writeBackStage is called from executeStage if necessary
					this.active = executeStage.isActive();
				}				
			}
			
			//If interrupted by a "reset" during Stage's accessMemory() method, the lock must be released.
			if (((ReentrantLock) Stage.getLock()).isHeldByCurrentThread()) {
				Stage.getLock().unlock();
			}
			
		}
		
		else if (pipeliningMode) {
			/*
			 * The SwingWorker thread activated from the GUI runs in the executeStage, and
			 * creates two threads, one to run in the F/D Stage and the other to run in the
			 * WB Stage. If "reset" is clicked, issuing an interrupt to the SwingWorker thread,
			 * it interrupts the other two threads (which results in their exiting from their respective
			 * run() methods asap) prior to terminating itself by exiting its run() method as quickly as possible.
			 * 
			 * The SwingWorker thread also issues an interrupt to the thread running in the F/D Stage
			 * if a branch instruction is executed, causing that thread to exit its run() method, before restarting
			 * it.
			 */				
			executeStage.run(); //This manages the fetch and write back stages
	
		}
	}
	
	
	
	public void clearRegisters() {
		pc.setPC(0);
		ir.clear();
		mar.write(-1); // -1 triggers clear
		mbr.write(null);
		for (int i = 0; i < 16; i++) {
			genRegisters.write(i, null); //Set each general purpose register to null		
		}
		statusRegister.write(null);		
	}
	
	
	@Override
	public InstructionRegister getIR() { 
		return this.ir;
	}
	
	@Override
	public ProgramCounter getPC() {
		return this.pc;
	}
	
	@Override
	public RegisterFile getRegisters() {
		return this.genRegisters;
	}
	
	@Override
	public Register getStatusRegister() {
		return this.statusRegister;
	}

	@Override
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
