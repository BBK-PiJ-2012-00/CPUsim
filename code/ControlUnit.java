package code;

/*
 * The primary class of the simulator, representing the CPU's control unit.
 */

public class ControlUnit {
	
	private SystemBus systemBus = SystemBus.getInstance();
	
	private MBR mbr = new MBR(); //Reference these with their corresponding interfaces
	private MAR mar = new MAR();
	
	private RegisterFile genRegisters = new RegisterFile16();

}
