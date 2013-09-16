package code;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.SwingUtilities;

public class PipelinedFetchDecodeStage extends FetchDecodeStage {
	private BlockingQueue<Instruction> fetchToExecuteQueue;
	
	private UpdateListener updateListener;

	public PipelinedFetchDecodeStage(BusController systemBus, InstructionRegister ir, ProgramCounter pc,
			RegisterFile genRegisters, Register rCC, MemoryBufferRegister mbr, MemoryAddressRegister mar, 
			BlockingQueue<Instruction> fetchToExecuteQueue) {
		
		super(systemBus, ir, pc, genRegisters, rCC, mbr, mar);
		this.fetchToExecuteQueue = fetchToExecuteQueue;	

	}
	
	
	/*
	 * This thread will be interrupted by the SwingWorker thread running in the Ex. Stage in the event of
	 * a pipeline flush brought on by a branch instruction, or in the event that the user clicks "reset"
	 * on the GUI, which interrupts the SwingWorker thread, which interrupts this thread in turn, causing
	 * it to cease fetching/decoding instructions, return to the run() method and finally exit that method,
	 * effectively terminating this thread (which can then be restarted by the SwingWorker in the event of a
	 * pipeline flush, or if the user decides to restart execution after clicking "reset").
	 * 
	 * Interrupted status must be polled continuously to check for an interrupt originating 
	 * from execute stage when wait() statements are absent (during testing).
	 */
	public boolean instructionFetch() {
		
		//Fetch requires access to MAR, MBR and memory;see Stage super class for details of fetch.
		boolean successful = accessMemory(true, false, false, true); 
		return successful;
		
	}
	//Fetch ends with instruction being loaded into IR.
	
	/*
	 * Similarly to the fetch method above, the interrupted status of the thread must be continuously
	 * polled to check for an interrupt caused by a pipeline flush originating in execute stage, or
	 * from the user clicking "rest" on the GUI (which also causes an interrupt to be issued).
	 */
	public int instructionDecode() { //Returns int value of opcode
		Instruction instr = getIR().read();
		int opcodeValue = instr.getOpcode().getValue(); //Gets instruction opcode as int value
		
		if (Thread.currentThread().isInterrupted()) { 
			return -1; //Do not continue execution if interrupted (pipeline flush)
		}
		
		
		if (Thread.currentThread().isInterrupted()) { 
			return -1;
		}
		
		setWaitStatus(true);
		try {
			wait();
		} catch (InterruptedException e) {
		//	e.printStackTrace();
			setWaitStatus(false);
			return -1; //Do not continue execution if interrupted by Ex. stage running the SwingWorker thread; this means
			//reset has been clicked on GUI. -1 signals this thread return from run() method (which called this method).
		}
		setWaitStatus(false);		
		
		return opcodeValue;
		
	}
	
	@Override
	public synchronized void run() { //Synchronized to enable step execution

		setActive(true);
		setPipelineFlush(false); //If set to true in an earlier cycle, needs resetting
		
		while (isActive()) { //Continue fetching instructions		
			setActive(this.instructionFetch()); //Set to false if interrupted, and execution is cancelled below
			
			if (!isActive() || isPipelineFlush()) { //This will happen if an interrupt takes places within instructionFetch()
				if (isPipelineFlush()) {
					fireUpdate("** PIPELINE FLUSH ** \nFetch/decode of next sequential instruction abandoned.\n");		
				}
				
				if (((ReentrantLock) getLock()).isHeldByCurrentThread()) {//If interrupted during accessMemory(), must release lock
					getLock().unlock();
				}
				return;
			}
			this.setOpcodeValue(this.instructionDecode());
			if (this.getOpcodeValue() == -1) { //Signals interrupted wait() or pipeline flush; execution should be cancelled
				if (isPipelineFlush()) {
					fireUpdate("** PIPELINE FLUSH ** \nFetch/decode of next sequential instruction at address abandoned.\n");
					
				}
				setActive(false);
				return; //Terminates this thread
			}
			boolean forwardSuccessful = this.forward();
			if (!forwardSuccessful) { //Interrupt detected, meaning reset clicked or HALT decoded; cancel execution of THIS stage.
				if (isPipelineFlush()) {
					fireUpdate("** PIPELINE FLUSH ** \nFetch/decode of next sequential instruction abandoned.\n");		
				}
				setActive(false);
				return;
			}
		}
		return;
	}
	

	/*
	 * A method for forwarding the fetched and decoded instruction to the execute stage.
	 * 
	 * @return boolean false if interrupted by a reset, pipeline flush, or if HALT is decoded
	 */
	public boolean forward() {
		Integer opcode = this.getOpcodeValue(); //Get opcode value from superclass to pass to queue
		try {
			getPC().incrementPC();//For testing, as wait() statements are removed, this needs to be placed after queue.put()
									//in order to prevent missing branch targets.
			this.fireUpdate("> PC incremented by 1 (ready for next instruction fetch) \n");
			
			this.fireUpdate("> Waiting for Ex. Stage to take instruction " + getIR().read(0) + ".\n");
			
			//Waits here until put is successful (executeStage thread must be attempting take()).
			fetchToExecuteQueue.put(getIR().read(0)); 
			
			getIR().clear(0); //Rest IR index 0
			
			//Don't attempt to fetch another instruction after HALT fetched. Execute stage won't send signal in time.
			if (opcode == 13) {
				return false;
			}
			return true;
		} catch (InterruptedException e) {
			//e.printStackTrace();
			setActive(false);
			return false;
		}
	}
	//If this stage is waiting on a put(), and branch is taken, the put should be interrupted, and this stage is 
	//deactivated.
	

	
	
	//GUI events should not be handled from this thread but from EDT
	//This adds the update event to the EDT thread. 
	@Override
	public void fireUpdate(final String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(PipelinedFetchDecodeStage.this, update);
				updateListener.handleUpDateEvent(updateEvent);	
			}
		});
	}
	
	
	@Override
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;
	}
}
