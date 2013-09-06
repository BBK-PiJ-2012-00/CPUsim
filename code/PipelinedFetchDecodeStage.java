package code;

import java.util.concurrent.BlockingQueue;

public class PipelinedFetchDecodeStage extends FetchDecodeStage {
	private BlockingQueue<Integer> fetchToExecuteQueue;
	private boolean active;
	
	private ExecuteStage executeStage; //This class manages execution of execute stage
	private Thread executeThread;

	public PipelinedFetchDecodeStage(BusController systemBus,
			MemoryAddressRegister mar, MemoryBufferRegister mbr,
			InstructionRegister ir, ProgramCounter pc, BlockingQueue<Integer> fetchToExecuteQueue, ExecuteStage exStage) {
		
		super(systemBus, mar, mbr, ir, pc);
		this.fetchToExecuteQueue = fetchToExecuteQueue;
		this.executeStage = exStage;
		this.executeThread = new Thread(executeStage);

	}
	
	public void instructionFetch() {
		this.fireUpdate("\n** INSTRUCTION FETCH/DECODE STAGE ** \n");
		getIR().clear(); //Clear previous instruction from display
		getMAR().write(getPC().getValue()); //Write address value in PC to MAR.
		
		fireUpdate("Memory address from PC placed into MAR \n");
		
		setWaitStatus(true);
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
			setWaitStatus(false);
			active = false;
			return; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
		}
		setWaitStatus(false);
		
		
		//Transfer address from MAR to system bus, prompting read
		boolean successfulTransfer = getSystemBus().transferToMemory(getMAR().read(), null); 
		if (!successfulTransfer) { 
			//If SwingWorker is cancelled and thread of execution is interrupted, successfulTransfer will be false and the
			//method should not execute any further
			active = false;
			return;
		}
		
		System.out.println("Reentered f/d stage from bus: successful transfer = " + successfulTransfer);
		
		this.fireUpdate("Load contents of memory address " + getMAR().read() + " into MBR \n");
		
		
		setWaitStatus(true);
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
			setWaitStatus(false);
			return; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
		}
		setWaitStatus(false);	
		
		
		//A Data item should now be in MBR
		getIR().loadIR((Instruction) getMBR().read()); //Cast required as mbr holds type data, IR type Instruction; May need to handle exception
		this.fireUpdate("Load contents of MBR into IR \n");
		
		setWaitStatus(true);
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
			setWaitStatus(false);
			return; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
		}
		setWaitStatus(false);
		
		getMAR().write(-1);//Reset MAR. Repositioned here for user clarity; mem. addr. remains in MAR until instr. in IR.		
		getMBR().write(null); //Clear MBR to reflect that instruction has moved to IR (should it be reset earlier, to better reflect
		//movement?)
		
	}
	//Fetch ends with instruction being loaded into IR.
	
	
	public int instructionDecode() { //Returns int value of opcode
		Instruction instr = getIR().read();
		int opcodeValue = instr.getOpcode().getValue(); //Gets instruction opcode as int value
		getPC().incrementPC(); //Increment PC; done here so that with pipelining, the next instruction can be fetched at this point
		this.fireUpdate("PC incremented by 1 (ready for next instruction fetch) \n");
		
		setWaitStatus(true);
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
			setWaitStatus(false);
			return -1; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called). -1 signals to control
			//unit to stop execution.
		}
		setWaitStatus(false);		
		
		return opcodeValue;
		
	}
	
	@Override
	public synchronized void run() { //Synchronized to enable step execution
		if (!executeThread.isAlive()) {
			executeThread.start(); //Only start thread if it's not already alive
		}
		active = true;
		while (active) { //Continue fetching instructions		
			this.instructionFetch();
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
	
	public void flush() { //For pipeline flushing
		
	}

}
