package code;

/*
 * 16 general purpose registers. Although RISC machines typically have 32, 16 is likely to be
 * adequate initially and is simpler from the pedagogical perspective.
 */
public class RegisterFile16 implements RegisterFile {
	private Data[] generalPurposeRegisters = new Data[16]; //Enables general purpose registers to hold multiple data types
	private int pointer = 0; //Likely unecessary.
	
	@Override
	public void write(int index, Data data) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Data read(int index) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
