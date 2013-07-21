package code;

/*
 * 16 general purpose registers. Although RISC machines typically have 32, 16 is likely to be
 * adequate initially and is simpler from the pedagogical perspective.
 */
public class RegisterFile16 implements RegisterFile {
	private Data[] generalPurposeRegisters = new Data[16]; //Enables general purpose registers to hold multiple data types
	//Should this be of type Operand?
	
	@Override
	public void write(int index, Data data) {
		if (index > -1 && index < 16) { //Ensure valid index (0-15)
			generalPurposeRegisters[index] = data;
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
	

}
