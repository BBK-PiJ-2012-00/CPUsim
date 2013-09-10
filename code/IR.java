package code;

import javax.swing.SwingUtilities;

public class IR implements InstructionRegister {
	private Instruction contents; //Instruction in the instruction register
	private UpdateListener updateListener;
	
	public void loadIR(Instruction instr) {
		this.contents = instr;
//		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, 0, this.display());//0 is register index (always 0 for standard IR)
//		updateListener.handleUpDateEvent(updateEvent);
		fireUpdate(0, display());
	}
	
	public Instruction read() {
		return this.contents;
	}
	

	@Override
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;		
	}
	
	@Override
	public void clear() {
		System.out.println("Standard IR clear() called.");
		this.contents = null;
//		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, 0, this.display());
//		updateListener.handleUpDateEvent(updateEvent);
		fireUpdate(0, display());
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
		return this.contents;
	}

	@Override
	public void clear(int index) {
		this.contents = null;
		
	}

	@Override
	public void loadIR(int index, Instruction instr) {
		this.contents = instr;
//		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, 0, this.display());
//		updateListener.handleUpDateEvent(updateEvent);	
		fireUpdate(0, display());
	}
	
	//GUI events should be handled from EDT
	//This adds the update event to the EDT thread. Need to test this works on the GUI
	@Override
	public void fireUpdate(final int index, final String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(IR.this, 0, update);
				IR.this.updateListener.handleUpDateEvent(updateEvent);	
			}
		});
	}

}
