package code;

/*
 * Interface representing stages of the instruction cycle. Stages are represented as objects as opposed
 * to simple methods to allow for pipelined execution without having to use duplicate, re-written code.
 * This structure allows for one thread to be active within each stage at the same time, or when not in
 * pipelined execution, for the main thread to progress through each stage.
 */
public interface InstructionCycleStage extends Runnable {

}
