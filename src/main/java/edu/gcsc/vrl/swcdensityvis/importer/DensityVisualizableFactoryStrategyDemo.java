/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

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
