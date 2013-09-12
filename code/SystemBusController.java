package code;

public class SystemBusController implements BusController {	
	private ControlLine controlLine; //Reference to control line
	
	
	public SystemBusController(ControlLine controlLine) {
		this.controlLine = controlLine;
	}	

	
	@Override
	public ControlLine accessControlLine() {
		return this.controlLine;
	}
	

	@Override
	public boolean transferToMemory(int memoryAddress, Data data) {
		boolean returned = controlLine.writeToBus(memoryAddress, data);
		//return controlLine.writeToBus(memoryAddress, data);
		return returned;
	}

	
	@Override
	public boolean transferToCPU(Data data) { //Called by memory
		return controlLine.writeToBus(-1, data); //-1 to reflect transfer to CPU (non-existent memory address)
	}


	@Override
	public void setCaller(Stage callingStage) {
		controlLine.setCaller(callingStage);
		
	}
	
		

}
