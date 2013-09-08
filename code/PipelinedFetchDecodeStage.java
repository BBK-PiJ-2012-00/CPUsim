package code;

import java.util.concurrent.BlockingQueue;

import javax.swing.SwingUtilities;

public class PipelinedFetchDecodeStage extends FetchDecodeStage {
	private BlockingQueue<Integer> fetchToExecuteQueue;
	//private boolean active;
	//private boolean pipelineFlush;


	public PipelinedFetchDecodeStage(BusController systemBus, InstructionRegister ir, ProgramCounter pc,
			RegisterFile genRegisters, Register statusRegister, MemoryBufferRegister mbr, MemoryAddressRegister mar, 
			BlockingQueue<Integer> fetchToExecuteQueue) {
		
		super(systemBus, ir, pc, genRegisters, statusRegister, mbr, mar);
		this.fetchToExecuteQueue = fetchToExecuteQueue;	

	}
	
	/*
	 * Interrupted status must be polled continuously to check for a pipeline flush originating 
	 * from execute stage.
	 */
	public void instructionFetch() {
		
		accessMemory(true, false, false); //Fetch requires access to MAR, MBR and memory; use synchronized block to do this
										//See Stage super class for details.
		
//		System.out.println("Entering instructionFetch()");
//		this.fireUpdate("\n** INSTRUCTION FETCH/DECODE STAGE ** \n");
//		//getIR().clear(); //Clear previous instruction from display
//		
//		if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
//			System.out.println("Entering interrupted block 1");
//			fireUpdate("**Branch taken in execute stage; pipeline flush. Current instruction \nfetch/decode abandoned.");
//			pipelineFlush = true;
//			return;
//		}
//		
//		getMAR().write(getPC().getValue()); //Write address value in PC to MAR.
//		
//		if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
//			fireUpdate("**Branch taken in execute stage; pipeline flush. Current instruction \nfetch/decode abandoned.");
//			pipelineFlush = true;
//			return;
//		}
//		
//		fireUpdate("Memory address from PC placed into MAR \n");
//		
////		setWaitStatus(true);
////		try {
////			wait();
////		} catch (InterruptedException e) {
////			e.printStackTrace();
////			setWaitStatus(false);
////			active = false;
////			return; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
////		}
////		setWaitStatus(false);
//		
//		if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
//			fireUpdate("Branch taken in execute stage; pipeline flush. Current instruction \nfetch/decode abandoned.");
//			pipelineFlush = true;
//			return;
//		}
//		
//		//Transfer address from MAR to system bus, prompting read
//		boolean successfulTransfer = getSystemBus().transferToMemory(getMAR().read(), null); 
//		if (!successfulTransfer) { 
//			//If SwingWorker is cancelled and thread of execution is interrupted, successfulTransfer will be false and the
//			//method should not execute any further
//			active = false;
//			return;
//		}
//		//Flushing during system bus operation? 
//		
//		if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
//			fireUpdate("Branch taken in execute stage; pipeline flush. Current instruction \nfetch/decode abandoned.");
//			pipelineFlush = true;
//			return;
//		}
//		
//		this.fireUpdate("Load contents of memory address " + getMAR().read() + " into MBR \n");
//		
//		
//		
////		setWaitStatus(true);
////		try {
////			wait();
////		} catch (InterruptedException e) {
////			e.printStackTrace();
////			setWaitStatus(false);
////			return; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
////		}
////		setWaitStatus(false);
//		
//		if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
//			fireUpdate("**Branch taken in execute stage; pipeline flush. Current instruction \nfetch/decode abandoned.");
//			pipelineFlush = true;
//			return;
//		}
//		
//		
//		
//		System.out.println("Ln102, attempting to cast: " + getMBR().read());
//		//A Data item should now be in MBR
//		getIR().loadIR((Instruction) getMBR().read()); //Cast required as mbr holds type data, IR type Instruction; May need to handle exception
//		System.out.println("Instruction: " + getMBR().read());
//		this.fireUpdate("Load contents of MBR into IR \n");
//		
//		if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
//			fireUpdate("**Branch taken in execute stage; pipeline flush. Current instruction \nfetch/decode abandoned.");
//			pipelineFlush = true;
//			return;
//		}
//		
////		setWaitStatus(true);
////		try {
////			wait();
////		} catch (InterruptedException e) {
////			e.printStackTrace();
////			setWaitStatus(false);
////			return; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
////		}
////		setWaitStatus(false);
//		
//		getMAR().write(-1);//Reset MAR. Repositioned here for user clarity; mem. addr. remains in MAR until instr. in IR.		
//		getMBR().write(null); //Clear MBR to reflect that instruction has moved to IR (should it be reset earlier, to better reflect
//		//movement?)
//		
	}
	//Fetch ends with instruction being loaded into IR.
	
	/*
	 * Similarly to the fetch method above, the interrupted status of the thread must be continuously
	 * polled to check for an interrupt caused by a pipeline flush originating in execute stage.
	 */
	public int instructionDecode() { //Returns int value of opcode
		Instruction instr = getIR().read();
		System.out.println("Instruction fetched and about to be decoded is: " + instr.toString());
		int opcodeValue = instr.getOpcode().getValue(); //Gets instruction opcode as int value
		
		if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
			fireUpdate("**Branch taken in execute stage; pipeline flush. Current instruction \nfetch/decode abandoned.");
			setPipelineFlush(true);
			return -1; //Do not continue execution if interrupted (pipeline flush)
		}
		
		getPC().incrementPC(); //Increment PC; done here so that with pipelining, the next instruction can be fetched at this point
		this.fireUpdate("PC incremented by 1 (ready for next instruction fetch) \n");
		
		if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
			fireUpdate("**Branch taken in execute stage; pipeline flush. Current instruction \nfetch/decode abandoned.");
			setPipelineFlush(true);
			return -1;
		}
		
//		setWaitStatus(true);
//		try {
//			wait();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//			setWaitStatus(false);
//			return -1; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called). -1 signals to control
//			//unit to stop execution.
//		}
//		setWaitStatus(false);		
		
		return opcodeValue;
		
	}
	
	@Override
	public synchronized void run() { //Synchronized to enable step execution
//		if (!executeThread.isAlive()) {
//			executeThread.start(); //Only start thread if it's not already alive
//		}
		System.out.println("Starting run() in FDstage");
		setActive(true);
		setPipelineFlush(false);
		while (isActive()) { //Continue fetching instructions		
			this.instructionFetch();
			if (!isActive() || isPipelineFlush()) { //This will happen if an interrupt takes places within instructionFetch()
				System.out.println("PipelineFlush: " + isPipelineFlush());
				System.out.println("About to call return before getting to decode() from within run()");
				return;
			}
			this.setOpcodeValue(this.instructionDecode());
			if (this.getOpcodeValue() == -1) { //Signals interrupted wait(); execution should be cancelled
				setActive(false);
				return; //Additional boolean may need to be set so controlUnit knows what's going on
			}
			boolean forwardSuccessful = this.forward();
			if (!forwardSuccessful) { //Interrupt meaning reset clicked or HALT decoded; cancel execution of THIS stage.
				setActive(false);
				return;
			}
		}
	}


	public boolean forward() {
		Integer opcode = this.getOpcodeValue(); //Get opcode value from superclass to pass to queue
		try {
			System.out.println("About to put");
			if (isPipelineFlush()) {
				//Don't offer to queue; will possibly result in execution of instruction that should be skipped
				return false;
			}
			fetchToExecuteQueue.put(opcode); //Waits here until put is successful (executeStage thread must be attempting take()).
			System.out.println("Put successful");
			((IRfile) getIR()).shuntContents(0, 1); //Move contents of IR register 0 to IR register 1 for use by execute stage
			if (opcode == 13) { //Don't attempt to fetch another instruction after HALT fetched. Execute stage won't send signal in time.
				System.out.println("HALT, so no more fetching");
				return false;
			}
			return true;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setActive(false);
			return false;
		}
	}
	//If this stage is waiting on a put(), and branch is taken, the put should be interrupted, and this stage is 
	//deactivated.
	
//	public boolean isActive() {
//		return this.active;
//	}
	
	/*
	 * There must be a way to differentiate between interrupts generated by pipelining flushing
	 * and those generated by the user clicking reset; in the former case, execution must continue,
	 * with the fetch/decode stage simply resetting itself, in the latter case execution should terminate
	 * altogether.
	 * 
	 */
//	@Override
//	public void setPipelineFlush(boolean isFlush) { //For pipeline flushing
//		this.pipelineFlush = isFlush;
//		System.out.println("PIPELINE FLUSH TRIGGERED");
//	}
	
//	public boolean flushed() {
//		return this.pipelineFlush;
//	}
	
	//GUI events should not be handled from this thread but from EDT or SwingWorker
	//This adds the update event to the EDT thread. Need to test this works on the GUI
	@Override
	public void fireUpdate(final String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(PipelinedFetchDecodeStage.this, update);
				getUpdateListener().handleUpDateEvent(updateEvent);	
			}
		});
	}
}
