/// package's name
package edu.gcsc.vrl.swcdensityvis.test;

/// imports
import edu.gcsc.vrl.swcdensityvis.data.Cuboid;
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
public class CuboidTests {

	private final static Cuboid CUBOID = new Cuboid(0, 0, 0, 1, 1, 1);
	private static final double DELTA = 1e-15;

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
		Cuboid cuboid = new Cuboid(0, 0, 0, 1, 1, 1);
		assertTrue("Cuboid could not be constructed.", (cuboid != null));
	}

	@Test
	public void getX() {
		assertEquals("x-coord should be 0", CUBOID.getX(), 0, DELTA);
	}

	@Test
	public void getY() {
		assertEquals("x-coord should be 0", CUBOID.getY(), 0, DELTA);
	}

	@Test
	public void getZ() {
		assertEquals("x-coord should be 0", CUBOID.getZ(), 0, DELTA);
	}

	@Test
	public void getWidth() {
		assertEquals("width should be 1", CUBOID.getWidth(), 1, DELTA);
	}

	@Test
	public void getHeight() {
		assertEquals("height should be 1", CUBOID.getHeight(), 1, DELTA);
	}

	@Test
	public void getDepth() {
		assertEquals("depth should be 1", CUBOID.getHeight(), 1, DELTA);
	}

	@Test
	public void equals() {
		Cuboid c1 = new Cuboid(0, 0, 0, 1, 1, 1);
		Cuboid c2 = new Cuboid(0, 0, 0, 1, 1, 1);
		assertEquals("cuboid c1 and c2 should be the same", c1, c2);
		assertEquals("cuboid c1 and c2 should be the same", c1, c1);
	}
}
