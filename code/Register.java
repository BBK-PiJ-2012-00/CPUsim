package code;

public interface Register {		
	
	public Operand read();
	
	public void write(Operand operand);
	
	public void registerListener(UpdateListener listener);
	
	public String display();
	
	

}
