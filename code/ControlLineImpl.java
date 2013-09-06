package code;

public class ControlLineImpl implements ControlLine {
	private AddressBus addressBus;
	private DataBus dataBus;	
	private MainMemory memory;	
	private MemoryBufferRegister mbr;//Reference to CPU's MBR
	
	private boolean isWaiting; //So that the SwingWorker thread on the GUI can ascertain if the thread is waiting in this object
	
	private UpdateListener updateListener;
	
	
	public ControlLineImpl(MemoryBufferRegister mbr) {
		this.mbr = mbr;
		addressBus = new AddressBusImpl();
		dataBus = new DataBusImpl();
	}
	
	public void registerMemoryModule(MainMemory memory) {
		this.memory = memory;
	}	
	
	
	
	public synchronized boolean writeToBus(int address, Data data) { 
		if (address == -1) { //Indicates transfer from memory to CPU (2nd phase of memory read; delivery from memory to MBR)
			dataBus.put(data); 
			
			if (data instanceof Instruction) { //Activity monitor commentary
				fireActivityUpdate("Instruction " + data.toString() + " placed on data bus from \nmemory.\n");
			}
			else {
				fireActivityUpdate("Operand " + data.toString() + " placed on data bus from \nmemory.\n");
			}
			
			isWaiting = true;
			try {
				System.out.println("I'm waaaaaaiting!");
				wait();
			} catch (InterruptedException e) {
				System.out.println("I'm being interrupted.");
				e.printStackTrace();
				clear();
				isWaiting = false;
				System.out.println("About to return false.");
				return false;
			}
			System.out.println("Just left catch.");
			isWaiting = false;
			
			fireOperationUpdate(""); //Clear control line display
			
			return this.deliverToMBR(); //Complete read operation. 
		}
		
		else if (data == null) { //Signifies first phase of a read; MAR places address on address line, prompting memory to
			//place contents of the address on the address line onto data line for return to MBR.
			addressBus.put(address);
			
			fireOperationUpdate("Memory read");
			fireActivityUpdate("Address " + address + " placed on address line by MAR, prompting\nthe first stage" +
					"of a memory read.\n");
			
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
		
		//Memory write code:
		addressBus.put(address);
		dataBus.put(data);
		
		fireOperationUpdate("Memory write");
		if (data instanceof Instruction) {
			fireActivityUpdate("Address " + address + " placed on address bus by MAR \nand instruction " +
					data.toString() + " placed on data bus by MBR.\n");
		}
		else {
			fireActivityUpdate("Address " + address + " placed on address bus by MAR \nand operand " +
					data.toString() + " placed on data bus by MBR.\n");
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
	public void fireActivityUpdate(String update) {
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, update);
		updateListener.handleUpDateEvent(updateEvent);
	}
	
	
	@Override
	public void fireOperationUpdate(String update) {
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, true, update); //True reflects update is to control line display
		updateListener.handleUpDateEvent(updateEvent);
	}
	
	
	@Override
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;
	}
	
	public void resetWaitStatus() {
		isWaiting = false;
	}
	
	/*
	 * To clear system bus lines in event of SwingWorker thread being cancelled (resets GUI display).
	 */
	public void clear() {
		addressBus.put(-1);
		dataBus.put(null);
		fireOperationUpdate(""); //Reset control line display
	}
	
	
}
