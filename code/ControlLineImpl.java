package code;

import javax.swing.SwingUtilities;

public class ControlLineImpl implements ControlLine {
	private AddressBus addressBus;
	private DataBus dataBus;	
	private MainMemory memory;	
	private MemoryBufferRegister mbr;//Reference to CPU's MBR
	
	private boolean isWaiting; //So that the SwingWorker thread on the GUI can ascertain if the thread is waiting in this object
	
	private UpdateListener updateListener;
	
	private Stage callingStage; //A reference to the stage using the system bus; for pipelined mode updates, so that GUI
								//updates can be directed to the relevant activity monitor display.
	
	
	public ControlLineImpl(MemoryBufferRegister mbr) {
		this.mbr = mbr;
		addressBus = new AddressBusImpl();
		dataBus = new DataBusImpl();
	}
	
	public void registerMemoryModule(MainMemory memory) {
		this.memory = memory;
	}	
	
	
	//Synchronized to allow notify() operation on wait(); notifying thread must hold lock.
	public synchronized boolean writeToBus(int address, Data data) { 
		if (address == -1) { //Indicates transfer from memory to CPU (2nd phase of memory read; delivery from memory to MBR)
			dataBus.put(data); 
			
			if (data instanceof Instruction) { //Activity monitor commentary (pipelined mode f/d activity monitor)
				String update = "> Instruction " + data.toString() + " placed on data bus from \nmemory.\n";
				if (callingStage == null) { //callingStage will be null for standard execution (won't be set)
					fireActivityUpdate(update);
				}
				else {//Instructions are only transferred to CPU in F/D stage
					fireActivityUpdate(update, 0); //0 refers to F/D activity monitor in pipelined mode.
				}
			}
			else { //Operands are transferred to CPU from memory only in Ex. Stage LOAD instructions
				String update = "> Operand " + data.toString() + " placed on data bus from \nmemory.\n";
				if (callingStage == null) { //Standard execution, update single activity monitor as usual
					fireActivityUpdate(update);
				}
				else { //Pipelined mode, callingStage will not be null
					fireActivityUpdate(update, 1); //Update Ex. Stage activity monitor in pipelined mode.
				}
			}
			
			isWaiting = true;
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.println("I'm being interrupted.");
				e.printStackTrace();
				clear();
				isWaiting = false;
				return false;
			}
			isWaiting = false;
			
			fireOperationUpdate(""); //Clear control line display
			
			return this.deliverToMBR(); //Complete read operation. 
		}
		
		else if (data == null) { //Signifies first phase of a read; MAR places address on address line, prompting memory to
			//place contents of the address on the address line onto data line for return to MBR.
			addressBus.put(address);			
			fireOperationUpdate("Memory read");
			
			String update = "> Address " + address + " placed on address bus from MAR, prompting\nthe first stage" +
					"of a memory read.\n";
			if (callingStage == null) { //Standard mode, update single activity monitor
				fireActivityUpdate(update);
			}
			else if (callingStage instanceof PipelinedFetchDecodeStage) {
				fireActivityUpdate(update, 0); //Update F/D activity monitor
			}
			else if (callingStage instanceof PipelinedExecuteStage) {
				fireActivityUpdate(update, 1); //Update Ex. activity mointor
			}
			
			isWaiting = true;
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				clear();
				isWaiting = false;
				return false;
			}
			isWaiting = false;
			
			return this.deliverToMemory(true);
		}
		
		//Memory write code: address and data supplied
		addressBus.put(address);
		dataBus.put(data);
		
		fireOperationUpdate("Memory write");
//		if (data instanceof Instruction) { //Is this ever called? Instructions aren't written to memory (only fetched)
//			fireActivityUpdate("Address " + address + " placed on address bus from MAR \nand instruction " +
//					data.toString() + " placed on data bus by MBR.\n");
//		}
		//else {
//			fireActivityUpdate("Address " + address + " placed on address bus from MAR \nand operand " +
//					data.toString() + " placed on data bus by MBR.\n");
		//}
		String update = "> Address " + address + " placed on address bus from MAR \nand operand " +
				data.toString() + " placed on data bus by MBR.\n";
		if (callingStage == null) { //Standard mode
			fireActivityUpdate(update);			
		}
		else { //Memory writes only called by Ex. stage, so implicitly update Ex. stage activity monitor
			fireActivityUpdate(update, 1);
		}
		
		isWaiting = true;
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
			clear();
			isWaiting = false;
			return false;
		}
		isWaiting = false;
		
		return this.deliverToMemory(false);	//False -> not a read operation (write operation)	
	}
	
	
	
	public boolean deliverToMBR() { //Prompts dataLine to load its value into MBR, completing memory read operation
		return mbr.write(dataBus.read());		
	}
	
	
	
	public boolean deliverToMemory(boolean isRead) { //If isRead, first stage of read operation, otherwise write.
		if (isRead) {	
			return memory.notifyRead(addressBus.read());
		}
		fireOperationUpdate(""); //Reset control line as data is written into MBR
		return memory.notifyWrite(addressBus.read(), dataBus.read());
	}
	

	
	@Override
	public AddressBus getAddressBus() {
		return this.addressBus;
	}
	
	
	@Override
	public DataBus getDataBus() {
		return this.dataBus;
	}
	
	
	@Override
	public boolean isWaiting() {
		return isWaiting;
	}
	
	
	@Override
	public void fireOperationUpdate(final String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(ControlLineImpl.this, true, update); //True reflects update is to control line display
				updateListener.handleUpDateEvent(updateEvent);
			}
		});
		
	}
	
	//SWITCH TO PRIVATE VISIBILITY
	public void fireActivityUpdate(final String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(ControlLineImpl.this, false, update);
			    //False parameter indicates activity monitor update as opposed to control line update
				updateListener.handleUpDateEvent(updateEvent);	
			}
		});
	}
	
	public void fireActivityUpdate(final String update, final int activityMonitorRef) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(ControlLineImpl.this, false, activityMonitorRef, update);
			    //false signifies its not a control line display update but an activity monitor update
			    //activityMonitorRef. is an integer referring to which GUI activity monitor the update should be passed in
			    //pipelined mode.
				updateListener.handleUpDateEvent(updateEvent);	
			}
		});
	}
	
	@Override
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;
	}
	
	public void resetWaitStatus() {
		isWaiting = false;
	}
	
	/*
	 * To clear system bus lines in event of SwingWorker thread being cancelled (resets GUI display)
	 * or pipeline flush.
	 */
	@Override
	public void clear() {
		resetWaitStatus();
		addressBus.put(-1);
		dataBus.put(null);
		fireOperationUpdate(""); //Reset control line display
	}
	
	@Override
	public void setCaller(Stage caller) {
		this.callingStage = caller;		
	}
	
	
}
