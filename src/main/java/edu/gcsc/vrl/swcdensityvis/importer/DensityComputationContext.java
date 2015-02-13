/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/**
 *
 * @author stephan
 */
public class DensityComputationContext {

	private final DensityComputationStrategy densityComputation;
	
	/**
	 * 
	 * @param strategy 
	 */
	public DensityComputationContext(DensityComputationStrategy strategy) {
		this.densityComputation = strategy;
	}

	public DensityComputationContext() {
		this.densityComputation = new DefaultDensityComputation();
	}

	public void executeDensityComputation() {
		this.densityComputation.computeDensity();
	}
}
