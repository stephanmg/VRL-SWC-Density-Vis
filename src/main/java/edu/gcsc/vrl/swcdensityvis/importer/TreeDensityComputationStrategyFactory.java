/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/**
 * @brief tree density computation strategy factory
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 * TODO: Implement the TreeDensityComputationStrategyFactory 
 */
public class TreeDensityComputationStrategyFactory extends AbstractDensityComputationStrategyFactory {
	/**
	 * 
	 */
	public TreeDensityComputationStrategyFactory() {
	}

	/**
	 * @param choice
	 * @return 
	 */
	@Override
	public TreeDensityComputationStrategy getTreeDensityComputationStrategy(String choice) {
		return null;
	}

	/**
	 * @param choice
	 * @return 
	 */
	@Override
	public EdgeDensityComputationStrategy getEdgeDensityComputationStrategy(String choice) {
		return null;
	}
}
