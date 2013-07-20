package code;

/*
 * Class to represent Memory Address Register. This is a singleton to ensure only one instance.  See MBR
 * class for motivation.
 */
public class MAR implements MemoryAddressRegister {
	private static MemoryAddressRegister mar;
	
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
	
	/*
	 * 	 * (non-Javadoc)
	 * @see code.MemoryAddressRegister#write(int)
	 * Writing to MAR should prompt one of two things: transfer of the contents to PC,
	 * or activation of system bus -> control unit can have micro-methods which handle
	 * data flow. These can then be incorporated major methods representing stages.
	 * Control unit is responsible for coordination.
	 * An extra block in ControlLine's writeToBus() method can deal with the initial
	 * setting of PC; an address only, with no data, signifies setting PC? Or, address
	 * can take on -2!
	 */
	@Override
	public void write(int address) {
		registerContents = address;
	}
	
	@Override
	public int read() {
		return registerContents;
	}

}
