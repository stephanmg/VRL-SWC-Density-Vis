/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gcsc.vrl.swcdensityvis.test;

import edu.gcsc.vrl.swcdensityvis.Edge;
import edu.gcsc.vrl.swcdensityvis.EdgeUtility;
import eu.mihosoft.vrl.reflection.Pair;
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
public class EdgeUtilityTests {
	
	public EdgeUtilityTests() {
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
	public void getBounding() {
		Edge<Vector3f> edge = new Edge<Vector3f>(new Vector3f(0,0,0), new Vector3f(1, 1, 1));
		Pair<Vector3f, Vector3f> box = EdgeUtility.getBounding(edge);
		assertEquals("Min should be equal", box.getFirst(), new Vector3f(0,0,0));
		assertEquals("Max should be equal", box.getSecond(), new Vector3f(1,1,1));
		
		/**
		 * @todo implement
		 */
	
	}
}
