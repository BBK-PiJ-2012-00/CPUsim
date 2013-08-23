package code;

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
				ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, index, "");
				updateListener.handleUpDateEvent(updateEvent);				
			}
			else {
				generalPurposeRegisters[index] = data;
				ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, index, data.toString());
				updateListener.handleUpDateEvent(updateEvent);
			}
		}
		//Otherwise do nothing (throw exception?)
	}
	
	@Override
	public Data read(int index) {
		if (index > -1 && index < 16) { //Ensure valid index (0-15)
			return generalPurposeRegisters[index];
		}
		//Throw exception?
		return null;
	}
	
	@Override
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;		
	}
	

}
