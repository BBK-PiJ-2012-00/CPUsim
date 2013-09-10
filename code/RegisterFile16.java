package code;

import javax.swing.SwingUtilities;

/*
 * 16 general purpose registers. Although RISC machines typically have 32, 16 is likely to be
 * adequate initially and is simpler from the pedagogical perspective.
 * 
 * It is possible that the last register (r15) could be reserved as a control/status register,
 * for branch comparisons and other test conditions (could be marked as such on the GUI).
 */
public class RegisterFile16 implements RegisterFile {
	private Data[] generalPurposeRegisters = new Data[16]; //Enables general purpose registers to hold multiple data types
	//Should this be of type Operand?
	private UpdateListener updateListener; //to handle update events every time a register is updated
	
	
	@Override
	public void write(int index, Data data) {
		if (index > -1 && index < 16) { //Ensure valid index (0-15)
			if (data == null) { //Indicates clearing of register field on GUI
//				ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, index, "");
//				updateListener.handleUpDateEvent(updateEvent);	
				fireUpdate(index, "");
			}
			else {
				generalPurposeRegisters[index] = data;
//				ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, index, data.toString());
//				updateListener.handleUpDateEvent(updateEvent);
				fireUpdate(index, data.toString());
			}
		}
		//Otherwise do nothing (throw exception?)
	}
	
	@Override
	public Data read(int index) {
		if (index > -1 && index < 16) { //Ensure valid index (0-15)
			return generalPurposeRegisters[index];
		}
		return null;
	}
	
	@Override
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;		
	}
	
	//GUI events should be handled from EDT
	//This adds the update event to the EDT thread. Need to test this works on the GUI
	@Override
	public void fireUpdate(final int index, final String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(RegisterFile16.this, index, update);
				RegisterFile16.this.updateListener.handleUpDateEvent(updateEvent);	
			}
		});
	}
	

}
