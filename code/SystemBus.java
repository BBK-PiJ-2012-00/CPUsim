package code;

/*
 * Design consideration: should address and data lines simply be represented as int/Data fields respectively 
 * in the SystemBus class? As opposed to having their own classes? 
 * 
 * SystemBus class used by modules to access bus, which prevents any ambiguity and synchronization issues over
 * the multiple bus lines. However, the control line itself references MBR/MAR, as well as main memory.
 * 
 * Should SystemBus become a simple intermediary class which passes data in a more simple manner between memory 
 * and CPU?
 * 
 * Keep as is and change name to SystemBusController? Control line does legwork and thus has direct access to
 * memory and CPU; this class is an interface to the lines for memory and CPU to avoid ambiguity. Can improve
 * functionality and structure by having addressline/dataline deliver their contents directly to memory/cpu?
 * There isn't any issue with lines accessing memory/cpu, only with memory/cpu accessing lines which would leave
 * room for error.
 * 
 * Address/data lines should hold all values -> control line is responsible for coordination. Use aLine.read() and
 * put() methods to achieve this.
 * 
 * Do refactor -> rename, and this is now a BusController class.
 */

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
 * 		Sort out concurrency issues; ensure bus operations are atomic. Control line method is synchronized, but
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
	public boolean transferToCPU(Data data) { //Called by memory
		return controlLine.writeToBus(-1, data); //-1 to reflect transfer to CPU (non-existent memory address) -> still necessary!?
	}
	
	public boolean deliverToMBR(Data data) { //Called by control line
		
		return false;
	}
	

}
