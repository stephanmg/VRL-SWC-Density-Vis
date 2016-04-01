package edu.gcsc.vrl.swcdensityvis.test;

import edu.gcsc.vrl.swcdensityvis.importer.SWC.ComputeSWCDensity;
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
public class ComputeSWCDensityTests {

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
		ComputeSWCDensity d = new ComputeSWCDensity();
		assertTrue("compute swc density can be instantiated", (d != null));
		/**
		 * @todo implement
		 */
	}
}
