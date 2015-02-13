/// package's name
package edu.gcsc.vrl.swcdensityvis.demo;

import edu.gcsc.vrl.swcdensityvis.importer.DensityVisualizable;
import edu.gcsc.vrl.swcdensityvis.importer.DensityVisualizableFactory;
import edu.gcsc.vrl.swcdensityvis.importer.TreeDensityComputation;

/**
 *
 * @author stephan
 */
public class DensityVisualizableFactoryStrategyDemo {
	/**
	 * 
	 * @param args 
	 */
	public static void main(String... args) {
		DensityVisualizableFactory factory  = new DensityVisualizableFactory();
		DensityVisualizable SWCDensityVisualizer = factory.getDensityVisualizer("SWC", new TreeDensityComputation());
		SWCDensityVisualizer.computeDensity();
		
		System.err.println("Main...");
	}

}
