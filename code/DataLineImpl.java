package code;

public class DataLineImpl implements DataLine {
	private Data data; //DataLine will carry data of type Instruction and int at the very least; Data required as supertype.

	@Override
	public void put(int value) {
		data = value;
	}

	@Override
	public Data read() { //Integers aren't the only data type to traverse data line
		return data;
	}


}
