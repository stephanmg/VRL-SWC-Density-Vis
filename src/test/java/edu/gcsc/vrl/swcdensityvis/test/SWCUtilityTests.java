/// package's name
package edu.gcsc.vrl.swcdensityvis.test;

/// imports
import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.swcdensityvis.DensityUtil;
import edu.gcsc.vrl.swcdensityvis.SWCCompartmentInformation;
import edu.gcsc.vrl.swcdensityvis.SWCUtility;
import eu.mihosoft.vrl.reflection.Pair;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.vecmath.Vector3f;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
/** @todo make use of easymock to mock things away in test cases next */
import static org.easymock.EasyMock.createNiceMock;

/**
 *
 * @author stephan
 */
public class SWCUtilityTests {
	
	public SWCUtilityTests() {
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
	public void testRayBoxIntersectionTestX() {
		Vector3f p1 = new Vector3f(0.5f, 0.5f, 0.5f);
		Vector3f p2 = new Vector3f(1.5f, 0.5f, 0.5f);
		Vector3f dir = new Vector3f(p2);
		dir.sub(p1);
		
		Vector3f boxmin = new Vector3f(0, 0, 0);
		Vector3f boxmax = new Vector3f(1, 1, 1);
		Pair<Boolean, Pair<Float, Float>> res = SWCUtility.RayBoxIntersection(p1, dir, boxmin, boxmax);
		assertTrue(res.getFirst());
		
		Vector3f x1 = new Vector3f(p1);
		Vector3f scaled1 = new Vector3f(dir);
		scaled1.scale(res.getSecond().getFirst());

		x1.add(scaled1);

		Vector3f segment = new Vector3f(x1);				
		segment.sub(p1);
		assertTrue(segment.length() == 0.5);
	}
	@Test
	public void testRayBoxIntersectionTestY() {
		Vector3f p1 = new Vector3f(0.5f, 0.5f, 0.5f);
		Vector3f p2 = new Vector3f(0.5f, 1.5f, 0.5f);
		Vector3f dir = new Vector3f(p2);
		dir.sub(p1);
		
		Vector3f boxmin = new Vector3f(0, 0, 0);
		Vector3f boxmax = new Vector3f(1, 1, 1);
		Pair<Boolean, Pair<Float, Float>> res = SWCUtility.RayBoxIntersection(p1, dir, boxmin, boxmax);
		assertTrue(res.getFirst());
		
		Vector3f x1 = new Vector3f(p1);
		Vector3f scaled1 = new Vector3f(dir);
		scaled1.scale(res.getSecond().getFirst());

		x1.add(scaled1);

		Vector3f segment = new Vector3f(x1);				
		segment.sub(p1);
		assertTrue(segment.length() == 0.5);
	}
	
	@Test
	public void testRayBoxIntersectionTestZ() {
		Vector3f p1 = new Vector3f(0.5f, 0.5f, 0.5f);
		Vector3f p2 = new Vector3f(0.5f, 0.5f, 1.5f);
		Vector3f dir = new Vector3f(p2);
		dir.sub(p1);
		
		Vector3f boxmin = new Vector3f(0, 0, 0);
		Vector3f boxmax = new Vector3f(1, 1, 1);
		Pair<Boolean, Pair<Float, Float>> res = SWCUtility.RayBoxIntersection(p1, dir, boxmin, boxmax);
		assertTrue(res.getFirst());
		
		Vector3f x1 = new Vector3f(p1);
		Vector3f scaled1 = new Vector3f(dir);
		scaled1.scale(res.getSecond().getFirst());

		x1.add(scaled1);

		Vector3f segment = new Vector3f(x1);				
		segment.sub(p1);
		assertTrue(segment.length() == 0.5);
	}
	
	
	@Test
	public void testComputeDensity() {
		HashMap<String, ArrayList<SWCCompartmentInformation>> cells = new HashMap<String, ArrayList<SWCCompartmentInformation>>(1);
		try {
			for (int i = 0; i < 1; i ++) {
			 	cells.put("dummy" + i, SWCUtility.parse(new File("data/02a_pyramidal2aFI.swc")));
			}
			HashMap<Integer, Float> res = SWCUtility.computeDensity(cells);
		
	 	} catch (IOException e) {
		 System.err.println("File not found: " + e);
	 	}
		 
	}

	@Test
	public void testComputeDensityAlternative() {
		HashMap<String, ArrayList<SWCCompartmentInformation>> cells = new HashMap<String, ArrayList<SWCCompartmentInformation>>(1);
		try {
			for (int i = 0; i < 1; i ++) {
			 	cells.put("dummy" + i, SWCUtility.parse(new File("data/02a_pyramidal2aFI.swc")));
			}
			HashMap<Integer, Float> res = SWCUtility.computeDensityAlternative(cells);
		
	 	} catch (IOException e) {
		 System.err.println("File not found: " + e);
	 	}
		 
	}

	/*@Test
	public void testDensityUtil() {
		HashMap<String, ArrayList<SWCCompartmentInformation>> cells = new HashMap<String, ArrayList<SWCCompartmentInformation>>(1);
		try {
			for (int i = 0; i < 1; i ++) {
			 	cells.put("dummy" + i, SWCUtility.parse(new File("data/02a_pyramidal2aFI.swc")));
			}
			HashMap<Integer, Float> res = SWCUtility.computeDensity(cells);
		
	 	} catch (IOException e) {
		 System.err.println("File not found: " + e);
	 	}
		int width = 10;
		int height = 10;
		int depth = 10;
      	Density density = DensityUtil.computeDensity(cells, width, height, depth);
	}*/

	
	 @Test
	 public void testEdgeSegmentWithinCube() {
		 /**
		  * @todo implement
		  */
	 }
}
