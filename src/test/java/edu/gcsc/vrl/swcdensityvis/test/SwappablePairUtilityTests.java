/// package's name
package edu.gcsc.vrl.swcdensityvis.test;

/// imports
import edu.gcsc.vrl.swcdensityvis.data.SwappablePair;
import edu.gcsc.vrl.swcdensityvis.util.SwappablePairUtility;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author stephan
 */
public class SwappablePairUtilityTests {

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void swap() {
		SwappablePair<Integer> p = new SwappablePair<Integer>(1, 0);
		SwappablePairUtility.swap(p);
		assertTrue("first element should now be 0, second eleent should now be 1", (p.getFirst() == 0 && p.getSecond() == 1));
	}
}
