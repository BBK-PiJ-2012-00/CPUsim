package code;

/*
 * For use with pipelined Stages; a file of instruction registers.
 */
public class IRfile implements InstructionRegister {
	private Instruction[] irRegisters;	
	private UpdateListener updateListener;	

	
	public IRfile() {
		irRegisters = new Instruction[3]; //Create register file of size 3
	}
	

	@Override
	public void loadIR(Instruction instr) {
		irRegisters[0] = instr; //loadIR() most often called by FetchDecodeStage.
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, 0, instr.toString()); //0 represents index
		updateListener.handleUpDateEvent(updateEvent);
	}
	
	
	@Override
	public void loadIR(int index, Instruction instr) { //For use by execute stage in pipelining mode (possibly also by w/b stage).
		irRegisters[index] = instr;
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, index, instr.toString());
		updateListener.handleUpDateEvent(updateEvent);
	}
		

	@Override
	public Instruction read() {
		return irRegisters[0];
	}
	
	@Override
	public Instruction read(int index) {
		return irRegisters[index];
	}

	@Override
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;

	}

	@Override
	public void clear() {
		System.out.println("IRfile clear() called.");
		for (int i = 0; i < irRegisters.length; i++) {
			clear(i);
		}
	}
	
	@Override
	public void clear(int index) {
		irRegisters[index] = null;
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, index, "");
		updateListener.handleUpDateEvent(updateEvent);			
		
	}

	@Override
	public String display() {		
		return null;
	}

}
