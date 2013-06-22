package code;

public class DataLine implements BusLine {
	private int data;

	@Override
	public void put(int value) {
		data = value;
	}

	@Override
	public int read() {
		return data;
	}

}
