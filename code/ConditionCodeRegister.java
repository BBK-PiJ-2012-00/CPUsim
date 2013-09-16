package code;

import javax.swing.SwingUtilities;

/*
 * A class to represent a condition code register, generally for use with conditional branch instructions. User-visible,
 * can be referenced as r16 in assembly code. Operands can be moved to, moved from, and loaded into the condition code
 * register (often abbreviated to rCC).
 */
public class ConditionCodeRegister implements Register {
	private Operand contents;
	private UpdateListener updateListener;

	
	@Override
	public Operand read() {
		return this.contents;
	}

	
	@Override
	public void write(Operand operand) {
		this.contents = operand;
		fireUpdate(display());
	}

	
	@Override
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;
		
	}


	private String display() {
		String displayString = "";
		if (this.contents == null) {
			return displayString; //Will update display with blank string if contents is empty
		}
		displayString += this.contents;
		return displayString;
	}
	
	
	//GUI events should be handled from EDT only.
	//This adds the update event to the EDT thread.
	private void fireUpdate(final String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(ConditionCodeRegister.this, update);
				ConditionCodeRegister.this.updateListener.handleUpDateEvent(updateEvent);	
			}
		});
	}

}
