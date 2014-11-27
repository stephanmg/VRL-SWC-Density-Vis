/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gcsc.vrl.swcdensityvis.test;

import edu.gcsc.vrl.swcdensityvis.ComputeSWCDensity;
import edu.gcsc.vrl.swcdensityvis.ComputeSWCDistance;
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
public class ComputeSWCDistanceTests {
	
	public ComputeSWCDistanceTests() {
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
	public void compute_density() {
		ComputeSWCDistance d = new ComputeSWCDistance();
		assertTrue("compute swc density can be instantiated", (d != null));
		/**
		 * @todo implement
		 */
	}
}
