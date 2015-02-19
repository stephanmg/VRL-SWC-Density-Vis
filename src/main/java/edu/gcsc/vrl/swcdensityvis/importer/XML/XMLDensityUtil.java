/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.XML;

/**
 * @brief some factory methods to create instances of the bridge component
 * @author stephan
 */
public class XMLDensityUtil {

	/**
	 *
	 */
	private XMLDensityUtil() {

	}

	/**
	 *
	 * @return
	 */
	public static XMLDensityVisualizerImplementable getImpl() {
		return new XMLDensityVisualizerImpl();
	}

	/**
	 *
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
	 * 
	 * @return 
	 */
	public static XMLDensityVisualizerImplementable getDefaultImpl() {
		return new XMLDensityVisualizerImpl();
	}
	
	/**
	 * 
	 * @return 
	 */
	public static XMLDensityVisualizerImplementable getDefaultDiameterImpl() {
		return new XMLDensityVisualizerDiameterImpl();
	}
}
