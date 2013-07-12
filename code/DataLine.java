package code;

public class DataLine implements BusLine {
	private Data data;

	@Override
	public void put(Data value) {
		data = value;
	}

	@Override
	public int read() { //Currently assumes integers are only data to traverse data line
		Operand intOperand = (Operand) data;
		return intOperand.unwrapInteger();
	}

}
