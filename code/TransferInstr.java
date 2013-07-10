package code;

/*
 * TO DO:
 * 		Create checked exception for the construction; overflow / false opcode scenario.
 */

public class TransferInstr implements Instruction {
	private final Opcode OPCODE; //Cannot be changed once set
	private int source;
	private int destination;
	
	public TransferInstr(Opcode opcode, int source, int destination) {		
		if (opcode.getValue() > 3) { //Only data transfer instruction opcodes can be an opcode field in TransferInstr class
			throw new IllegalStateException("Illegal opcode!"); //Should perhaps create checked exception to handle this
		}
		if (source > 16384 || destination > 16384) { //Source/destination fields are 14 bits long, meaning max value of 16384 decimal.
			throw new IllegalStateException("Overflow in source/destination operand!");
		}
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
	
	@Override 
	public boolean isFloatingPoint() {
		return false;
	}

}
