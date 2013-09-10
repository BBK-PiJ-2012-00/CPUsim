package code;

/*
 * An interface for the instruction register (IR). The instruction register holds type Instruction only; no Data
 * objects.  Important to note is that both IR and IRfile implement this interface; the IRfile class is for use
 * with the pipelined stages, the IR class for standard execution.
 */
public interface InstructionRegister {
	
	/*
	 * Loads an instruction into the IR.
	 * 
	 * @param Instruction instr the instruction to be loaded.
	 */
	public void loadIR(Instruction instr);
	
	
	/*
	 * Reads the contents of the IR.
	 * 
	 * @return Instruction the instruction held in the IR.
	 */
	public Instruction read();
	
	
	/*
	 * Reads the contents an IRfile object at the specified index.
	 * If used with an IR object, the index is ignored and the IR is
	 * read.
	 * 
	 * @param int index the index of the IRfile slot to be read.
	 * @return Instruction the instruction contained in the IRfile
	 * at the specified index.
	 */
	public Instruction read(int index);
	
	
	/*
	 * Registers an UpdateListener to prompt GUI updates.
	 * 
	 * @param UpdateListener listener the listener to be registered.
	 */
	public void registerListener(UpdateListener listener);
	
	
	/*
	 * Sets the contents of the IR to null (clears all slots of an IRfile). 
	 */
	public void clear();
	
	
	/*
	 * Sets the contents of the specified index of an IRfile object to null.
	 * If used with an IR object, the index will be ignored and the IR will be
	 * cleared.
	 * 
	 * @param int index the index of the IRfile register to be cleared.
	 */
	public void clear(int index);
	
	
	/*
	 * Formats contents into a String representation for GUI display (used with
	 * IR objects only; returns null for IRfile).
	 * 
	 * @return String the String format of the instruction register contents.
	 */
	public String display();

	
	/*
	 * For use with IRfile (pipelined mode). For the standard IR class,
	 * the index parameter is ignored and the instruction is loaded into
	 * the IR (although this method isn't called from standard stages, only
	 * the pipelined stages. The index is ignored as a safeguard).
	 * 
	 * @param Instruction instr the instruction to be loaded into the IR at
	 * the specified index.
	 * @param int index the index of the slot in the IRfile.
	 */
	public void loadIR(int index, Instruction instr);
	
	


	void fireUpdate(int index, String update);
}
