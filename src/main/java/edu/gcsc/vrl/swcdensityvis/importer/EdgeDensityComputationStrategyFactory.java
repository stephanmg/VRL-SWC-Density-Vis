/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/// imports
import edu.gcsc.vrl.swcdensityvis.importer.XML.EdgeDensityComputationStrategyXML;

/**
 *
 * @author stephan
 */
public class EdgeDensityComputationStrategyFactory extends AbstractDensityComputationStrategyFactory {

	public EdgeDensityComputationStrategyFactory() {
	}

	@Override
	public TreeDensityComputationStrategy getTreeDensityComputationStrategy(String choice) {
		return null;
	}

	@Override
	public EdgeDensityComputationStrategy getEdgeDensityComputationStrategy(String choice) {
		if (choice.equalsIgnoreCase("XML")) {
			return new EdgeDensityComputationStrategyXML();
		} else {
			return new EdgeDensityComputationStrategyXML();
		}
	}

}
