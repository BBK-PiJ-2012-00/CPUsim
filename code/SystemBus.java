package code;

/*
 * There can only be one System Bus module, but the System Bus can have more than one of each line;
 * i.e. two ControlLines and corresponding Data/AddressLines for performance. These can be added as
 * fields as and when necessary.
 */
public class SystemBus implements Bus {
	private static SystemBus systemBus = null; //System Bus is a singleton. Should perhaps be referenced as type Bus.
	
	private ControlLine controlLine;
	private DataLine dataLine;
	private AddressLine addressLine;
	
	private SystemBus() {
		controlLine = new ControlLineImpl();
		dataLine = new DataLineImpl();
		addressLine = new AddressLineImpl();
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
