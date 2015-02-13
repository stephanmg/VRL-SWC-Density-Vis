/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

import edu.gcsc.vrl.densityvis.DensityResult;

/**
 *
 * @author stephan
 */
public class DensityComputationContext {

	private DensityComputationStrategy densityComputationStrategy;

	/**
	 *
	 * @param strategy
	 */
	public DensityComputationContext(DensityComputationStrategy strategy) {
		this.densityComputationStrategy = strategy;
	}

	public DensityComputationContext() {
		this.densityComputationStrategy = new DefaultDensityComputation();
	}

	public DensityResult executeDensityComputation() {
		return this.densityComputationStrategy.computeDensity();
	}

	/**
	 *
	 * @param strategy
	 */
	public void setDensityComputationStrategy(DensityComputationStrategy strategy) {
		this.densityComputationStrategy = strategy;
	}

	/**
	 *
	 * @return
	 */
	public DensityComputationStrategy getDensityComputationStrategy() {
		return this.densityComputationStrategy;
	}

}
