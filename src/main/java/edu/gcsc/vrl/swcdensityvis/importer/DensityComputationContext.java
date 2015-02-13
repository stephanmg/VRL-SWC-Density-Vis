/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

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

	public void executeDensityComputation() {
		this.densityComputationStrategy.computeDensity();
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
