package code;

import java.util.concurrent.BlockingQueue;

import javax.swing.SwingUtilities;

public class PipelinedFetchDecodeStage extends FetchDecodeStage {
	private BlockingQueue<Integer> fetchToExecuteQueue;
	private boolean active;
	private boolean pipelineFlush;


	public PipelinedFetchDecodeStage(BusController systemBus,
			MemoryAddressRegister mar, MemoryBufferRegister mbr,
			InstructionRegister ir, ProgramCounter pc, BlockingQueue<Integer> fetchToExecuteQueue) {
		
		super(systemBus, mar, mbr, ir, pc);
		this.fetchToExecuteQueue = fetchToExecuteQueue;	

	}
	
	/*
	 * Interrupted status must be polled continuously to check for a pipeline flush originating 
	 * from execute stage.
	 */
	public void instructionFetch() {
		System.out.println("Entering instructionFetch()");
		this.fireUpdate("\n** INSTRUCTION FETCH/DECODE STAGE ** \n");
		getIR().clear(); //Clear previous instruction from display
		
		if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
			System.out.println("Entering interrupted block 1");
			fireUpdate("**Branch taken in execute stage; pipeline flush. Current instruction \nfetch/decode abandoned.");
			pipelineFlush = true;
			return;
		}
		
		System.out.println("Left interrupted block 1: pipelineFlush = " + pipelineFlush);
		
		getMAR().write(getPC().getValue()); //Write address value in PC to MAR.
		
		if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
			fireUpdate("**Branch taken in execute stage; pipeline flush. Current instruction \nfetch/decode abandoned.");
			//pipelineFlush = true;
			return;
		}
		
		fireUpdate("Memory address from PC placed into MAR \n");
		
//		setWaitStatus(true);
//		try {
//			wait();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//			setWaitStatus(false);
//			active = false;
//			return; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
//		}
//		setWaitStatus(false);
		
		if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
			fireUpdate("Branch taken in execute stage; pipeline flush. Current instruction \nfetch/decode abandoned.");
			//pipelineFlush = true;
			return;
		}
		
		//Transfer address from MAR to system bus, prompting read
		boolean successfulTransfer = getSystemBus().transferToMemory(getMAR().read(), null); 
		if (!successfulTransfer) { 
			//If SwingWorker is cancelled and thread of execution is interrupted, successfulTransfer will be false and the
			//method should not execute any further
			active = false;
			return;
		}
		
		if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
			fireUpdate("Branch taken in execute stage; pipeline flush. Current instruction \nfetch/decode abandoned.");
			//pipelineFlush = true;
			return;
		}
		
		this.fireUpdate("Load contents of memory address " + getMAR().read() + " into MBR \n");
		
		
//		setWaitStatus(true);
//		try {
//			wait();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//			setWaitStatus(false);
//			return; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
//		}
//		setWaitStatus(false);
		
		if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
			fireUpdate("**Branch taken in execute stage; pipeline flush. Current instruction \nfetch/decode abandoned.");
			//pipelineFlush = true;
			return;
		}		
		
		//A Data item should now be in MBR
		getIR().loadIR((Instruction) getMBR().read()); //Cast required as mbr holds type data, IR type Instruction; May need to handle exception
		System.out.println("Instruction: " + getMBR().read());
		this.fireUpdate("Load contents of MBR into IR \n");
		
		if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
			fireUpdate("**Branch taken in execute stage; pipeline flush. Current instruction \nfetch/decode abandoned.");
			//pipelineFlush = true;
			return;
		}
		
//		setWaitStatus(true);
//		try {
//			wait();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//			setWaitStatus(false);
//			return; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
//		}
//		setWaitStatus(false);
		
		getMAR().write(-1);//Reset MAR. Repositioned here for user clarity; mem. addr. remains in MAR until instr. in IR.		
		getMBR().write(null); //Clear MBR to reflect that instruction has moved to IR (should it be reset earlier, to better reflect
		//movement?)
		
	}
	//Fetch ends with instruction being loaded into IR.
	
	/*
	 * Similarly to the fetch method above, the interrupted status of the thread must be continuously
	 * polled to check for an interrupt caused by a pipeline flush originating in execute stage.
	 */
	public int instructionDecode() { //Returns int value of opcode
		Instruction instr = getIR().read();
		int opcodeValue = instr.getOpcode().getValue(); //Gets instruction opcode as int value
		
		if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
			fireUpdate("**Branch taken in execute stage; pipeline flush. Current instruction \nfetch/decode abandoned.");
			//pipelineFlush = true;
			return -1; //Do not continue execution if interrupted (pipeline flush)
		}
		
		getPC().incrementPC(); //Increment PC; done here so that with pipelining, the next instruction can be fetched at this point
		this.fireUpdate("PC incremented by 1 (ready for next instruction fetch) \n");
		
		if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
			fireUpdate("**Branch taken in execute stage; pipeline flush. Current instruction \nfetch/decode abandoned.");
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
		active = true;
		while (active) { //Continue fetching instructions		
			this.instructionFetch();
			if (!active || pipelineFlush) { //This will happen if an interrupt takes places within instructionFetch()
				return;
			}
			this.setOpcodeValue(this.instructionDecode());
			if (this.getOpcodeValue() == -1) { //Signals interrupted wait(); execution should be cancelled
				active = false;
				return; //Additional boolean may need to be set so controlUnit knows what's going on
			}
			boolean forwardSuccessful = this.forward();
			if (!forwardSuccessful) { //Interrupt meaning reset clicked; cancel execution
				active = false;
				return;
			}
		}
	}


	public boolean forward() {
		Integer opcode = this.getOpcodeValue(); //Get opcode value from superclass to pass to queue
		try {
			((IRfile) getIR()).shuntContents(0, 1); //Move contents of IR regsiter 0 to IR register 1 for use by execute stage
			fetchToExecuteQueue.put(opcode); //Waits here until put is successful (executeStage thread must be attempting take()).
			return true;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			active = false;
			return false;
		}
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	/*
	 * There must be a way to differentiate between interrupts generated by pipelining flushing
	 * and those generated by the user clicking reset; in the former case, execution must continue,
	 * with the fetch/decode stage simply resetting itself, in the latter case execution should terminate
	 * altogether.
	 * 
	 */
	public void setPipelineFlush(boolean isFlush) { //For pipeline flushing
		this.pipelineFlush = isFlush;
	}
	
	public boolean flushed() {
		return this.pipelineFlush;
	}
	
	//GUI events should not be handled from this thread but from EDT or SwingWorker
	@Override
	public void fireUpdate(final String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, update);
				getUpdateListener().handleUpDateEvent(updateEvent);	
			}
		});
	}
}
