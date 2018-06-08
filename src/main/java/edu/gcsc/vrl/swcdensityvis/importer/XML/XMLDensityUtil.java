/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.XML;

/**
 * @brief factory methods to generate bridge objects
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
public class XMLDensityUtil {

	/**
	 * @brief
	 */
	private XMLDensityUtil() {

	}

	/**
	 * @brief get the visualizer implementation
	 * @return
	 */
	public static XMLDensityVisualizerImplementable getImpl() {
		return new XMLDensityVisualizerImpl();
	}

	/**
	 * @brief get the visualizer implementation
	 * @param choice
	 * @return
	 */
	public static XMLDensityVisualizerImplementable getImpl(String choice) {
		if (choice.equalsIgnoreCase("DEFAULT")) {
			return new XMLDensityVisualizerImpl();
		} else {
			return null;
		}
	}
	
	/**
	 * @brief get the default visualizer implementation
	 * @return 
	 */
	public static XMLDensityVisualizerImplementable getDefaultImpl() {
		return new XMLDensityVisualizerImpl();
	}
	
	/**
	 * @brief get the default diameter visualizer implementation
	 * @return 
	 */
	public static XMLDensityVisualizerImplementable getDefaultDiameterImpl() {
		return new XMLDensityVisualizerDiameterImpl();
	}
}
