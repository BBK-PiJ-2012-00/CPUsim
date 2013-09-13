package code;

/*
 * A class to represent transfer instructions LOAD, STORE and MOVE. All transfer instructions consist of one
 * source and one destination field.
 */

public class TransferInstr extends Instruction {
	private int source;
	private int destination;
	
	public TransferInstr(Opcode opcode, int source, int destination) {	
		super(opcode);
		if (this.OPCODE.getValue() > 3) { //Only data transfer instruction opcodes can be an opcode field in TransferInstr class
			throw new IllegalStateException("Illegal opcode!"); //Should perhaps create checked exception to handle this
		}
		
		if (this.OPCODE.getValue() == 1) { //LOAD instruction
			if (source < 0 || source > 99) { //Restrict source to valid memory addresses
				throw new IllegalStateException("Invalid memory reference! (LOAD)");
			}
			if (destination < 0 || destination > 16) {//Allow general purpose and rCC registers
				throw new IllegalStateException("Invalid register reference! (LOAD)");
			}
		}
		
		if (this.OPCODE.getValue() == 2) { //STORE instruction
			if (source < 0 || source > 15) { //Restrict source to general purpose registers
				throw new IllegalStateException("Invalid register reference! (STORE)");
			}
			if (destination < 0 || destination > 99) {//Restrict to valid memory addresses
				throw new IllegalStateException("Invalid memory reference! (STORE)");
			}
		}
		

		if (this.OPCODE.getValue() == 3) { //MOVE instruction (register-register source destination)
			if (source < 0 || source > 16) { //Restrict source to general purpose and rCC registers
				throw new IllegalStateException("Invalid register reference! (STORE)");
			}
			if (destination < 0 || destination > 16) {//Restrict to general purpose and rCC registers
				throw new IllegalStateException("Invalid memory reference! (STORE)");
			}
		}
		
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
