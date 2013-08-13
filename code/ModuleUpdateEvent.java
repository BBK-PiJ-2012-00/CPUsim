package code;

import java.util.EventObject;

public class ModuleUpdateEvent extends EventObject {
	private String update; //The String with the update value
	private int register; //For general purpose registers

	public ModuleUpdateEvent(Object source, String update) {
		super(source);
		this.update = update;
	}
	
	public ModuleUpdateEvent(Object source, int register, String update) {
		super(source);
		this.update = update;
		this.register = register;
	}
	
	public String getUpdate() {
		return this.update;
	}
	
	public int getRegisterReference() {
		return this.register;
	}

}
