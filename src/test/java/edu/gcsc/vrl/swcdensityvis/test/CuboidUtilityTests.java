package edu.gcsc.vrl.swcdensityvis.test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import edu.gcsc.vrl.swcdensityvis.Cuboid;
import edu.gcsc.vrl.swcdensityvis.CuboidUtility;
import eu.mihosoft.vrl.reflection.Pair;
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
public class CuboidUtilityTests {
	
	public CuboidUtilityTests() {
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
	public void getSampleCuboidBounding() {
		Pair<int[], int[]> res = CuboidUtility.getSampleCuboidBounding(new Cuboid(0f, 0f, 0f, 1.0f, 1.0f, 1.0f), new Cuboid(0f, 0f, 0f, 0.25f, 0.25f, 0.25f), new Cuboid(0.25f, 0.25f, 0.25f, 0.25f, 0.25f, 0.25f), 0.25f, 0.25f, 0.25f);
		/**
		 * @todo implement
		 */
	}
	
	@Test
	public void getCuboidById() {
		CuboidUtility.getCuboidbyId(new Cuboid(0f, 0f, 0f, 1f, 1f, 1f), new int[] {1, 2, 3}, 0.1f, 0.1f, 0.1f);
		/**
		 * @todo implement
		 */
	}
	
	@Test
	public void getCuboidId() {
		CuboidUtility.getCuboidId(new Cuboid(0f, 0f, 0f, 1.0f, 1.0f, 1.0f), new Cuboid(0f, 0f, 0f, 0.25f, 0.25f, 0.25f), 0.1f, 0.1f, 0.1f);
		/**
		 * @todo implement
		 */
	}
}
