package code;

public class ControlUnitImpl implements ControlUnit {
private BusController systemBus = SystemBusController.getInstance();
	
	private MBR mbr = new MBR(); //Reference these with their corresponding interfaces
	private MAR mar = new MAR();
	
	private RegisterFile genRegisters = new RegisterFile16();
	
	//Stages represented by methods
	
	public void instructionFetch() {
		//Code for instruction fetch stage
	}
	
	public void instructionDecode() {
		
	}
	
	public void instructionExecute() {
		//Can call other, private methods depending on instruction opcode
		//case switch statement: if arithmetic, call arithmeticInstrExecute(), etc
	}
	
	public void instructionStore() { //Not required in every cycle
		
	}
	


}
