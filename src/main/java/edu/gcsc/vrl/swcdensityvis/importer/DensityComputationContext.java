/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/// imports
import edu.gcsc.vrl.densityvis.Density;

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

	public Density executeDensityComputation() {
		return this.densityComputationStrategy.computeDensity();
	}
	
	public void setDensityData(DensityData data) {
		this.densityComputationStrategy.setDensityData(data);
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
