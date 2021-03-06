package code;

import javax.swing.SwingUtilities;

public class StandardExecuteStage extends ExecuteStage {

	public StandardExecuteStage(BusController systemBus, InstructionRegister ir, ProgramCounter pc,	RegisterFile genRegisters,
			Register rCC, MemoryBufferRegister mbr, MemoryAddressRegister mar, WriteBackStage writeBackStage) {
		
		super(systemBus, ir, pc, genRegisters, rCC, mbr, mar, writeBackStage);
	}
	
	
	@Override
	public boolean instructionExecute(int opcode) {
		this.fireUpdate("\n** INSTRUCTION EXECUTION STAGE ** \n");
		switch (opcode) {
		
			case 1: //A LOAD instruction
					boolean successful = accessMemory(false, true, false, false); //Final false to reflect standard mode
					if (!successful) {
						return false;
					}

					break;
					
					
			case 2: //A STORE instruction
					successful = accessMemory(false, false, true, false); //Final false to reflect standard mode
					if (!successful) {
						return false;
					}

					break;
					
					
			case 3: //A MOVE instruction (moving data between registers)
					this.fireUpdate("> Executing MOVE instruction: \n");
					
					if (getIR().read().getField2() == 16) { //ConditionCodeRegister destination reference
						getCC().write((Operand)getGenRegisters().read(getIR().read().getField1()));//Write operand from source register
						//to condition/status register
						
						this.fireUpdate("> Loaded operand " + getGenRegisters().read(getIR().read().getField1()) + 
								" into condition code register\n");
						
						setWaitStatus(true);
						try {
							wait();
						} catch (InterruptedException e) {
						//	e.printStackTrace();
							setWaitStatus(false);
							return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
						}
						setWaitStatus(false);
					}
					
					else if (getIR().read().getField1() == 16) { //ConditionCodeRegister source reference
							getGenRegisters().write(getIR().read().getField2(), getCC().read());
							//Write operand from rCC to general purpose register
							
							
							this.fireUpdate("> Loaded operand " + getCC().read() + " from rCC" + 
									" into r" + getGenRegisters().read(getIR().read().getField2()) + "\n");
							
							setWaitStatus(true);
							try {
								wait();
							} catch (InterruptedException e) {
							//	e.printStackTrace();
								setWaitStatus(false);
								return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
							}
							setWaitStatus(false);
					}
					
					else { //Register-register move
					
						getGenRegisters().write(getIR().read().getField2(), getGenRegisters().read(getIR().read().getField1()));
						//Write to the destination specified in field2 of instr held in ir, the instr held in the register
						//specified by field1 of the instruction in the ir.
						
						this.fireUpdate("> Operand " + getGenRegisters().read(getIR().read().getField1()) + 
								" moved into r" + getGenRegisters().read(getIR().read().getField2()) + "\n");
						
						setWaitStatus(true);
						try {
							wait();
						} catch (InterruptedException e) {
						//	e.printStackTrace();
							setWaitStatus(false);
							return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
						}
						setWaitStatus(false);
					}
					
					
					getGenRegisters().write(getIR().read().getField1(), null); //Complete the move by resetting register source
					
					setWaitStatus(true);
					try {
						wait();
					} catch (InterruptedException e) {
					//	e.printStackTrace();
						setWaitStatus(false);
						return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
					}
					setWaitStatus(false);
					
					break;
					
					
					
			case 4: //An ADD instruction (adding contents of one register to second (storing in the first).
					Operand op1 = (Operand) getGenRegisters().read(getIR().read().getField1());//access operand stored in first register
					Operand op2 = (Operand) getGenRegisters().read(getIR().read().getField2());//access operand stored in second register
					Operand result = ALU.AdditionUnit(op1, op2); //Have ALU perform operation
					fireUpdate("> Operands " + op1 + " and " + op2 + " loaded from general purpose \nregisters into ALU " +
							"for ADD operation: " + op1 + " + " + op2 + "\n");
					
					setWaitStatus(true);
					try {
						wait();
					} catch (InterruptedException e) {
					//	e.printStackTrace();
						setWaitStatus(false);
						return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
					}
					setWaitStatus(false);
					
					this.forward(result); //Forward to WB stage
					
					fireUpdate("\n** WRITE BACK STAGE **\n");//Simpler to place this here than within writeBackStage object
					fireUpdate("> Result operand " + result + " written to r" + getIR().read().getField1() + " from ALU\n");
					
					setWaitStatus(true);
					try {
						wait();
					} catch (InterruptedException e) {
					//	e.printStackTrace();
						setWaitStatus(false);
						return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
					}
					setWaitStatus(false);
					
					ALU.clearFields();
					
					break;
					
			
			case 5: //A SUB instruction (subtracting contents of one register from a second (storing in the first).
					op1 = (Operand) getGenRegisters().read(getIR().read().getField1()); //access first operand
					op2 = (Operand) getGenRegisters().read(getIR().read().getField2()); //access second operand
					result = ALU.SubtractionUnit(op1, op2);
					fireUpdate("> Operands " + op1 + " and " + op2 + " loaded from general purpose \nregisters into ALU " +
							"for SUB operation: " + op1 + " - " + op2 + "\n");
					
					setWaitStatus(true);
					try {
						wait(); //Makes more sense to place wait here than to complicate writeBack stage.
					} catch (InterruptedException e) {
				//		e.printStackTrace();
						setWaitStatus(false);
						return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
					}
					setWaitStatus(false);
					
			
					this.forward(result); //Forward result to WB stage
					
					fireUpdate("\n** WRITE BACK STAGE **\n");//Simpler to place this here than within writeBackStage object
					fireUpdate("> Result operand " + result + " written to r" + getIR().read().getField1() + " from ALU\n");
					
					setWaitStatus(true);
					try {
						wait();
					} catch (InterruptedException e) {
				//		e.printStackTrace();
						setWaitStatus(false);
						return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
					}
					setWaitStatus(false);
					
					ALU.clearFields();
					
					break;	
					
					
			case 6: //A DIV instruction (dividing contents of one register by contents of another (storing in the first).
					op1 = (Operand) getGenRegisters().read(getIR().read().getField1()); //access first operand
					op2 = (Operand) getGenRegisters().read(getIR().read().getField2()); //access second operand
					result = ALU.DivisionUnit(op1, op2); //op1 / op2
					fireUpdate("> Operands " + op1 + " and " + op2 + " loaded from general purpose \nregisters into ALU " +
							"for DIV operation: " + op1 + " / " + op2 + "\n");
					
					setWaitStatus(true);
					try {
						wait();
					} catch (InterruptedException e) {
				//		e.printStackTrace();
						setWaitStatus(false);
						return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
					}
					setWaitStatus(false);
					
					this.forward(result);
					
					fireUpdate("\n** WRITE BACK STAGE **\n");//Simpler to place this here than within writeBackStage object
					fireUpdate("> Result operand " + result + " written to r" + getIR().read().getField1() + " from ALU\n");
					
					setWaitStatus(true);
					try {
						wait();
					} catch (InterruptedException e) {
				//		e.printStackTrace();
						setWaitStatus(false);
						return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
					}
					setWaitStatus(false);
					
					ALU.clearFields();
					
					break;	
					
					
			case 7: //A MUL instruction (multiplying contents of one register by contents of another (storing result in first).
					op1 = (Operand) getGenRegisters().read(getIR().read().getField1()); //access first operand
					op2 = (Operand) getGenRegisters().read(getIR().read().getField2()); //access second operand
					result = ALU.MultiplicationUnit(op1, op2); //op1 * op2
					fireUpdate("> Operands " + op1 + " and " + op2 + " loaded from general purpose \nregisters into ALU " +
							"for MUL operation: " + op1 + " * " + op2 + "\n");
					
					setWaitStatus(true);
					try {
						wait();
					} catch (InterruptedException e) {
				//		e.printStackTrace();
						setWaitStatus(false);
						return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
					}
					setWaitStatus(false);
				
					this.forward(result); //Forward resut to WB stage
					
					fireUpdate("\n** WRITE BACK STAGE **\n");//Simpler to place this here than within writeBackStage object
					fireUpdate("> Result operand " + result + " written to r" + getIR().read().getField1() + " from ALU\n");
					
					setWaitStatus(true);
					try {
						wait();
					} catch (InterruptedException e) {
				//		e.printStackTrace();
						setWaitStatus(false);
						return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
					}
					setWaitStatus(false);
					
					ALU.clearFields();
					
					break;
					
					
					
			case 8: //A BR instruction (unconditional branch to memory location in instruction field 1).
					getPC().setPC(getIR().read().getField1());
					fireUpdate("> PC set to " + getIR().read().getField1() + " as result of " + getIR().read().getOpcode() + 
							" operation\n");
					
					setWaitStatus(true);
					try {
						wait();
					} catch (InterruptedException e) {
					//	e.printStackTrace();
						setWaitStatus(false);
						return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
					}
					setWaitStatus(false);
					
					break;
					
				
			case 9: //A BRZ instruction (branch if value in status register is zero).
					if (getCC().read().unwrapInteger() == 0) {
						getPC().setPC(getIR().read().getField1());//If rCC holds 0 set PC to new address held in instruction
						fireUpdate("> PC set to " + getIR().read().getField1() + " as result of " + getIR().read().getOpcode() + 
								" operation\n");
						
						setWaitStatus(true);
						try {
							wait();
						} catch (InterruptedException e) {
					//		e.printStackTrace();
							setWaitStatus(false);
							return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
						}
						setWaitStatus(false);
					}
					
					else { //If condition code register doesn't hold 0, provide activity monitor comment to say branch not taken
						fireUpdate("> Branch (BRZ) not taken as condition code\nvalue does not equal 0\n");
						
						setWaitStatus(true);
						try {
							wait();
						} catch (InterruptedException e) {
					//		e.printStackTrace();
							setWaitStatus(false);
							return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
						}
						setWaitStatus(false);				
						
					}
					break;
					
					
			case 10: //A SKZ instruction (skip the next instruction (increment PC by one) if status register holds 0).
					 if (getCC().read().unwrapInteger() == 0) {
						 getPC().incrementPC();
						 fireUpdate("> PC incremented by 1 as result of " + getIR().read().getOpcode() + 
								 " operation\n");
						 
			 			setWaitStatus(true);
						try {
							wait();
						} catch (InterruptedException e) {
					//		e.printStackTrace();
							setWaitStatus(false);
							return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
					  	}
					    setWaitStatus(false);	
					    
					 }
					 
					//If condition code register does not hold value of 0, provide activity monitor comment to say skip not taken
					 else { 
						 fireUpdate("> Skip (SKZ) instruction not executed as condition\ncode value does not equal 0\n");
						 
						setWaitStatus(true);
						try {
							wait();
						} catch (InterruptedException e) {
						//	e.printStackTrace();
							setWaitStatus(false);
							return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
						}
						setWaitStatus(false);			
								
					 }
					 break;
					 
					 
			case 11: //A BRE instruction (branch if status reg. contents = contents of register ref. in instruction)
					 int genRegRef = getIR().read().getField2(); //Reference to register referred to in instruction
					
					 if (getCC().read().equals((Operand) getGenRegisters().read(genRegRef))) { //If equal
						 getPC().setPC(getIR().read().getField1()); //Set PC to equal address in field1 of instruction in ir
						 fireUpdate("> PC set to " + getIR().read().getField1() + " as result of " + getIR().read().getOpcode() + 
								 " operation\n");
						 
						setWaitStatus(true);
						try {
							wait();
						} catch (InterruptedException e) {
					//		e.printStackTrace();
							setWaitStatus(false);
							return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
						}
						setWaitStatus(false);			
					 }
					 
					 else {  //If not equal, do nothing other than provide activity monitor comment to say branch not taken
						 fireUpdate("> Branch (BRE) not taken as condition code\nvalue does not equal " + 
								 getGenRegisters().read(getIR().read().getField2()) + " (contents of r" + 
								 getIR().read().getField2() + ")\n");
						 
						setWaitStatus(true);
						try {
							wait();
						} catch (InterruptedException e) {
					//		e.printStackTrace();
							setWaitStatus(false);
							return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
						}
						setWaitStatus(false);			
						 
					 }
					 break; 
					 
			case 12: //A BRNE instruction (branch if status reg. contents != contents of register ref. in instruction)
					 genRegRef = getIR().read().getField2(); //Reference to register referred to in instruction
					
					 if (!(getCC().read().equals((Operand) getGenRegisters().read(genRegRef)))) { //If not equal
						 getPC().setPC(getIR().read().getField1()); //Set PC to equal address in field1 of instruction in ir	
						 fireUpdate("> PC set to " + getIR().read().getField1() + " as result of " + getIR().read().getOpcode() + 
								 " operation\n");
						 
						
						 setWaitStatus(true);
						 try {
							 wait();
						 } catch (InterruptedException e) {
					//		 e.printStackTrace();
							 setWaitStatus(false);
							 return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
						 }
						 setWaitStatus(false);			
						 
					 }
					 
					 else { //If equal, do nothing other than provide activity monitor comment to say branch not taken
						 fireUpdate("> Branch (BRNE) not taken as condition code\nvalue equals " + 
								 getGenRegisters().read(getIR().read().getField2()) + " (contents of r" + 
								 getIR().read().getField2() + ")\n");
						 
						 setWaitStatus(true);
						 try {							 
							wait();
						 } catch (InterruptedException e) {
					//		 e.printStackTrace();
							 setWaitStatus(false);
							 return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
						 }
						 setWaitStatus(false);			
						 
					 }
					 break; 
					 
			
					 
			case 13: //A HALT instruction (stops instruction cycle). For clarity, resets all registers.
					 fireUpdate("> HALT instruction decoded; end of program");
					 return false; //Signals end of instruction cycle		
		}
		return true;
	}
	
	
	@Override
	public synchronized void run() {
		setActive(this.instructionExecute(getOpcodeValue())); //Control unit checks status of active; if false, exits instr. cycle.
	}
	


	@Override
	public boolean forward(Operand result) {
		this.getWriteBackStage().receive(result);
		this.getWriteBackStage().run();
		return true;
	}


	//GUI events should not be handled from this thread but from EDT only.
	//This adds the update event to the EDT thread. 
	@Override
	public void fireUpdate(final String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(StandardExecuteStage.this, update);
				StandardExecuteStage.this.getUpdateListener().handleUpDateEvent(updateEvent);	
			}
		});
	}

	

}
