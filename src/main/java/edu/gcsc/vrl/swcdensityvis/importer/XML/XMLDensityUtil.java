/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.XML;

/**
 *
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
	public static XMLDensityVisualizerImpl getImpl() {
		return new XMLDensityVisualizerImpl();
	}

	/**
	 *
	 * @param choice
	 * @return
	 */
	public static XMLDensityVisualizerImpl getImpl(String choice) {
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
	public static XMLDensityVisualizerImpl getDefaultImpl() {
		return new XMLDensityVisualizerImpl();
	}
}
