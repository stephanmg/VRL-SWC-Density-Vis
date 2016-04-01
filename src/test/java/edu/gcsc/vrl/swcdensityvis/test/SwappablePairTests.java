/// package's name
package edu.gcsc.vrl.swcdensityvis.test;

/// imports
import edu.gcsc.vrl.swcdensityvis.data.SwappablePair;
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
public class SwappablePairTests {

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
	public void construct() {
		SwappablePair<Integer> p = new SwappablePair<Integer>(0, 0);
		assertTrue("Instance could not be created.", (p != null));
	}
}
