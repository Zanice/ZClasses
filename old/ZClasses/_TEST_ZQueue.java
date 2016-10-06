package ZClasses;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ZClasses.ZQueue.Node;

public class _TEST_ZQueue {
	private static ZQueue<Integer> queue;
	private static Random random;
	
	private int[] data;
	
	private static int size_trials = 10;
	private static int add_trials = 100;
	private static int peek_trials = 10;
	private static int next_trials = 10;
	private static int dump_trials = 10;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		random = new Random();
	}

	@Before
	public void setUp() throws Exception {
		queue = new ZQueue<Integer>();
	}

	@After
	public void tearDown() throws Exception {
		if (queue != null)
			queue.dump();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		;
	}

	@Test
	public void size_TEST() {
		assertQueueIsEmpty();
		
		int localsize;
		
		for (int i = 0; i < size_trials; i++) {
			localsize = 0;
		}
	}

	@Test
	public void add_TEST() {
		assertQueueIsEmpty();
		
		for (int i = 0; i < add_trials; i++) {
			if (queue.back != null) {
				assertNotNull(queue.back);
				assertNull(queue.back.next);
				assertEquals((int) queue.back.element, i - 1);
				assertNotNull(queue.front);
				assertEquals((int) queue.front.element, 0);
			}
			else {
				assertNull(queue.back);
				assertNull(queue.front);
			}
			
			queue.add(i);
			
			assertNotNull(queue.back);
			assertNull(queue.back.next);
			assertEquals((int) queue.back.element, i);
			assertNotNull(queue.front);
			assertEquals((int) queue.front.element, 0);
		}
		
		ZQueue<Integer>.Node<Integer> current = queue.front;
		int trial = 0;
		while (trial < add_trials) {
			assertNotNull(current);
			assertEquals((int) current.element, trial);
			current = current.next;
			trial++;
		}
	}

	@Test
	public void peek_TEST() {
		fail("Not yet implemented");
	}

	@Test
	public void next_TEST() {
		fail("Not yet implemented");
	}
	
	@Test
	public void dump_TEST() {
		assertQueueIsEmpty();
		
		for (int i = 0; i < dump_trials; i++) {
			constructRandomData(random(100));
			
			for (Integer piece : data) {
				queue.add(piece);
			
				if (random(10) == 9) {
					queue.dump();
					assertQueueIsEmpty();
				}
			}
			
			queue.dump();
			assertQueueIsEmpty();
		}
	}
	
	private int random(int limit) {
		return random.nextInt(limit);
	}
	
	private void constructRandomData(int items) {
		data = new int[items];
		int current;
		
		for (int i = 0; i < items; i++) {
			current = random(1000);
			current *= random.nextInt(2) == 1 ? 1 : -1;
			data[i] = current;
		}
	}
	
	private void assertQueueIsEmpty() {
		assertEquals(queue.size, 0);
		assertNull(queue.front);
		assertNull(queue.back);
	}
}
