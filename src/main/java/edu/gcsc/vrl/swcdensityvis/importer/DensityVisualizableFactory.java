/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/// imports
import edu.gcsc.vrl.swcdensityvis.importer.SWC.SWCDensityVisualizer;
import edu.gcsc.vrl.swcdensityvis.importer.asc.ASCDensityVisualizer;
import edu.gcsc.vrl.swcdensityvis.importer.XML.XMLDensityVisualizer;

/**
 * @brief factory for density visualizable classes
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
public final class DensityVisualizableFactory {
	/**
	 * @brief get default density visualizer
	 * @return DensityVisualizabler
	 */
	public DensityVisualizable getDefaultDensityVisualizer() {
		return new DefaultDensityVisualizer();
	}
	
	/**
	 * @brief create a density visualizer
	 * @param visualizerType
	 * @return DensityVisualizable
	 */
	public DensityVisualizable getDensityVisualizer(String visualizerType) {
		return create(visualizerType);
	}
	
	/**
	 * @brief get a density visualizer with a given strategy
	 * @param visualizerType
	 * @param strategy
	 * @return DensityVisualizable
	 */
	public DensityVisualizable getDensityVisualizer(String visualizerType, DensityComputationStrategy strategy) {
		return create(visualizerType, strategy);
	}
	
	/**
	 * @brief get density visualizer with strategy and implementation
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
	 * @brief internal method to create density visualizers
	 * @param visualizerType
	 * @return DensityVisualizable
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
	 * @brief internal method to create density visualizers
	 * @param visualizerType
	 * @param strategy
	 * @return DensityVisualizable
	 */
	private DensityVisualizable create(String visualizerType, DensityComputationStrategy strategy) {
		DensityVisualizable densityVisualizable = create(visualizerType);
		densityVisualizable.setContext(new DensityComputationContext(strategy));
		return densityVisualizable;
	}
}
