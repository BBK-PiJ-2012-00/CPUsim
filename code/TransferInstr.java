package code;

public class TransferInstr implements Instruction {
	private final Opcode opcode;
	private int source;
	private int destination;
	
	public TransferInstr(Opcode opcode, int source, int destination) throws IllegalStateException {
		if (opcode.getValue() > 3) { //Only data transfer instruction opcodes can be an opcode field in TransferInstr class
			throw new IllegalStateException("Illegal opcode");
		}
		//if (source || destination > 2^14)... make sure no overflow!
		this.opcode = opcode;
		this.source = source;
		this.destination = destination;
	}
	
	public Opcode getOpcode() {
		return this.opcode;
	}
	
	public String toMachineString() {
		String machineString = opcode.getValue() + " " + Integer.toHexString(source) + " " + Integer.toHexString(destination);
		return machineString;		
	}
	
	@Override
	public String toString() {
		return opcode.toString() + " " + Integer.toHexString(source) + " " + Integer.toHexString(destination);
	}

}
