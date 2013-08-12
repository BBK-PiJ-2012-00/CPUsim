package code;

/*
 * All read and write operations should be atomic and commandeer the bus for the duration of the operation; this is 
 * simpler for GUI and clarity.
 */

public class MemoryModule implements MainMemory {
	private static MainMemory memoryModule = null; //Keeps track of singleton
	
	//Array full of null values to start with - should it be initialised to hold 0s (to avoid null pointer exception)?
	private final Data[] MEMORY; //Array representing main memory itself/
	private int pointer; //Points to next available location for storage	
	private BusController systemBusController; //Reference to system bus
	
	private RegisterListener updateListener; //Will perhaps have different listener
	
	
	
	private MemoryModule() { //Memory is a singleton to prevent duplicates and inconsistency
		//systemBus = SystemBus.getInstance(); //This causes stack overflow error; SystemBus creates new controlLine,
		//which in turn creates new memory module, which creates a new systemBus... until call stack overflows.
		MEMORY = new Data[100];
		pointer = 0;
	}
	
	public synchronized static MainMemory getInstance() {
		if (memoryModule == null) {
			memoryModule = new MemoryModule();
		}
		return memoryModule;
	}
	
	
	public int getPointer() {
		return this.pointer;
	}
	
	public void resetPointer() {
		this.pointer = 0;
	}
	
	public void clearMemory() { //To reset memory contents when loading a new program
		for (int i = 0; i < 100; i++) { //Resets each non-null address to null, effectively clearing memor contents
			if (MEMORY[i] != null) {
				MEMORY[i] = null;
			}
			else {
				break;
			}
		}
	}
	
	public Data accessAddress(int index) { //Primarily for testing purposes, not for use by program.
		return MEMORY[index];
	}
	
	//Should take Data type as parameter, not Instruction (what about Operands are not type Instruction!)
	//May even be obsolete, as notify() below could be used
	//May take an array as a parameter, depdening on Loader implementation
	public void writeInstruction(Instruction instr, int index) { //For use by Loader to write instructions into memory
		MEMORY[index] = instr; 
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, display());
		updateListener.handleUpDateEvent(updateEvent);
		//As this is only used by loader, it should take the address of the first instruction loaded into memory
		//and transfer this address via the address bus to the MAR, and on to the PC to begin execution.
		//A method can be called to do this; transferFirstInstruction().
		//When a user selects a program to load, this should prompt the sim to the point where the PC is loaded
		//but is waiting to begin execution.
		//So it simply causes MAR to set the PC, and then the sim waits for user to press 'start'.
	}
	
	
	/*
	 * Absolute addressing may be clearer pedagogically; will there ever be a need to start a program from a 
	 * memory address other than 0? Even if data variables are declared first in the assembly language, it can
	 * be assembled in such away that instructions are loaded first, with data following, thus execution will
	 * always start from address 0 (array can be made of variables, then of instructions, and then the two can
	 * be put together into one large array to be passed to this method). 
	 */
	public void loadMemory(Data[] programCode) {
	//	int pointerValue = pointer; //Pointer value before loading program code
	//	if (pointer != 0) {
	//		pointerValue = pointer; //Save value of pointer so that PC can be set to start address of program, if not 0
	//	}
		for (Data line : programCode) {
			MEMORY[pointer] = line; //Load pointer location with line of program code
			//System.out.println(MEMORY[pointer].toString());
			pointer++; //Increment pointer
		}
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, display());
		updateListener.handleUpDateEvent(updateEvent);
	//	if (pointerValue != 0) { //Set PC to start address, if not 0 (PC set to 0 by default)
			
	//	}
	}
	
	public void setPC() { //Method to send start address of program code via system bus to set PC before execution begins
		//Can use systembus.transferToCPU(), 
	}
	
	
	public boolean notify(int address, Data data) { //Method to prompt memory to receive data from system bus (write)
		//No checking of address being empty or not; up to programmer
		if (address < 100 && address >= 0) {
			MEMORY[address] = data;
			ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, display());
			updateListener.handleUpDateEvent(updateEvent);
			return true;
		}
		//Throw an exception or simply return false?
		return false;
	}
	
	public boolean notify(int address) { //Absence of data indicates requested memory read as opposed to write (as in reality)
		Data dataRead;
		if (address < 100 && address >=0) {
			if (MEMORY[address] == null) {
				dataRead = new OperandImpl(0); //If the contents of address is null, read as 0 (as in reality)				
			}
			else {
				dataRead = MEMORY[address];
			}
			systemBusController = SystemBusController.getInstance(); //Putting this here breaks awkward creation chain, and is of no consequence 
			//as there is only ever one system bus instance with global access point.
			
			//As this is all performed by the same thread, there should be no issue
			systemBusController.transferToCPU(dataRead);//Transfers read data to system bus, subsequently to CPU
			return true;
		}
		//Throw exception if address invalid? This would disrupt program flow (possibly)
		return false;
	}
	
	
	//Fix so that numbers 1-10 display with extra space to align | character
	public String display() { //Display memory on command line interface
		String displayString = "";
		//System.out.println("  -- MAIN MEMORY --  ");
		for (int i = 0; i < MEMORY.length; i++) {
			if (MEMORY[i] == null) {
				if (i < 10) { //For formatting of single digits
					String iString = "  0" + i;
					displayString += "\n" + iString + "| ------------";
				}
				else {
					//System.out.println(i + "| " + "--------");
					displayString += "\n  " + i + "| ------------";
				}
			}
			else { 
				if (i < 10) {
					String iString = "  0" + i;
					displayString += "\n" + iString + "| " + MEMORY[i].toString();
				}
				else {
					//System.out.println(i + "| " + MEMORY[i].toString());
					displayString+= "\n  " + i + "| " + MEMORY[i].toString();
				}
			}
		}
		//displayString += "</html>";
		return displayString;
	}
	
	public void registerListener(RegisterListener listener) {
		this.updateListener = listener;
	}
	

}
