package code;

import javax.swing.SwingUtilities;

/*
 * For use with pipelined Stages; a file of instruction registers.
 */
public class IRfile implements InstructionRegister {
	private Instruction[] irRegisters;	
	private UpdateListener updateListener;	

	
	public IRfile() {
		irRegisters = new Instruction[3]; //Create register file of size 3
	}
	

	@Override
	public void loadIR(Instruction instr) {
		irRegisters[0] = instr;
		if (instr == null) { //For a clear operation, instr will be null, so update with empty String
			fireUpdate(0, "");
		}
		else {
			fireUpdate(0, instr.toString());
		}
	}
	
	
	@Override
	public void loadIR(int index, Instruction instr) {
		irRegisters[index] = instr;
		if (irRegisters[index] == null) {
		//For a clear operation, instr will be null, so update with empty String
			fireUpdate(index, "");
		}
		else {		
			fireUpdate(index, instr.toString());
		}
	}
		

	@Override
	public Instruction read() {
		return irRegisters[0];  //Return index 0 for no-argument read()
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
			clear(i);
		}
	}
	
	
	@Override
	public void clear(int index) {
		irRegisters[index] = null;
		fireUpdate(index, "");
		
	}
	
	
	//GUI events should be handled from EDT only.
	//This adds the update event to the EDT thread.
	private void fireUpdate(final int index, final String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(IRfile.this, index, update);
				IRfile.this.updateListener.handleUpDateEvent(updateEvent);	
			}
		});
	}


}
