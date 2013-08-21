package code;

public abstract class ExecuteStage implements Runnable {
	private BusController systemBus;
	
	private MemoryAddressRegister mar;
	private MemoryBufferRegister mbr;	
	
	private InstructionRegister ir;
	private ProgramCounter pc;
	private RegisterFile genRegisters;
	private Register statusRegister;
	
	private WriteBackStage writeBackStage;
	
	private boolean active;
	private int opcode;
	
	private RegisterListener updateListener;
	
	
	public ExecuteStage(InstructionRegister ir, ProgramCounter pc, RegisterFile genRegisters, Register statusRegister,
			WriteBackStage writeBackStage) {
		systemBus = SystemBusController.getInstance();
		
		mar = MAR.getInstance();
		mbr = MBR.getInstance();
		
		this.ir = ir;
		this.pc = pc;
		this.genRegisters = genRegisters;
		this.statusRegister = statusRegister;
		
		this.writeBackStage = writeBackStage;
	}
	
	public synchronized boolean instructionExecute(int opcode) {
		this.fireUpdate("\n** INSTRUCTION EXECUTION STAGE ** \n");
		switch (opcode) {
		
			case 1: //A LOAD instruction
					this.fireUpdate("Executing LOAD instruction; memory address " +	ir.read().getField1() + "\nplaced into MAR to initiate operand fetch \n");
					mar.write(ir.read().getField1()); //Load mar with source address of instruction in IR
					//Request a read from memory via system bus, with address contained in mar
					
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					systemBus.transferToMemory(mar.read(), null);
					this.fireUpdate("Operand " + mbr.read().toString() + " loaded from address " + ir.read().getField1() + " into MBR\n");
					
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					mar.write(-1); //Reset MAR
//					
//					try {
//						wait();
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					
					if (ir.read().getField2() == 16) { //ConditionCodeRegister reference
						statusRegister.write((Operand) mbr.read()); //Write operand in mbr to condition/status register
						
						this.fireUpdate("Loaded operand " + mbr.read() + " into condition code register\n");
						
						try {
							wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					
					
					//Transfer data in mbr to destination field in instruction in ir (field2).
					genRegisters.write(ir.read().getField2(), mbr.read());//getField2() gives reg. destination index, mbr.read()
					//gives the operand to be moved from mbr to genRegisters at index given in getField2().
					this.fireUpdate("Operand " + mbr.read() + " loaded into r" + ir.read().getField2() + "\n");
					
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					mbr.write(null); //Reset MBR
					break;
					
					
			case 2: //A STORE instruction
					this.fireUpdate("Executing STORE instruction; destination memory \naddress " + ir.read().getField2() + 
						" placed into MAR \n");
					mar.write(ir.read().getField2()); //Load mar with destination (memory address)
					
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					mbr.write(genRegisters.read(ir.read().getField1())); //Write to mbr the data held in genRegisters at index
					//given by field1(source) of instruction held in IR.
					this.fireUpdate("Operand " + genRegisters.read(ir.read().getField1()) + " loaded from r" + ir.read().getField1() + 
							" into MBR\n");
					
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					systemBus.transferToMemory(mar.read(), mbr.read()); //Transfer contents of mbr to address specified in mar
					
					this.fireUpdate("Operand " + genRegisters.read(ir.read().getField1()) + " stored in memory address " +
							ir.read().getField2() + "\n");
					
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					break;
					
					
			case 3: //A MOVE instruction (moving data between registers)
					this.fireUpdate("Executing MOVE instruction \n");
					
					if (ir.read().getField2() == 16) { //ConditionCodeRegister destination reference
						statusRegister.write((Operand)genRegisters.read(ir.read().getField1())); //Write operand from source register
						//to condition/status register
						
						this.fireUpdate("Loaded operand " + genRegisters.read(ir.read().getField1()) + 
								" into condition code register\n");
						
						try {
							wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					else { //Register-register move
					
						genRegisters.write(ir.read().getField2(), genRegisters.read(ir.read().getField1()));
						//Write to the destination specified in field2 of instr held in ir, the instr held in the register
						//specified by field1 of the instruction in the ir.
						
						this.fireUpdate("Operand " + genRegisters.read(ir.read().getField1()) + 
								" moved into r" + genRegisters.read(ir.read().getField2()) + "\n");
						
						try {
							wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					
					genRegisters.write(ir.read().getField1(), null); //Complete the move by resetting register source
					
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					break;
					
					
					
			case 4: //An ADD instruction (adding contents of one register to second (storing in the first).
					Operand op1 = (Operand) genRegisters.read(ir.read().getField1()); //access operand stored in first register
					Operand op2 = (Operand) genRegisters.read(ir.read().getField2());//access operand stored in second register
					Operand result = ALU.AdditionUnit(op1, op2); //Have ALU perform operation
					fireUpdate("Operands " + op1 + " and " + op2 + " loaded from general purpose \nregisters into ALU " +
							"for ADD operation: " + op1 + " + " + op2 + "\n");
					
					try { //Makes more sense to put the wait here than complicate write back stage
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					writeBackStage.receive(result); //Call write back stage to store result of addition
					writeBackStage.run();
					fireUpdate("\n** WRITE BACK STAGE **\n");//Simpler to place this here than within writeBackStage object
					fireUpdate("Result operand " + result + " written to r" + ir.read().getField1() + " from ALU\n");
					
					try { //Makes more sense to put the wait here than complicate write back stage
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					ALU.clearFields();
					
					break;
					
			
			case 5: //A SUB instruction (subtracting contents of one register from a second (storing in the first).
					op1 = (Operand) genRegisters.read(ir.read().getField1()); //access first operand
					op2 = (Operand) genRegisters.read(ir.read().getField2()); //access second operand
					result = ALU.SubtractionUnit(op1, op2);
					fireUpdate("Operands " + op1 + " and " + op2 + " loaded from general purpose \nregisters into ALU " +
							"for SUB operation: " + op1 + " - " + op2 + "\n");
					
					try { //Makes more sense to put the wait here than complicate write back stage
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					writeBackStage.receive(result);
					writeBackStage.run();
					fireUpdate("\n** WRITE BACK STAGE **\n");//Simpler to place this here than within writeBackStage object
					fireUpdate("Result operand " + result + " written to r" + ir.read().getField1() + " from ALU\n");
					
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					ALU.clearFields();
					
					break;	
					
					
			case 6: //A DIV instruction (dividing contents of one register by contents of another (storing in the first).
					op1 = (Operand) genRegisters.read(ir.read().getField1()); //access first operand
					op2 = (Operand) genRegisters.read(ir.read().getField2()); //access second operand
					result = ALU.DivisionUnit(op1, op2); //op1 / op2
					fireUpdate("Operands " + op1 + " and " + op2 + " loaded from general purpose \nregisters into ALU " +
							"for DIV operation: " + op1 + " / " + op2 + "\n");
					
					try { //Makes more sense to put the wait here than complicate write back stage
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					writeBackStage.receive(result);
					writeBackStage.run();
					fireUpdate("\n** WRITE BACK STAGE **\n");//Simpler to place this here than within writeBackStage object
					fireUpdate("Result operand " + result + " written to r" + ir.read().getField1() + " from ALU\n");
					
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					ALU.clearFields();
					
					break;	
					
					
			case 7: //A MUL instruction (multiplying contents of one register by contents of another (storing result in first).
					op1 = (Operand) genRegisters.read(ir.read().getField1()); //access first operand
					op2 = (Operand) genRegisters.read(ir.read().getField2()); //access second operand
					result = ALU.MultiplicationUnit(op1, op2); //op1 * op2
					fireUpdate("Operands " + op1 + " and " + op2 + " loaded from general purpose \nregisters into ALU " +
							"for MUL operation: " + op1 + " * " + op2 + "\n");
					
					try { //Makes more sense to put the wait here than complicate write back stage
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					writeBackStage.receive(result);
					writeBackStage.run();
					fireUpdate("\n** WRITE BACK STAGE **\n");//Simpler to place this here than within writeBackStage object
					fireUpdate("Result operand " + result + " written to r" + ir.read().getField1() + " from ALU\n");
					
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					ALU.clearFields();
					
					break;
					
					
					
			case 8: //A BR instruction (unconditional branch to memory location in instruction field 1).
					pc.setPC(ir.read().getField1());
					fireUpdate("PC set to " + ir.read().getField1() + " as result of " + ir.read().getOpcode() + " operation\n");
					
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//A branch instruction updates PC to new memory location
					break;
					
				
			case 9: //A BRZ instruction (branch if value in status register is zero).
					if (statusRegister.read().unwrapInteger() == 0) {
						pc.setPC(ir.read().getField1()); //If statusRegister holds 0, set PC to new address held in instruction
						
						try {
							wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					break; //If not 0, do nothing
					
					
			case 10: //A SKZ instruction (skip the next instruction (increment PC by one) if status register holds 0).
					 if (statusRegister.read().unwrapInteger() == 0) {
						 pc.incrementPC();
						 
						 try {
								wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						 
					 }
					 break; //If not 0, do nothing
					 
					 
			case 11: //A BRE instruction (branch if status reg. contents = contents of register ref. in instruction)
					 int genRegRef = ir.read().getField2(); //Reference to register referred to in instruction
					 if (statusRegister.read().equals((Operand) genRegisters.read(genRegRef))) { //If equal
						 pc.setPC(ir.read().getField1()); //Set PC to equal address in field1 of instruction in ir	
						 
						 try {
								wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					 }
					 break; //If not equal, do nothing
					 
			case 12: //A BRNE instruction (branch if status reg. contents != contents of register ref. in instruction)
					 genRegRef = ir.read().getField2(); //Reference to register referred to in instruction
					 if (!(statusRegister.read().equals((Operand) genRegisters.read(genRegRef)))) { //If not equal
						 pc.setPC(ir.read().getField1()); //Set PC to equal address in field1 of instruction in ir		
						 
						 try {
								wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						 
					 }
					 break; //If equal, do nothing
					 
			
					 
			case 13: //A HALT instruction (stops instruction cycle). For clarity, resets all registers.
					 //pc.setPC(0);
					 //statusRegister.write(null);
					// ir.loadIR(null);	
					 
					 fireUpdate("HALT instruction decoded; end of program");
					 return false; //Signals end of instruction cycle		
		}
		return true;
	}
	
	public WriteBackStage getWriteBackStage() {
		return this.writeBackStage;
	} 
	
	public synchronized void run() {
		active = this.instructionExecute(opcode);
	}
	
	public void setOpcodeValue(int opcode) {
		this.opcode = opcode;
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	
	//public abstract void receive(int opcode);
	
	public abstract void forward(Operand result); //For forwarding execution to WriteBackStage
	
	private void fireUpdate(String update) {
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, update);
		updateListener.handleUpDateEvent(updateEvent);
	}
	
	public void registerListener(RegisterListener listener) {
		this.updateListener = listener;
	}
	

	
		//what about data loaded into MBR that is data (operand) as opposed to instruction; loaded straight to a register
		//http://comminfo.rutgers.edu/~muresan/201_JavaProg/11CPU/Lecture11.pdf
		//have methods to represent storeExecuteCycle, loadExecuteCycle etc, depending on decode of fetched instruction
		//This is instruction fetch -> only covers fetching of an instruction, not its execution; fetching data from memory
		//will be part of executing a LOAD instruction, which occurs in execute. Decode determines nature of instruction; 
		//instructionDecode() method.

}
