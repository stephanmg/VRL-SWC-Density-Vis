/// package's name
package edu.gcsc.vrl.swcdensityvis.demo;

/// imports
import edu.gcsc.vrl.swcdensityvis.importer.DensityComputationContext;
import edu.gcsc.vrl.swcdensityvis.importer.DensityComputationStrategyFactoryProducer;
import edu.gcsc.vrl.swcdensityvis.importer.DensityVisualizable;
import edu.gcsc.vrl.swcdensityvis.importer.DensityVisualizableFactory;
import edu.gcsc.vrl.swcdensityvis.importer.XML.XMLDensityUtil;
import edu.gcsc.vrl.swcdensityvis.importer.XML.XMLDensityVisualizer;

/**
 *
 * @author stephan
 */
public class DensityVisualizableFactoryStrategyDemo {

	/**
	 *
	 * @param args
	 */
	public static void main(String... args) {
		/// get factory for producing visualizers
		DensityVisualizableFactory factory = new DensityVisualizableFactory();
		/// get actual density visalizer, e.g. swx xml or asc
		DensityVisualizable visualizer = factory.getDefaultDensityVisualizer();

		/// get density computation context
		DensityComputationContext densityComputationContext = new DensityComputationContext();

		/// set the strategy to apply in visualizer, e. g. xml, swc, or asc (here we choose edge density computation for xml file types!)
		densityComputationContext.setDensityComputationStrategy(new DensityComputationStrategyFactoryProducer().getDefaultAbstractDensityComputationStrategyFactory().getEdgeDensityComputationStrategy("XML"));
		visualizer.setContext(densityComputationContext);

		System.err.println("Main...");

		/// another example
		XMLDensityVisualizer xmlDensityVisualizer = new XMLDensityVisualizer(XMLDensityUtil.getDefaultImpl());
		xmlDensityVisualizer.setContext(densityComputationContext);
		///xmlDensityVisualizer.computeDensity();

		/// or maybe that way? (get the default density visualizer)
		DensityVisualizableFactory densityVisualizableFactory = new DensityVisualizableFactory();
		densityVisualizableFactory.getDefaultDensityVisualizer();
	}

}
