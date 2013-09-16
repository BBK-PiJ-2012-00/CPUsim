package code;

import javax.swing.SwingUtilities;

public class IR implements InstructionRegister {
	private Instruction contents; //Instruction in the instruction register
	private UpdateListener updateListener;
	
	
	@Override
	public void loadIR(Instruction instr) {
		this.contents = instr;
		fireUpdate(0, display());
	}
	
	
	@Override
	public void loadIR(int index, Instruction instr) {
		this.contents = instr;
		fireUpdate(0, display());
	}
	
	
	@Override
	public Instruction read() {
		return this.contents;
	}
	
	
	@Override
	public Instruction read(int index) {
		return this.contents;
	}
	

	@Override
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;		
	}
	
	
	@Override
	public void clear() {
		this.contents = null;
		fireUpdate(0, display());
	}	
	

	@Override
	public void clear(int index) {
		this.contents = null;
		
	}
	
	
	
	/*
	 * Formats contents into a String representation for GUI display (used with
	 * IR objects only; returns null for IRfile).
	 * 
	 * @return String the String format of the instruction register contents.
	 */
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
	private void fireUpdate(final int index, final String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(IR.this, 0, update);
				IR.this.updateListener.handleUpDateEvent(updateEvent);	
			}
		});
	}

}
