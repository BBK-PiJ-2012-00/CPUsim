package code;

import java.util.EventObject;

public class ModuleUpdateEvent extends EventObject {
	private String update; //The String with the update value

	public ModuleUpdateEvent(Object source, String update) {
		super(source);
		this.update = update;
	}
	
	public String getUpdate() {
		return this.update;
	}

}
