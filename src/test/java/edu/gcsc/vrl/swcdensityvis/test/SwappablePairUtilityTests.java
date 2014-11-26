/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gcsc.vrl.swcdensityvis.test;

import edu.gcsc.vrl.swcdensityvis.SwappablePair;
import edu.gcsc.vrl.swcdensityvis.SwappablePairUtility;
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
public class SwappablePairUtilityTests {
	
	public SwappablePairUtilityTests() {
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
	public void swap() {
		SwappablePair<Integer> p = new SwappablePair<Integer>(1, 0);
		SwappablePairUtility.swap(p);
		assertTrue("first element should now be 0, second eleent should now be 1", (p.getFirst() == 0 && p.getSecond() == 1));
	}

	
}
