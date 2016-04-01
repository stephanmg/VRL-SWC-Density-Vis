/// package's name
package edu.gcsc.vrl.swcdensityvis.test;

/// imports
import edu.gcsc.vrl.swcdensityvis.data.Edge;
import javax.vecmath.Vector3f;
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
public class EdgeTests {

	private final static Edge<Vector3f> EDGE = new Edge<Vector3f>(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));

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
		Edge<Vector3f> e1 = new Edge<Vector3f>(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
		assertTrue("edge could not be constructed", (e1 != null));
	}

	@Test
	public void getFirst() {
		assertEquals("first edges should be equal to vector (0, 0, 0)", EDGE.getFrom(), new Vector3f(0, 0, 0));
	}

	@Test
	public void getSecond() {
		assertEquals("second edge should be equal to vector (1, 1, 1)", EDGE.getTo(), new Vector3f(1, 1, 1));
	}

	@Test
	public void equals() {
		Edge<Vector3f> e1 = new Edge<Vector3f>(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
		Edge<Vector3f> e2 = new Edge<Vector3f>(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
		Edge<Vector3f> e3 = new Edge<Vector3f>(new Vector3f(0, 0, 0), new Vector3f(2, 2, 2));
		assertEquals("e1 and e2 should be equal", e1, e2);
		assertEquals("e1 and e1 should be equal", e1, e1);

	}
}
