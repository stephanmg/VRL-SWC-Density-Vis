/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gcsc.vrl.swcdensityvis.test;

import edu.gcsc.vrl.swcdensityvis.*;
import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeySizeException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.vecmath.Vector3d;
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
	
	public KDTreeTests() {
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
	 /**
	  * @todo define members for the tree etc., other test methods need to access them
	  */
	 public void testBuildKDTree() {
		ArrayList<SWCCompartmentInformation> info = new ArrayList<SWCCompartmentInformation>();
		 try {
		 	info = SWCUtility.parse(new File("/Users/stephan/Code/git/VRL-SWC-Density-Vis/data/02a_pyramidal2aFI.swc"));
		
	 	} catch (IOException e) {
		 System.err.println("File not found: " + e);
	 	}
		 
		KDTree<ArrayList<Vector3d>> tree = SWCUtility.buildKDTree(SWCUtility.getIndicents(info));
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
			tree.range(lo, hi);
		}catch (KeySizeException e) {
			fail("No key could be found for search query: " + e);
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
