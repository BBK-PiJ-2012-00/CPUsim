package code;

/*
 * There can only be one System Bus module, but the System Bus can have more than one of each line;
 * i.e. two ControlLines and corresponding Data/AddressLines for performance. These can be added as
 * fields as and when necessary.  Address and data lines are accessed via their corresponding control line,
 * so that in the event of multiple lines, access is simplified and integrity is protected.
 * 
 * Should the SystemBus class reference only the control lines, which then handle activation of data/address lines?
 * Closer to reality.
 * 
 * TO DO: restrict size of data that can be transferred: no more than 2^32
 * 			
 * 		Sort out concurrency issues; ensure bus operations are atomic. Control line method is sychronized, but
 * 			check that's sufficient.
 */
public class SystemBus implements Bus {
	private static SystemBus systemBus = null; //System Bus is a singleton. Should perhaps be referenced as type Bus.
	
	private ControlLine controlLine;
	
	//References to CPU (MAR, MBR) and main memory
	
	private SystemBus() {
		controlLine = new ControlLineImpl();
	}
	
	/*
	 * Singleton global access point.
	 */
	public synchronized static SystemBus getInstance() { //Synchronized to ensure no concurrency issues with pipelining
		if (systemBus == null) {
			systemBus = new SystemBus();
			return systemBus;
		}
		return systemBus;
	}

	@Override
	public boolean transferToMemory(int memoryAddress, Data data) {
		return controlLine.writeToBus(memoryAddress, data);
	}

	@Override
	public boolean transferToCPU(Data data) {
		return controlLine.writeToBus(-1, data); //-1 to reflect transfer to CPU (non-existent memory address)
	}
	
	

}
