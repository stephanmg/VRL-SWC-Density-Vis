/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/**
 * @brief strategy factory producer
 * @author stephan <stephan@syntaktischer-zucker.de>
 */
public class DensityComputationStrategyFactoryProducer {
	/**
	 * @brief ctor
	 */
	public DensityComputationStrategyFactoryProducer() {
		
	}
	
	/**
	 * @brief 
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
	 * @brief get default abstract density computation strategy factory
	 * @return 
	 */
	public AbstractDensityComputationStrategyFactory getDefaultAbstractDensityComputationStrategyFactory() {
		return new EdgeDensityComputationStrategyFactory();
	}
}
