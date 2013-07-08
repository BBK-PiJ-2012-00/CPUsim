package code;

public class TransferInstr implements Instruction {
	private final Opcode OPCODE; //Cannot be changed once set
	private int source;
	private int destination;
	
	public TransferInstr(Opcode opcode, int source, int destination) {
		if (opcode.getValue() > 3) { //Only data transfer instruction opcodes can be an opcode field in TransferInstr class
			throw new IllegalStateException("Illegal opcode"); //Should perhaps create checked exception to handle this
		}
		//if (source || destination > 2^14)... make sure no overflow!
		this.OPCODE = opcode;
		this.source = source;
		this.destination = destination;
	}
	
	@Override
	public Opcode getOpcode() {
		return this.OPCODE;
	}
	
	@Override
	public String toMachineString() { //Think about format, leading 0s
		String machineString = OPCODE.getValue() + " " + Integer.toHexString(source) + " " + Integer.toHexString(destination);
		return machineString;		
	}
	
	@Override
	public String toString() {
		return OPCODE.toString() + " " + Integer.toHexString(source) + " " + Integer.toHexString(destination);
	}

	@Override
	public boolean isInstruction() {		
		return true;
	}

	@Override
	public boolean isInteger() {
		return false;
	}

}
