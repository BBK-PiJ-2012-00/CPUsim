package code;

public class IR implements InstructionRegister {
	private Instruction contents; //Instruction in the instruction register
	private UpdateListener updateListener;
	
	public void loadIR(Instruction instr) {
		this.contents = instr;
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, this.display());
		updateListener.handleUpDateEvent(updateEvent);
	}
	
	public Instruction read() {
		return this.contents;
	}
	//code for decoding instruction and executing it: for arithmetic, this involves passing it to ALU

	@Override
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;		
	}
	
	@Override
	public void clear() {
		this.contents = null;
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, this.display());
		updateListener.handleUpDateEvent(updateEvent);
	}
	
	@Override
	public String display() {
		String displayString = "";
		if (this.contents == null) {
			return displayString; //Will update display with blank string if contents is empty
		}
		displayString += this.contents;
		return displayString;
	}

	@Override
	public Instruction read(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clear(int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadIR(int index, Instruction instr) {
		this.contents = instr;
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, this.display());
		updateListener.handleUpDateEvent(updateEvent);		
	}

}
