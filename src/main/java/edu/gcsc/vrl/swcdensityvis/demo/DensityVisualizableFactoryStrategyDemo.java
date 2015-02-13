/// package's name
package edu.gcsc.vrl.swcdensityvis.demo;

import edu.gcsc.vrl.swcdensityvis.importer.DensityComputationStrategyFactory;
import edu.gcsc.vrl.swcdensityvis.importer.DensityVisualizable;
import edu.gcsc.vrl.swcdensityvis.importer.DensityVisualizableFactory;

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
		DensityVisualizableFactory visualizer  = new DensityVisualizableFactory();
		DensityComputationStrategyFactory computer = new DensityComputationStrategyFactory();
		DensityVisualizable SWCDensityVisualizer = visualizer.getDensityVisualizer("SWC", computer.getDefaultDensityComputation());
		SWCDensityVisualizer.computeDensity();
		
		System.err.println("Main...");
	}

}
