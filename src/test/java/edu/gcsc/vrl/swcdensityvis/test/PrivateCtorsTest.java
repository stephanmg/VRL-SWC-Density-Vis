/// package's name
package edu.gcsc.vrl.swcdensityvis.test;

/// imports
import edu.gcsc.vrl.swcdensityvis.util.CuboidUtility;
import edu.gcsc.vrl.swcdensityvis.importer.SWC.DensityUtil;
import edu.gcsc.vrl.swcdensityvis.util.EdgeUtility;
import edu.gcsc.vrl.swcdensityvis.util.SWCUtility;
import edu.gcsc.vrl.swcdensityvis.util.SwappablePairUtility;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
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
public class PrivateCtorsTest {

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
	public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		Constructor<CuboidUtility> constructor = CuboidUtility.class.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		constructor.newInstance();

		Constructor<SWCUtility> constructor2 = SWCUtility.class.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor2.getModifiers()));
		constructor2.setAccessible(true);
		constructor2.newInstance();

		Constructor<SwappablePairUtility> constructor3 = SwappablePairUtility.class.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor3.getModifiers()));
		constructor3.setAccessible(true);
		constructor3.newInstance();

		Constructor<EdgeUtility> constructor4 = EdgeUtility.class.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor4.getModifiers()));
		constructor4.setAccessible(true);
		constructor4.newInstance();

		Constructor<DensityUtil> constructor5 = DensityUtil.class.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor5.getModifiers()));
		constructor5.setAccessible(true);
		constructor5.newInstance();
	}
}
