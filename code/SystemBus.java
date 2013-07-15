package code;

public class SystemBus implements Bus {
	private static SystemBus sysBusInstance = null; //System Bus is a singleton.
	
	private ControlLine controlLine;
	private DataLine dataLine;
	private AddressLine addressLine;
	
	private SystemBus() {
		controlLine = new ControlLineImpl();
		
	}
	
	/*
	 * There can only be one System Bus module, but the System Bus can have more than one of each line;
	 * i.e. two ControlLines and corresponding Data/AddressLines for performance. These can be added as
	 * fields as and when necessary.
	 */

}
