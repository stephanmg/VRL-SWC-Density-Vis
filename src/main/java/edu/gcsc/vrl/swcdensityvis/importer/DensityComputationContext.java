/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/// imports
import edu.gcsc.vrl.densityvis.Density;

/**
 * @brief density computation contex
 * @author stephan <stephan@syntaktischer-zucker.de>
 */
public class DensityComputationContext {
	/// strategy
	private DensityComputationStrategy densityComputationStrategy;

	/**
	 * @brief set strategy
	 * @param strategy
	 */
	public DensityComputationContext(DensityComputationStrategy strategy) {
		this.densityComputationStrategy = strategy;
	}

	/**
	 * @brief set default strategy
	 */
	public DensityComputationContext() {
		this.densityComputationStrategy = new DefaultDensityComputation();
	}

	/**
	 * @brief execute density computation
	 * @return Density
	 */
	public Density executeDensityComputation() {
		return this.densityComputationStrategy.computeDensity();
	}
	
	/**
	 * @brief set the density data
	 * @see {@link DensityData}
	 * @param data 
	 */
	public void setDensityData(DensityData data) {
		this.densityComputationStrategy.setDensityData(data);
	}

	/**
	 * @brief set the density computation strategy
	 * @param strategy
	 */
	public void setDensityComputationStrategy(DensityComputationStrategy strategy) {
		this.densityComputationStrategy = strategy;
	}

	/**
	 * @brief get the density computation strategy
	 * @return DensityComputationStrategy
	 */
	public DensityComputationStrategy getDensityComputationStrategy() {
		return this.densityComputationStrategy;
	}
}
