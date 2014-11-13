/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gcsc.vrl.swcdensityvis.test;

import edu.gcsc.vrl.swcdensityvis.SWCCompartmentInformation;
import edu.gcsc.vrl.swcdensityvis.SWCUtility;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
	public void testComputeDensity() {
		HashMap<String, ArrayList<SWCCompartmentInformation>> cells = new HashMap<String, ArrayList<SWCCompartmentInformation>>(1);
		try {
			for (int i = 0; i < 5; i ++) {
			 	cells.put("dummy" + i, SWCUtility.parse(new File("data/02a_pyramidal2aFI.swc")));
			}
			ArrayList<Double> res = SWCUtility.computeDensity(cells);
		
	 	} catch (IOException e) {
		 System.err.println("File not found: " + e);
	 	}
		 
	}
	 @Test
	 public void testEdgeSegmentWithinCube() {
		 /**
		  * @todo implement
		  */
	 }
}
