package code;

public class ControlLineImpl implements ControlLine {
	private AddressBus addressBus;
	private DataBus dataBus;	
	private MainMemory memory;
	
	private MemoryBufferRegister mbr;//Reference to CPU's MBR
	//private MemoryAddressRegister mar; //Not required (for the time being)
	
	private boolean isWaiting; //So that the SwingWorker thread on the GUI can ascertain if the thread is waiting in this object
	
	private String activityMonitorUpdate; //For activity monitor commentary
	private String operationUpdate; //To display on GUI control line whether the operation is a read or a write
	
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
			//no need to place address on address bus -> if address field in AddressLine is null/0, this signifies delivery
			//to CPU as opposed to memory (and is true to reality).
			dataBus.put(data); //This is functionally redundant, but will be useful for GUI animation of bus lines
			
			isWaiting = true;
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			isWaiting = false;
			
			return this.deliverToMBR(); //Complete read operation. 
		}
		else if (data == null) { //Signifies first phase of a read; MAR places address on address line, prompting memory to
			//place contents of the address on the address line onto data line for return to MBR.
			addressBus.put(address);
			
			isWaiting = true;
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			isWaiting = false;
			
			return this.deliverToMemory(true);
		}
		//Memory write code:
		addressBus.put(address);
		dataBus.put(data);
		
		isWaiting = true;
		try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		return memory.notifyWrite(addressBus.read(), dataBus.read());
	}
	
	
	public String display() {
		
		return null;
	}
	
	@Override
	public AddressBus getAddressBus() {
		return this.addressBus;
	}
	
	@Override
	public DataBus getDataBus() {
		return this.dataBus;
	}
	
	public boolean isWaiting() {
		return isWaiting;
	}
	
	public void fireActivityUpdate(String update) {
		//For activity monitor commentary
	}
	
	public void fireOperationUpdate(String update) {
		//For control line display on GUI (read/write)
	}
	
	
	
}
