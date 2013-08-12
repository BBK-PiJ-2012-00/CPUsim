package code;

public class IR implements InstructionRegister {
	private Instruction contents; //Instruction in the instruction register
	private RegisterListener registerListener;
	
	public void loadIR(Instruction instr) {
		this.contents = instr;
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, "" + contents);
		registerListener.handleUpDateEvent(updateEvent);
	}
	
	public Instruction read() {
		return this.contents;
	}
	//code for decoding instruction and executing it: for arithmetic, this involves passing it to ALU

	@Override
	public void registerListener(RegisterListener listener) {
		this.registerListener = listener;		
	}

}
