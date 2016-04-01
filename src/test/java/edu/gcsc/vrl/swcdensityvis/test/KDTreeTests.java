/// package's name
package edu.gcsc.vrl.swcdensityvis.test;

/// imports
import edu.gcsc.vrl.swcdensityvis.importer.SWC.SWCCompartmentInformation;
import edu.gcsc.vrl.swcdensityvis.util.SWCUtility;
import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeySizeException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.vecmath.Vector3f;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author stephan
 */
public class KDTreeTests {
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
	 /**
	  * @todo define members for the tree etc., other test methods need to access them
	  */
	 public void testBuildKDTree() {
		HashMap<String, ArrayList<SWCCompartmentInformation>> cells = new HashMap<String, ArrayList<SWCCompartmentInformation>>(1);
		try {
		 	cells.put("dummy", SWCUtility.parse(new File("data/02a_pyramidal2aFI_original.swc")));
		
	 	} catch (IOException e) {
		 System.err.println("File not found: " + e);
	 	}
		 
		for (Map.Entry<String, ArrayList<SWCCompartmentInformation>> cell : cells.entrySet()) {
		HashMap<Vector3f, ArrayList<Vector3f>> incidents = SWCUtility.getIndicents(cell.getValue());
		KDTree<ArrayList<Vector3f>> tree = SWCUtility.buildKDTree(incidents);
		assertEquals("Tree size is required to be: 1514, but was: " + tree.size(), tree.size(), 1514);
		double[] elem = {2.14, 14.34, -0.15};
		try {
			tree.search(elem);
		}catch (KeySizeException e) {
			fail("No key could be found for search query: " + e);
		}
		double[] lo = {0, 0,0 };
		double[] hi = {5, 20, 5};
		try {
			List<ArrayList<Vector3f>> temps = tree.range(lo, hi);
		}catch (KeySizeException e) {
			fail("No key could be found for search query: " + e);
		}
		}
	 }
	 
	@Test
	/**
	 * @todo implement
	 */
	public void testKDTreeSearch() {
		 
	}
	@Test
	/**
	 * @todo implement
	 */
	public void testKDTreeRange() {
		
	}
}
