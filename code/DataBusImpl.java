package code;

public class DataBusImpl implements DataBus {
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
