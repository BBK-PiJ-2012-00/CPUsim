package code;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.SwingUtilities;

public class PipelinedExecuteStage extends ExecuteStage {
	private BlockingQueue<Instruction> fetchToExecuteQueue;
	private BlockingQueue<Instruction> executeToWriteQueue;
	//private boolean active;
	
	private FetchDecodeStage fetchDecodeStage; //This has a field as reference is only needed in pipelined mode
	private Thread fetchDecodeThread;
	
	private Thread writeBackThread; //Managed by this stage
	
	
	public PipelinedExecuteStage(BusController systemBus, InstructionRegister ir, ProgramCounter pc, RegisterFile genRegisters,
			Register statusRegister, MemoryBufferRegister mbr, MemoryAddressRegister mar,
			BlockingQueue<Instruction> fetchToExecuteQueue, BlockingQueue<Instruction> executeToWriteQueue, FetchDecodeStage fdStage,
			WriteBackStage wbStage) {
		
		super(systemBus, ir, pc, genRegisters, statusRegister, mbr, mar, wbStage);
		
		this.fetchToExecuteQueue = fetchToExecuteQueue;
		this.executeToWriteQueue = executeToWriteQueue;
		
		this.fetchDecodeStage = fdStage;
	}
	
	public boolean instructionExecute(int opcode) {
		//this.fireUpdate("\n** INSTRUCTION EXECUTION STAGE ** \n");
		
		switch (opcode) {
		
			case 1: //A LOAD instruction
					
					System.out.println("E: about to accesMemory()");
					boolean loadSuccessful = accessMemory(false, true, false); //For pipelined execution, there is the possibility of conflicts
					//with the f/d stage, as both this operation and fetch operations in the f/d stage make use of the
					//MAR and MBR registers. accessMemory() is synchronized on a static lock object and ensures that
					//only one of a LOAD, STORE or instruction fetch operation may take place at any one time.
					return loadSuccessful;
					

					//break;
					
					
			case 2: //A STORE instruction
				
					boolean storeSuccessful = accessMemory(false, false, true);	
					return storeSuccessful;
					//break;
					
					
			case 3: //A MOVE instruction (moving data between registers)
					this.fireUpdate("Executing MOVE instruction: \n");
					
					if (getIR().read(1).getField2() == 16) { //ConditionCodeRegister destination reference
						getCC().write((Operand)getGenRegisters().read(getIR().read(1).getField1())); //Write operand from source register
						//to condition/status register
						
						this.fireUpdate("Loaded operand " + getGenRegisters().read(getIR().read(1).getField1()) + 
								" into condition code register\n");
						
						setWaitStatus(true);
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
							setWaitStatus(false);
							return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
						}
						setWaitStatus(false);
					}
					
					else { //Register-register move
					
						getGenRegisters().write(getIR().read(1).getField2(), getGenRegisters().read(getIR().read(1).getField1()));
						//Write to the destination specified in field2 of instr held in ir, the instr held in the register
						//specified by field1 of the instruction in the ir.
						
						this.fireUpdate("Operand " + getGenRegisters().read(getIR().read(1).getField1()) + 
								" moved into r" + getGenRegisters().read(getIR().read(1).getField2()) + "\n");
						
						setWaitStatus(true);
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
							setWaitStatus(false);
							return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
						}
						setWaitStatus(false);
					}
					
					
					getGenRegisters().write(getIR().read(1).getField1(), null); //Complete the move by resetting register source
					
					setWaitStatus(true);
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
						setWaitStatus(false);
						return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
					}
					setWaitStatus(false);
					
					break;
					
					
					
			case 4: //An ADD instruction (adding contents of one register to second (storing in the first).
					Operand op1 = (Operand) getGenRegisters().read(getIR().read(1).getField1()); //access operand stored in first register
					Operand op2 = (Operand) getGenRegisters().read(getIR().read(1).getField2());//access operand stored in second register
					Operand result = ALU.AdditionUnit(op1, op2); //Have ALU perform operation
					fireUpdate("Operands " + op1 + " and " + op2 + " loaded from general purpose \nregisters into ALU " +
							"for ADD operation: " + op1 + " + " + op2 + "\n");
					
					setWaitStatus(true);
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
						setWaitStatus(false);
						return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
					}
					setWaitStatus(false);
					
					//writeBackStage.receive(result); //Call write back stage to store result of addition
					//writeBackStage.run();
					boolean forwardSuccessful = this.forward(result);
					if (!forwardSuccessful) { //Indicates interrupted
						return false; //Stop execution
					}
					
//					fireUpdate("\n** WRITE BACK STAGE **\n");//Simpler to place this here than within writeBackStage object
//					fireUpdate("Result operand " + result + " written to r" + getIR().read(1).getField1() + " from ALU\n");
					
					setWaitStatus(true);
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
						setWaitStatus(false);
						return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
					}
					setWaitStatus(false);
					
					ALU.clearFields();
					
					break;
					
			
			case 5: //A SUB instruction (subtracting contents of one register from a second (storing in the first).
					op1 = (Operand) getGenRegisters().read(getIR().read(1).getField1()); //access first operand
					op2 = (Operand) getGenRegisters().read(getIR().read(1).getField2()); //access second operand
					result = ALU.SubtractionUnit(op1, op2);
					fireUpdate("Operands " + op1 + " and " + op2 + " loaded from general purpose \nregisters into ALU " +
							"for SUB operation: " + op1 + " - " + op2 + "\n");
					
					setWaitStatus(true);
					try {
						wait(); //Makes more sense to place wait here than to complicate writeBack stage.
					} catch (InterruptedException e) {
						e.printStackTrace();
						setWaitStatus(false);
						return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
					}
					setWaitStatus(false);
					
					//writeBackStage.receive(result);
					//writeBackStage.run();
					this.forward(result);
					
//					fireUpdate("\n** WRITE BACK STAGE **\n");//Simpler to place this here than within writeBackStage object
//					fireUpdate("Result operand " + result + " written to r" + getIR().read(1).getField1() + " from ALU\n");
//					
					setWaitStatus(true);
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
						setWaitStatus(false);
						return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
					}
					setWaitStatus(false);
					
					ALU.clearFields();
					
					break;	
					
					
			case 6: //A DIV instruction (dividing contents of one register by contents of another (storing in the first).
					op1 = (Operand) getGenRegisters().read(getIR().read(1).getField1()); //access first operand
					op2 = (Operand) getGenRegisters().read(getIR().read(1).getField2()); //access second operand
					result = ALU.DivisionUnit(op1, op2); //op1 / op2
					fireUpdate("Operands " + op1 + " and " + op2 + " loaded from general purpose \nregisters into ALU " +
							"for DIV operation: " + op1 + " / " + op2 + "\n");
					
					setWaitStatus(true);
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
						setWaitStatus(false);
						return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
					}
					setWaitStatus(false);
					
					//writeBackStage.receive(result);
					//writeBackStage.run();
					this.forward(result);
					
//					fireUpdate("\n** WRITE BACK STAGE **\n");//Simpler to place this here than within writeBackStage object
//					fireUpdate("Result operand " + result + " written to r" + getIR().read(1).getField1() + " from ALU\n");
					
					setWaitStatus(true);
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
						setWaitStatus(false);
						return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
					}
					setWaitStatus(false);
					
					ALU.clearFields();
					
					break;	
					
					
			case 7: //A MUL instruction (multiplying contents of one register by contents of another (storing result in first).
					op1 = (Operand) getGenRegisters().read(getIR().read(1).getField1()); //access first operand
					op2 = (Operand) getGenRegisters().read(getIR().read(1).getField2()); //access second operand
					result = ALU.MultiplicationUnit(op1, op2); //op1 * op2
					fireUpdate("Operands " + op1 + " and " + op2 + " loaded from general purpose \nregisters into ALU " +
							"for MUL operation: " + op1 + " * " + op2 + "\n");
					
					setWaitStatus(true);
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
						setWaitStatus(false);
						return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
					}
					setWaitStatus(false);
					
					//writeBackStage.receive(result);
					//writeBackStage.run();
					this.forward(result);
					
//					fireUpdate("\n** WRITE BACK STAGE **\n");//Simpler to place this here than within writeBackStage object
//					fireUpdate("Result operand " + result + " written to r" + getIR().read(1).getField1() + " from ALU\n");
					
					setWaitStatus(true);
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
						setWaitStatus(false);
						return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
					}
					setWaitStatus(false);
					
					ALU.clearFields();
					
					break;
					
					
					
			case 8: //A BR instruction (unconditional branch to memory location in instruction field 1).
					getPC().setPC(getIR().read(1).getField1());
					fireUpdate("PC set to " + getIR().read(1).getField1() + " as result of " + getIR().read(1).getOpcode() + 
							" operation\n");
					//Pipeline needs flushing!! This involves ditching the instruction being fetched; clear IR file 0.
					//Fetch stage also needs to go back to beginning of cycle, with new value in pc.
					//Need a reference to fetch decode stage
					//Have a method pipelineFlush() on f/d stage, which restarts its cycle
					
					pipelineFlush();
					
					
					setWaitStatus(true);
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
						setWaitStatus(false);
						return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
					}
					setWaitStatus(false);
					
					
					
					break;
					
				
			case 9: //A BRZ instruction (branch if value in status register is zero).
					if (getCC().read().unwrapInteger() == 0) {
						getPC().setPC(getIR().read(1).getField1()); //If statusRegister holds 0, set PC to new address held in instruction
						fireUpdate("PC set to " + getIR().read(1).getField1() + " as result of " + getIR().read(1).getOpcode() 
								+ " operation\n");
						
						pipelineFlush();
						
						setWaitStatus(true);
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
							setWaitStatus(false);
							return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
						}
						setWaitStatus(false);
					}
					
					else { //If condition code register doesn't hold 0, provide activity monitor comment to say branch not taken
						fireUpdate("Branch (BRZ) not taken as condition code\nvalue does not equal 0\n");
						
						setWaitStatus(true);
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
							setWaitStatus(false);
							return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
						}
						setWaitStatus(false);				
						
					}
					break;
					
					
			case 10: //A SKZ instruction (skip the next instruction (increment PC by one) if status register holds 0).
					 if (getCC().read().unwrapInteger() == 0) {
						 getPC().incrementPC();
						 fireUpdate("PC set to " + getPC().getValue() + " as result of " + getIR().read(1).getOpcode() + 
								 " operation\n");
				
						 pipelineFlush();
						 
			 			setWaitStatus(true);
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
							setWaitStatus(false);
							return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
					  	}
					    setWaitStatus(false);	
					    
					 }
					 
					 else { //If condition code register does not hold value of 0, provide activity monitor comment to say skip not taken
						 fireUpdate("Skip (SKZ) instruction not executed as condition\ncode value does not equal 0\n");
						 
						setWaitStatus(true);
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
							setWaitStatus(false);
							return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
						}
						setWaitStatus(false);			
								
					 }
					 break;
					 
					 
			case 11: //A BRE instruction (branch if status reg. contents = contents of register ref. in instruction)
					 int genRegRef = getIR().read(1).getField2(); //Reference to register referred to in instruction
					 if (getCC().read().equals((Operand) getGenRegisters().read(genRegRef))) { //If equal
						 getPC().setPC(getIR().read(1).getField1()); //Set PC to equal address in field1 of instruction in ir
						 fireUpdate("PC set to " + getIR().read(1).getField1() + " as result of " + getIR().read(1).getOpcode() 
								 + " operation\n");
						
						 
						 pipelineFlush();
						 
						setWaitStatus(true);
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
							setWaitStatus(false);
							return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
						}
						setWaitStatus(false);			
					 }
					 
					 else {  //If not equal, do nothing other than provide activity monitor comment to say branch not taken
						 fireUpdate("Branch (BRE) not taken as condition code\nvalue does not equal " + 
								 getGenRegisters().read(getIR().read(1).getField2()) + " (contents of r" + 
								 getIR().read(1).getField2() + ")\n");
						 
						 
						setWaitStatus(true);
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
							setWaitStatus(false);
							return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
						}
						setWaitStatus(false);			
						 
					 }
					 break; 
					 
			case 12: //A BRNE instruction (branch if status reg. contents != contents of register ref. in instruction)
					 genRegRef = getIR().read(1).getField2(); //Reference to register referred to in instruction
					 if (!(getCC().read().equals((Operand) getGenRegisters().read(genRegRef)))) { //If not equal
						 getPC().setPC(getIR().read(1).getField1()); //Set PC to equal address in field1 of instruction in ir	
						 fireUpdate("PC set to " + getIR().read(1).getField1() + " as result of " + getIR().read(1).getOpcode() 
								 + " operation\n");
						 
						pipelineFlush();
						 
						 setWaitStatus(true);
						 try {
							 wait();
						 } catch (InterruptedException e) {
							 e.printStackTrace();
							 setWaitStatus(false);
							 return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
						 }
						 setWaitStatus(false);			
						 
					 }
					 
					 else { //If equal, do nothing other than provide activity monitor comment to say branch not taken
						 fireUpdate("Branch (BRNE) not taken as condition code\nvalue equals " + 
								 getGenRegisters().read(getIR().read(1).getField2()) + " (contents of r" + 
								 getIR().read(1).getField2() + ")\n");
						 
						 setWaitStatus(true);
						 try {							 
							wait();
						 } catch (InterruptedException e) {
							 e.printStackTrace();
							 setWaitStatus(false);
							 return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
						 }
						 setWaitStatus(false);			
						 
					 }
					 break; 
					 
			
					 
			case 13: //A HALT instruction (stops instruction cycle). For clarity, resets all registers.
					 System.out.println("HALT encountered.");
					 fireUpdate("HALT instruction decoded; end of program");
					 return false; //Signals end of instruction cycle		
		}
		return true;
	}
	
	
	
	/*
	 * This can manage difference between a reset and pipeline flush interrupt to f/d stage? Perhaps not;
	 * if reset is clicked -- THIS object will get the interrupt, either while it's waiting to take from the queue,
	 * put something in the queue, or executing. The fetch and writeBack threads will continue unless directed
	 * otherwise.  So, when THIS thread is interrupted, interrupt the others.  Otherwise, this thread will just
	 * restart the other threads in the event of a pipeline flush.
	 */
	public synchronized void run() {
		setActive(true);
		
		System.out.println("Entering E run(), SW thread ID: " + Thread.currentThread().getId());
		fetchDecodeThread = new Thread(fetchDecodeStage);
		fetchDecodeThread.start();
		
		writeBackThread = new Thread(getWriteBackStage());
		writeBackThread.start(); //This runs until reset is clicked or HALT is encountered.
		
		
		
		while (isActive()) {			
			try {
				fireUpdate("Waiting for instruction from fetch/decode stage.\n");
				Instruction instr = this.fetchToExecuteQueue.take();
				((IRfile) getIR()).loadIR(1, instr);
				int opcodeValue = instr.getOpcode().getValue();
				fireUpdate("Beginning execution of instruction " + instr.toString() + "\n received from F/D stage.");
				System.out.println("Just taken " + instr + " from queue");
				System.out.println("IR at 1 " + getIR().read(1));
				setActive(this.instructionExecute(opcodeValue));
				System.out.println("E: just returned from instructionExecute.");
				if (!isActive()) {
					System.out.println("Throwing homemade interrupt exception after returning false from execute.");
					throw new InterruptedException(); //Prompt termination of other threads
				}
				getIR().clear(1); //Reset
			}
			catch (InterruptedException ex) {
				ex.printStackTrace();
				fetchDecodeThread.interrupt();
				writeBackThread.interrupt();
				if (((ReentrantLock) getLock()).isHeldByCurrentThread()) {//If interrupted during accessMemory(), must release lock
					getLock().unlock();
				}
				System.out.println("!@!@!@!@" + ((ReentrantLock) getLock()).isLocked());
				setActive(false);
				return;
			}
			
		}
		//Terminate other stages once this stage is no longer active; alternatively use static boolean for active
		fetchDecodeThread.interrupt();
		writeBackThread.interrupt();
		return;
	}

	@Override
	public boolean forward(Operand result) {
		try {
			getWriteBackStage().receive(result); //Transfer result
			executeToWriteQueue.put(getIR().read(1));
			getIR().clear(1); //Clear IR index 2 once instruction passed.
		} catch (InterruptedException e) {
			e.printStackTrace();
			setActive(false);
			return false;
		}
		return true;

	}
	
	
	public void pipelineFlush() {
		System.out.println("** FLUSH **");
		((PipelinedFetchDecodeStage) fetchDecodeStage).setPipelineFlush(true); //Allows for differentiation between reset and flush
		fetchDecodeThread.interrupt(); //Poll Thread.isInterrupted() at crucial points, incl. System Bus
		getIR().clear(); //Clear all registers; be wary of clearing third IR register because WB stage may be busy
		while (fetchDecodeThread.isAlive()); //Wait for f/d thread to terminate
		fetchDecodeThread = new Thread(fetchDecodeStage); //Must create new thread, as old one cannot be restarted
		fetchDecodeThread.start(); //Restart f/d thread, which will fetch from new PC address value
		
	}
	
		//GUI events should not be handled from this thread but from EDT or SwingWorker
		//This adds the update event to the EDT thread. Need to test this works on the GUI
		@Override
		public void fireUpdate(final String update) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
				    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(PipelinedExecuteStage.this, update);
					PipelinedExecuteStage.this.getUpdateListener().handleUpDateEvent(updateEvent);	
				}
			});
		}
		
	

}
