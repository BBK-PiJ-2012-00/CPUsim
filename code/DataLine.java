package code;

public class DataLine implements BusLine {
	private int data;

	@Override
	public void put(int value) {
		data = value;
	}

	@Override
	public int read() { //Currently assumes integers are only data to traverse data line
		return data;
	}

}
