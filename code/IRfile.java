package code;

public class IRfile implements InstructionRegister {
	Instruction[] irRegisters;
	int registerPointer; //Points to next available register location
	
	private UpdateListener updateListener;
	
	public IRfile() {
		irRegisters = new Instruction[3];
		registerPointer = 0;
	}

	@Override
	public void loadIR(Instruction instr) {
//		irRegisters[registerPointer] = instr;
//		if (registerPointer == 2) { //Once pointer points to last index of irRegisters, reset to 0 to avoid overflow
//			registerPointer = 0;
//		}
//		else { //If not at the last index, increment by one to next available
//			registerPointer++;
//		}
		irRegisters[0] = instr; //loadIR() only ever called by FetchDecodeStage. When fetch/decode is finished, it is responsible
							//for moving the instruction to the next register for the execute stage to work with.

	}
	
	public void shuntContents(int sourceIndex, int destinationIndex) { //For moving contents of one register to another between stages
		irRegisters[destinationIndex] = irRegisters[sourceIndex];
		clear(sourceIndex); //Clear source
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
		for (int i = 0; i < irRegisters.length; i++) {
			irRegisters[i] = null;
		}
	}
	
	@Override
	public void clear(int index) {
		irRegisters[index] = null;
		
	}

	@Override
	public String display() {
		String displayString = "";
		
		return null;
	}

}
