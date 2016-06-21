/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/// imports
import edu.gcsc.vrl.swcdensityvis.importer.XML.EdgeDensityComputationStrategyXML;

/**
 * @brief edge density computation strategy factory
 * @author stephan <stephan@syntaktischer-zucker.de>
 */
public class EdgeDensityComputationStrategyFactory extends AbstractDensityComputationStrategyFactory {
	public EdgeDensityComputationStrategyFactory() {
	}

	/**
	 * @brief get tree density computation strategy
	 * @param choice
	 * @return 
	 */
	@Override
	public TreeDensityComputationStrategy getTreeDensityComputationStrategy(String choice) {
		return null;
	}

	/**
	 * @brief get edge density computation strategy
	 * @param choice
	 * @return 
	 */
	@Override
	public EdgeDensityComputationStrategy getEdgeDensityComputationStrategy(String choice) {
		if (choice.equalsIgnoreCase("XML")) {
			return new EdgeDensityComputationStrategyXML();
		} else {
			return new EdgeDensityComputationStrategyXML();
		}
	}

}
