/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/**
 * @brief factory interface for creating computation strategies
 * @author stephan <stephan@syntaktischer-zucker.de>
 */
public abstract class AbstractDensityComputationStrategyFactory {
	/**
	 * @brief get the tree density strategy for computations
	 * @param choice
	 * @return 
	 */
	public abstract TreeDensityComputationStrategy getTreeDensityComputationStrategy(String choice);

	/**
	 * @brief get the edge density strategy for computations
	 * @param choice
	 * @return 
	 */
	public abstract EdgeDensityComputationStrategy getEdgeDensityComputationStrategy(String choice);

	/**
	 * @brief returns the default computation strategy for density calculation
	 * @param choice
	 * @return 
	 */
	public DensityComputationStrategy getDefaultComputationStrategy(String choice) {
		return getEdgeDensityComputationStrategy(choice);
	}
}
