/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/// imports
import edu.gcsc.vrl.swcdensityvis.importer.SWC.SWCDensityVisualizer;
import edu.gcsc.vrl.swcdensityvis.importer.asc.ASCDensityVisualizer;
import edu.gcsc.vrl.swcdensityvis.importer.XML.XMLDensityVisualizer;

/**
 * @brief factory (can be called from an VRL-Studio Component!)
 * @author stephan
 */
public final class DensityVisualizableFactory {
	
	public DensityVisualizable getDefaultDensityVisualizer() {
		return new DefaultDensityVisualizer();
	}
	/**
	 * 
	 * @param visualizerType
	 * @return 
	 */
	public DensityVisualizable getDensityVisualizer(String visualizerType) {
		return create(visualizerType);
	}
	
	/**
	 * 
	 * @param visualizerType
	 * @param strategy
	 * @return 
	 */
	public DensityVisualizable getDensityVisualizer(String visualizerType, DensityComputationStrategy strategy) {
		return create(visualizerType, strategy);
	}
	
	/**
	 * 
	 * @param visualizerType
	 * @param strategy
	 * @param impl
	 * @return 
	 */
	public DensityVisualizable getDensityVisualizer(String visualizerType, DensityComputationStrategy strategy, DensityVisualizable impl) {
		/**
		 * @todo implement
		 */
		return impl;
	}

	/**
	 * 
	 * @param visualizerType
	 * @return 
	 */
	private DensityVisualizable create(String visualizerType) {
		if (visualizerType.equalsIgnoreCase("SWC")) {
			return new SWCDensityVisualizer();
		} else if (visualizerType.equalsIgnoreCase("ASC")) {
			return new ASCDensityVisualizer();
		} else if (visualizerType.equalsIgnoreCase("XML")) {
			return new XMLDensityVisualizer();
		} else {
			return new DefaultDensityVisualizer();
		}
	}
	
	/**
	 * 
	 * @param visualizerType
	 * @param strategy
	 * @return 
	 */
	private DensityVisualizable create(String visualizerType, DensityComputationStrategy strategy) {
		DensityVisualizable densityVisualizable = create(visualizerType);
		densityVisualizable.setContext(new DensityComputationContext(strategy));
		return densityVisualizable;
	}
}
