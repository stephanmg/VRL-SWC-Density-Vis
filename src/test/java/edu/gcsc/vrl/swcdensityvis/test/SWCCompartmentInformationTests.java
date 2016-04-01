/// package's name
package edu.gcsc.vrl.swcdensityvis.test;

/// imports
import edu.gcsc.vrl.swcdensityvis.importer.SWC.SWCCompartmentInformation;
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
public class SWCCompartmentInformationTests {

	private static final double DELTA = 1e-6;

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
	public void thickness() {
		SWCCompartmentInformation s = new SWCCompartmentInformation();
		s.setThickness(1);
		assertEquals("thickness should be 1", s.getThickness(), 1, DELTA);

	}

	@Test
	public void type() {
		SWCCompartmentInformation s = new SWCCompartmentInformation();
		s.setType(1);
		assertEquals("type should be 1", s.getType(), 1, DELTA);
	}

	@Test
	public void remaining_tests() {
		/**
		 * @todo implement
		 */
	}
}
