package code;

import javax.swing.SwingUtilities;

/*
 * All read and write operations should be atomic and commandeer the bus for the duration of the operation; this is 
 * simpler for GUI and clarity.
 */

public class MemoryModule implements MainMemory {

	private final Data[] MEMORY; //Array representing main memory itself/
	private int pointer; //Points to next available location for storage	
	private BusController systemBusController; //Reference to system bus
	
	private UpdateListener updateListener;
	

	
	public MemoryModule() {
		MEMORY = new Data[100];
		pointer = 0;
	}
	
	@Override
	public void registerBusController(BusController systemBusController) {
		this.systemBusController = systemBusController;
	}
	
	
	@Override
	public int getPointer() {
		return this.pointer;
	}
	
	
	@Override
	public void resetPointer() {
		this.pointer = 0;
	}
	
	
	@Override
	public void clearMemory() { //To reset memory contents when loading a new program
		for (int i = 0; i < 100; i++) { //Resets each non-null address to null, effectively clearing memory contents
			if (MEMORY[i] != null) {
				MEMORY[i] = null;
			}
			else {
				break;
			}
		}
	}
	
	
	
	@Override
	public Data accessAddress(int index) { //Primarily for testing purposes
		return MEMORY[index];
	}
	

	
	
	@Override
	public void loadMemory(Data[] programCode) {
		for (Data line : programCode) {
			MEMORY[pointer] = line; //Load pointer location with line of program code
			pointer++;
		}
		fireUpdate(display());
	}
	
	
	
	
	@Override
	public boolean notifyWrite(int address, Data data) { //Method to prompt memory to receive data from system bus (write)
		//No checking of address being empty or not; up to assembly programmer
		if (address < 100 && address >= 0) {
			MEMORY[address] = data;
			fireUpdate(display());
			return true;
		}
		return false;
	}
	
	
	@Override
	public boolean notifyRead(int address) { //Absence of data indicates requested memory read as opposed to write (as in reality)
		Data dataRead;
		if (address < 100 && address >=0) {
			if (MEMORY[address] == null) {
				dataRead = new OperandImpl(0); //If the contents of address is null, read as 0 (as in reality)				
			}
			else {
				dataRead = MEMORY[address];
			}
			
			return systemBusController.transferToCPU(dataRead);//Transfers read data to system bus, subsequently to CPU
		}
		return false;
	}
	
	
	@Override
	public String display() { //Display memory on command line interface
		String displayString = "";
		
		for (int i = 0; i < MEMORY.length; i++) {
			if (MEMORY[i] == null) {
				if (i < 10) { //For formatting of single digits
					String iString = "  0" + i;
					displayString += "\n" + iString + "| ------------";
				}
				else {
					displayString += "\n  " + i + "| ------------";
				}
			}
			else { 
				if (i < 10) {
					String iString = "  0" + i;
					displayString += "\n" + iString + "| " + MEMORY[i].toString();
				}
				else {
					displayString+= "\n  " + i + "| " + MEMORY[i].toString();
				}
			}
		}
		return displayString;
	}
	
	
	@Override
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;
	}
	
	
	//GUI events should be handled from EDT only.
	//This adds the update event to the EDT thread. 
	private void fireUpdate(final String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(MemoryModule.this, update);
				MemoryModule.this.updateListener.handleUpDateEvent(updateEvent);	
			}
		});
	}
	

}
