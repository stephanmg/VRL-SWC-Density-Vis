/// package's name
package edu.gcsc.vrl.swcdensityvis.test;

/// imports
import edu.gcsc.vrl.swcdensityvis.importer.SWC.ComputeSWCDistance;
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
public class ComputeSWCDistanceTests {
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
	public void compute_density() {
		ComputeSWCDistance d = new ComputeSWCDistance();
		assertTrue("compute swc density can be instantiated", (d != null));
		/**
		 * @todo implement
		 */
	}
}
