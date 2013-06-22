package code;

public interface Register {
	
	//For methods common to all registers
	//This should perhaps be an abstract class, as all registers have a contents field
	//There are then General Purpose registers
	//Or, leave this as an interface, and have abstract classes at next level down
	
	public int getContents();
	
	public int setContents();
	
	

}
