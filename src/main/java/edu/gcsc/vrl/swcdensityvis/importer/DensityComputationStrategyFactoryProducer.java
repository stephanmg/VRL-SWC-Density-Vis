/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/**
 *
 * @author stephan
 */
public class DensityComputationStrategyFactoryProducer {

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
