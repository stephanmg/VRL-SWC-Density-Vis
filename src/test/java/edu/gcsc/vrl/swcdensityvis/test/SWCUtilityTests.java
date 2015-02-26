/// package's name
package edu.gcsc.vrl.swcdensityvis.test;

/// imports
import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.swcdensityvis.importer.SWC.DensityUtil;
import edu.gcsc.vrl.swcdensityvis.importer.SWC.SWCCompartmentInformation;
import edu.gcsc.vrl.swcdensityvis.util.SWCUtility;
import eu.mihosoft.vrl.reflection.Pair;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.vecmath.Vector3f;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
/** @todo make use of easymock to mock things away in test cases next or mockito */
import static org.easymock.EasyMock.createNiceMock;

/**
 *
 * @author stephan
 */
public class SWCUtilityTests {
	private static final double DELTA = 1e-6;
	
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
	public void testRayBoxIntersectionWithNoDirection() {
		Vector3f p1 = new Vector3f(0.5f, 0.5f, 0.5f);
		Vector3f p2 = new Vector3f(0.5f, 0.5f, 0.5f);
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
		assertTrue(segment.length() == 0.0);
	}
	
	
	@Test
	public void testComputeDensity() {
		HashMap<String, ArrayList<SWCCompartmentInformation>> cells = new HashMap<String, ArrayList<SWCCompartmentInformation>>(1);
		try {
			for (int i = 0; i < 1; i ++) {
			 	cells.put("dummy" + i, SWCUtility.parse(new File("data/02a_pyramidal2aFI.swc")));
			}
		
	 	} catch (IOException e) {
		 System.err.println("File not found: " + e);
	 	}
		
		HashMap<Integer, Float> res = SWCUtility.computeDensity(cells, "AXON");
		assertTrue("Resulting HashMap should not be zero.", !res.isEmpty());
		res = SWCUtility.computeDensity(cells, SWCUtility.DEFAULT_WIDTH, SWCUtility.DEFAULT_HEIGHT, 
			                               SWCUtility.DEFAULT_DEPTH, SWCUtility.DEFAULT_SELECTION);
		assertTrue("Resulting HashMap should not be zero.", !res.isEmpty());
	}

	@Test
	public void testComputeDensityAlternative() {
		HashMap<String, ArrayList<SWCCompartmentInformation>> cells = new HashMap<String, ArrayList<SWCCompartmentInformation>>(1);
		try {
			for (int i = 0; i < 1; i ++) {
			 	cells.put("dummy" + i, SWCUtility.parse(new File("data/02a_pyramidal2aFI.swc")));
			}
		
	 	} catch (IOException e) {
		 System.err.println("File not found: " + e);
	 	}
		HashMap<Integer, Float> res = SWCUtility.computeDensityAlternative(cells);
		//assertTrue("Result should not be empty", !res.isEmpty());
		 
	}

	@Test
	public void testDensityUtil() {
		HashMap<String, ArrayList<SWCCompartmentInformation>> cells = new HashMap<String, ArrayList<SWCCompartmentInformation>>(1);
		try {
			for (int i = 0; i < 1; i ++) {
			 	cells.put("dummy" + i, SWCUtility.parse(new File("data/02a_pyramidal2aFI.swc")));
			}
		
	 	} catch (IOException e) {
		 System.err.println("File not found: " + e);
	 	}
		int width = 10;
		int height = 10;
		int depth = 10;
      	Density density = DensityUtil.computeDensity(cells, width, height, depth, "ALL");
	assertTrue("Voxels in density should not be zero: ", !density.getVoxels().isEmpty());
	}

	
	 @Test
	 public void testEdgeSegmentWithinCube() {
		 float x = 1;
		 float y = 1;
		 float z = 1;
		 float width = 11;
		 float depth = 11;
		 float height = 11;
		 
		 float length = SWCUtility.EdgeSegmentWithinCuboid(x, y, z, width, depth, height, new Vector3f(1, 1, 1), new ArrayList<Vector3f>(Arrays.asList(new Vector3f(11, 11, 11))));
		 
		 assertEquals("Length should be " + Math.sqrt(3*Math.pow(10, 2)), Math.sqrt(3*Math.pow(10, 2)), length, DELTA);

		 /**
		  * @todo implement other cases too
		  */
	 }
	 
	 @Test
	 public void parseStack() {
		try {
			SWCUtility.parseStack(new File("data/"));
		
	 	} catch (IOException e) {
		 System.err.println("File not found: " + e);
		 fail("File not found: " + e);
	 	}
	 }
	 
	 @Test
	 public void BoxProbe() {
		 Vector3f rayFrom = new Vector3f(0, 0, 0);
		 Vector3f boxMin = new Vector3f(-1, -1, -1); 
		 Vector3f boxMax = new Vector3f(1, 1, 1); 
		 boolean val = SWCUtility.BoxProbe(rayFrom, boxMin, boxMax);
		 assertTrue("Point should be within the box", val);
		 
		 rayFrom = new Vector3f(-2, 0, 0);
		 val = SWCUtility.BoxProbe(rayFrom, boxMin, boxMax);
		 assertTrue("Point should be outside the box", !val);
		 
		 
		 rayFrom = new Vector3f(0, -2, 0);
		 val = SWCUtility.BoxProbe(rayFrom, boxMin, boxMax);
		 assertTrue("Point should be outside the box", !val);
		 
		 
		 rayFrom = new Vector3f(0, 0, -2);
		 val = SWCUtility.BoxProbe(rayFrom, boxMin, boxMax);
		 assertTrue("Point should be outside the box", !val);
	 }
	 
	 @Test
	 public void getBoundingBox() {
		 HashMap<String, ArrayList<SWCCompartmentInformation>> cells = new HashMap<String, ArrayList<SWCCompartmentInformation>>(1);
		try {
			for (int i = 0; i < 1; i ++) {
			 	cells.put("dummy" + i, SWCUtility.parse(new File("data/02a_pyramidal2aFI.swc")));
			}
		
	 	} catch (IOException e) {
		 System.err.println("File not found: " + e);
	 	}
		
		 for (Map.Entry<String, ArrayList<SWCCompartmentInformation>> entry : cells.entrySet()) {
			 Pair<Vector3f, Vector3f> bounding = SWCUtility.getBoundingBox(entry);
			 assertEquals("Bounding min should agree.", bounding.getSecond(), new Vector3f(-75.14f, -22.01f, -14.62f));
			 assertEquals("Bounding max should agree.", bounding.getFirst(), new Vector3f(2.14f, 26.74f, 19.47f));
		 }
	 }
	 
	 @Test
	 public void getDimensions() {
		 HashMap<String, ArrayList<SWCCompartmentInformation>> cells = new HashMap<String, ArrayList<SWCCompartmentInformation>>(1);
		try {
			for (int i = 0; i < 1; i ++) {
			 	cells.put("dummy" + i, SWCUtility.parse(new File("data/02a_pyramidal2aFI.swc")));
			}
		
	 	} catch (IOException e) {
		 System.err.println("File not found: " + e);
	 	}
		
		 for (Map.Entry<String, ArrayList<SWCCompartmentInformation>> entry : cells.entrySet()) {
			Vector3f dim = SWCUtility.getDimensions(entry);
		 	assertEquals("Dimensions don't agree.", dim, new Vector3f(77.28f, 48.75f, 34.09f));
		 }	 
	 /**
	  * @todo check dims
	  */
	 }
	}
