package edu.gcsc.vrl.swcdensityvis.test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import edu.gcsc.vrl.swcdensityvis.data.Cuboid;
import edu.gcsc.vrl.swcdensityvis.util.CuboidUtility;
import eu.mihosoft.vrl.reflection.Pair;
import java.util.Arrays;
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
		Pair<int[], int[]> res = CuboidUtility.getSampleCuboidBoundingIndices(new Cuboid(0f, 0f, 0f, 10f, 10f, 10f), new Cuboid(2f, 2f, 2f, 0f, 0f, 0f), new Cuboid(5f, 5f, 5f, 0.2f, 0.2f, 0.2f), 0.2f, 0.2f, 0.2f);
		System.err.println("res: " + Arrays.toString(res.getFirst()) + "; " + Arrays.toString(res.getSecond()));
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
	//	CuboidUtility.getCuboidId(new Cuboid(0f, 0f, 0f, 1.0f, 1.0f, 1.0f), new Cuboid(0f, 0f, 0f, 0.25f, 0.25f, 0.25f), 0.1f, 0.1f, 0.1f);
		/**
		 * @todo implement
		 */
	}
}
