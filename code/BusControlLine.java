package code;

public interface BusControlLine {
	
	public void setAddressLine(int addr);
	
	public int readAddressLine();
	
	public void setDataLine(int data);
	
	public int readDataLine();

}
