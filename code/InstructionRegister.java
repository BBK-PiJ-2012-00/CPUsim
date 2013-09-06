package code;

/*
 * An interface for the instruction register. The instruction register holds type Instruction only; no Data
 * objects.
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
	
	public void registerListener(UpdateListener listener);
	
	public void clear();
	
	public String display(); //Not strictly necessary; can just use read()

	public Instruction read(int index);

	public void clear(int index);
}
