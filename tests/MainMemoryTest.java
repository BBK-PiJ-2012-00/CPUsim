package tests;

import static org.junit.Assert.*;

import code.*;

import org.junit.Test;

public class MainMemoryTest {
	private MainMemory memory = MemoryModule.getInstance(); //Constructor is private, so getInstance() must be used

	@Test
	public void testSingleton() { //Test that only once instance of memory can be created
		MainMemory anotherMemoryInstance = MemoryModule.getInstance();
		assertEquals(memory, anotherMemoryInstance);
	}

}
