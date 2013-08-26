package code;

/*
 * A class to represent a status register, generally for use with conditional branch instructions. User-visible,
 * can be referenced as r16 in assembly code.
 * 
 * This register may automatically be set as the result of certain operations? Easier to set it manually with explicit
 * LOAD instruction.  Can be incremented/decremented using arithmetic operations.
 */
public class StatusRegister implements Register {
	private Operand contents;
	private UpdateListener updateListener;

	@Override
	public Operand read() {
		return this.contents;
	}

	@Override
	public void write(Operand operand) {
		this.contents = operand;
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, this.display());
		updateListener.handleUpDateEvent(updateEvent);
	}

	@Override
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;
		
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

}
