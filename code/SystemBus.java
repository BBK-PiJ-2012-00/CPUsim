package code;

/*
 * There can only be one System Bus module, but the System Bus can have more than one of each line;
 * i.e. two ControlLines and corresponding Data/AddressLines for performance. These can be added as
 * fields as and when necessary.  Address and data lines are accessed via their corresponding control line,
 * so that in the event of multiple lines, access is simplified and integrity is protected.
 * 
 * Should the SystemBus class reference only the control lines, which then handle activation of data/address lines?
 * Closer to reality.
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
	
	

}
