package code;

public class DataLineImpl implements DataLine {
	private Data data;

	@Override
	public void put(Data value) {
		data = value;
	}

	@Override
	public Data read() {
		return data;
	}


}
