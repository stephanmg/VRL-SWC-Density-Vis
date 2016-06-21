/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/**
 * @brief strategy factory producer
 * @author stephan <stephan@syntaktischer-zucker.de>
 */
public class DensityComputationStrategyFactoryProducer {
	/**
	 * 
	 */
	public DensityComputationStrategyFactoryProducer() {
		
	}
	
	/**
	 * 
	 * @param choice
	 * @return 
	 */
	public AbstractDensityComputationStrategyFactory getFactory(String choice) {
		if (choice.equalsIgnoreCase("TREE")) {
			return new TreeDensityComputationStrategyFactory();
		} else if (choice.equalsIgnoreCase("EDGE")) {
			return new EdgeDensityComputationStrategyFactory();
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @return 
	 */
	public AbstractDensityComputationStrategyFactory getDefaultAbstractDensityComputationStrategyFactory() {
		return new EdgeDensityComputationStrategyFactory();
	}
}
