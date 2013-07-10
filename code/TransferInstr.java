package code;

/*
 * TO DO:
 * 		Create checked exception for the construction; overflow / false opcode scenario.
 * 		Accessor methods for the source/destination fields.
 * 		Source/dest fields: revise max/min values once memory array size is confirmed. Max size should not exceed
 * 			max memory address possible.
 */

public class TransferInstr extends Instruction {
	private int source;
	private int destination;
	
	public TransferInstr(Opcode opcode, int source, int destination) {	
		super(opcode);
		if (opcode.getValue() > 3) { //Only data transfer instruction opcodes can be an opcode field in TransferInstr class
			throw new IllegalStateException("Illegal opcode!"); //Should perhaps create checked exception to handle this
		}
		if (source > 16384 || destination > 16384) { //Source/destination fields are 14 bits long, meaning max value of 16384 decimal.
			throw new IllegalStateException("Overflow in source/destination operand!");
		}
		//this.OPCODE = opcode;
		this.source = source;
		this.destination = destination;		
	}
	
	@Override
	public int getField1() {
		return source;
	}
	
	@Override
	public int getField2() {
		return destination;
	}

	
	

}
