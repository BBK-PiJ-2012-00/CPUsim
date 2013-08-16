package code;

/*
 * This class is now a singleton (there can only be once instance, with a global access point). Because this class
 * is referenced by the ControlLine class, as well as the ControlUnit class, it is important that only one instance
 * of this class can exist (it has already occurred during testing that two separate instances co-existed, causing 
 * data inegrity issues). As the ControlLine is accessible only via the BusController, it would mean that the MBR
 * would have to be instantiated in the ControlUnit first, and then passed as a constructor parameter to the BusController,
 * and then to the ControlUnit class.  As the BusController has no need for an MBR reference, this causes added complication.
 * Thus, a singleton would allow both the ControlUnit and the ControlLine access, without worrying about duplicate objects
 * and passing objects through classes which need not have any knowledge of the MBR.  The same goes for the MAR class.
 */

public class MBR implements MemoryBufferRegister {
	private static MemoryBufferRegister MBRinstance;
	private Data registerContents;
	private RegisterListener updateListener;
	
	private MBR() {
		//Constructor only explicitly referenced in order to make it private, preventing instantiation
		//from outside the class.
		super(); //Calls default Object constructor.
	}
	
	public synchronized static MemoryBufferRegister getInstance() {
		if (MBRinstance == null) {
			MBRinstance = new MBR();
			return MBRinstance;
		}
		return MBRinstance;
	}
	
	@Override
	public boolean write(Data data) { //Successful write returns true
		//Size is restricted in Instruction/Operand classes
		registerContents = data;
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, display());
		updateListener.handleUpDateEvent(updateEvent);
		if (registerContents == null) {
			return false;
		}
		return true;
	}
	
	@Override
	public Data read() { 
		return registerContents;
	}
	
	public String display() {
		String mbrDisplay = "";
		if (registerContents == null) {
			mbrDisplay += "";
		}
		else {
			mbrDisplay += "  " + this.registerContents.toString();
		}
		return mbrDisplay;
	}

	@Override
	public void registerListener(RegisterListener listener) {
		this.updateListener = listener;
		
	}



}
