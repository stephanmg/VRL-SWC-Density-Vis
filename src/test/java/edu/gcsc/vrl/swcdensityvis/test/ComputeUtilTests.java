/// package's name
package edu.gcsc.vrl.swcdensityvis.test;

/// imports
import edu.gcsc.vrl.swcdensityvis.util.CompUtility;
import javax.vecmath.Vector3f;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Random;

/**
 *
 * @author stephan
 */
public class ComputeUtilTests {

	private static final Random rand = new Random();
	private static final Float min = 0f;
	private static final Float max = 1e3f;
	private static final int iterations = 10000;
	private static final float EPS = 1e-3f;

	/**
	 * @brief IProjection interface
	 */
	private abstract class IProjection {

		/**
		 * @brief test method for proejction
		 */
		public void test() {
			for (int i = 0; i < iterations; i++) {
				float finalX = rand.nextFloat() * (max - min) + min;
				float finalY = rand.nextFloat() * (max - min) + min;
				float finalZ = rand.nextFloat() * (max - min) + min;
				Vector3f q = new Vector3f(finalX, finalY, finalZ);
				Vector3f qPrime = new Vector3f(project(q));
				check_results(qPrime, q);
			}
		}

		/**
		 * @brief projects a given vector
		 * @param q
		 */
		abstract Vector3f project(Vector3f q);

		/**
		 * @brief checks the projected vector
		 * @param qPrime
		 * @param q
		 */
		abstract void check_results(Vector3f qPrime, Vector3f q);
	}

	/**
	 * @brief projects to XY-plane
	 */
	private class XYProjection extends IProjection {

		@Override
		Vector3f project(Vector3f q) {
			return CompUtility.projectToXYPlane(new Vector3f(q));
		}

		@Override
		void check_results(Vector3f qPrime, Vector3f q) {
			assertEquals(qPrime.x, q.x, EPS);
			assertEquals(qPrime.y, q.y, EPS);
			assertEquals(qPrime.z, 0, EPS);
		}
	}

	/**
	 * @brief projects to XZ-plane
	 */
	private class XZProjection extends IProjection {

		@Override
		Vector3f project(Vector3f q) {
			return CompUtility.projectToXZPlane(new Vector3f(q));
		}

		@Override
		void check_results(Vector3f qPrime, Vector3f q) {
			assertEquals(qPrime.x, q.x, EPS);
			assertEquals(qPrime.z, q.z, EPS);
			assertEquals(qPrime.y, 0, EPS);
		}
	}

	/**
	 * @brief projects to YZ-plane
	 */
	private class YZProjection extends IProjection {

		@Override
		Vector3f project(Vector3f q) {
			return CompUtility.projectToYZPlane(new Vector3f(q));
		}

		@Override
		void check_results(Vector3f qPrime, Vector3f q) {
			assertEquals(qPrime.y, q.y, EPS);
			assertEquals(qPrime.z, q.z, EPS);
			assertEquals(qPrime.x, 0, EPS);
		}
	}

	/**
	 * @brief projects to custom plane by n and p
	 */
	private class CustomProjection extends IProjection {

		private final Vector3f n;
		private final Vector3f p;

		CustomProjection(Vector3f n, Vector3f p) {
			n.normalize();
			this.n = n;
			this.p = p;
			assertEquals("Point p (" + p + ") not contained within the plane defined by the normal n (" + n + ")", n.dot(p), 0, EPS);
		}

		@Override
		Vector3f project(Vector3f q) {
			return CompUtility.projectToPlane(q, new Vector3f(this.n), new Vector3f(this.p));
		}

		@Override
		void check_results(Vector3f qPrime, Vector3f q) {
			assertEquals(qPrime.dot(n), 0, EPS);
		}
	}

	public ComputeUtilTests() {
	}

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
	public void testProjectToXYPlane() {
		new XYProjection().test();
	}

	@Test
	public void testProjectToXZPlane() {
		new XZProjection().test();
	}

	@Test
	public void testProjectToYZPlane() {
		new YZProjection().test();
	}

	@Test
	public void testCustomProjection() {
		new CustomProjection(new Vector3f(0, 0, 2), new Vector3f(0, 0, 0)).test();
	}

}
