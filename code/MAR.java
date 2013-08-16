package code;

/*
 * Class to represent Memory Address Register. This is a singleton to ensure only one instance.  See MBR
 * class for motivation.
 */
public class MAR implements MemoryAddressRegister {
	private static MemoryAddressRegister mar;
	private RegisterListener updateListener;
	
	private int registerContents;
	
	private MAR() {
		super();
		//Constructor only made explicit so that it can be made private, enforcing singleton.
	}
	
	public synchronized static MemoryAddressRegister getInstance() {
		if (mar == null) {
			mar = new MAR();
			return mar;
		}
		return mar;
	}
	
	public void registerListener(RegisterListener listener) {
		this.updateListener = listener;
	}
	
	/*
	 * 	 * (non-Javadoc)
	 * @see code.MemoryAddressRegister#write(int)
	 * Writing to MAR should prompt one of two things: transfer of the contents to PC,
	 * or activation of system bus -> control unit can have micro-methods which handle
	 * data flow. These can then be incorporated major methods representing stages.
	 * Control unit is responsible for coordination.
	 */
	@Override
	public void write(int address) {
		registerContents = address;
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, display());
		updateListener.handleUpDateEvent(updateEvent);
	}
	
	@Override
	public int read() {
		return registerContents;
	}
	
	public String display() {
		String marDisplay = "";
		if (registerContents == -1) {
			marDisplay += "";
		}
		else {
			marDisplay += this.registerContents;
		}
		return marDisplay;
	}

}
