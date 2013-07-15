package code;

public class DataLine implements BusLine {
	private Data data; //DataLine will carry data of type Instruction and int at the very lease; Data required as supertype.

	@Override
	public void put(int value) {
		data = value;
	}

	@Override
	public Data read() { //Integers aren't the only data type to traverse data line
		return data;
	}

}
