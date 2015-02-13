/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/**
 *
 * @author stephan
 */
public abstract class AbstractDensityComputationStrategyFactory {

	/**
	 * 
	 * @param choice
	 * @return 
	 */
	public abstract TreeDensityComputationStrategy getTreeDensityComputationStrategy(String choice);

	/**
	 * 
	 * @param choice
	 * @return 
	 */
	public abstract EdgeDensityComputationStrategy getEdgeDensityComputationStrategy(String choice);

	/**
	 * 
	 * @param choice
	 * @return 
	 */
	public DensityComputationStrategy getDefaultComputationStrategy(String choice) {
		return getEdgeDensityComputationStrategy(choice);
	}
	

}
