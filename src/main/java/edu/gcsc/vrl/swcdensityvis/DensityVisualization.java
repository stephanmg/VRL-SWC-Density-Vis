/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// importsa
import edu.gcsc.vrl.densityvis.*;
import edu.gcsc.vrl.swcdensityvis.importer.DensityComputationContext;
import edu.gcsc.vrl.swcdensityvis.importer.DensityComputationStrategyFactoryProducer;
import edu.gcsc.vrl.swcdensityvis.importer.DensityVisualizable;
import edu.gcsc.vrl.swcdensityvis.importer.DensityVisualizableFactory;
import edu.gcsc.vrl.swcdensityvis.importer.XML.XMLDensityUtil;
import edu.gcsc.vrl.swcdensityvis.importer.XML.XMLDensityVisualizer;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.v3d.Shape3DArray;
import eu.mihosoft.vrl.v3d.VGeometry3D;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author stephan
 */
@ComponentInfo(name = "DensityVisualization", category = "Neuro/SWC-Density-Vis")
public class DensityVisualization implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@OutputInfo(style = "shaped3darraycustom", name = " ", typeName = " ")
	public Shape3DArray visualizeDensity(
		@ParamGroupInfo(group = "Visualization|false|no description")
		@ParamInfo(name = "Density") DensityResult density,
		@ParamGroupInfo(group = "Visualization|false|no description")
		@ParamInfo(name = "Min Density", style = "slider", options = "min=1;max=100;") int percentage,
		@ParamGroupInfo(group = "Visualization")
		@ParamInfo(name = "Density Color 0", style = "color-chooser", options = "value=java.awt.Color.blue") Color dColorZero,
		@ParamGroupInfo(group = "Visualization")
		@ParamInfo(name = "Density Color 1", style = "color-chooser", options = "value=java.awt.Color.red") Color dColorOne,
		@ParamGroupInfo(group = "Visualization")
		@ParamInfo(name = "Density Transparency", style = "default", options = "value=true") boolean dTransparency,

		@ParamGroupInfo(group = "Visualization")
		@ParamInfo(name = "Density Visible?", style="default", options="value=true") boolean bVisibleDensity,

		@ParamGroupInfo(group = "Geometry|false|no description")
		@ParamInfo(name = "Line-graph Geometry") File[] swcFiles,
		@ParamGroupInfo(group = "Geometry|false|no description")
		@ParamInfo(name = "Representation", style="selection", options="value=[\"cylinder\", \"schematic\"]") String representation,
		@ParamGroupInfo(group = "Geometry|false|no description")
		@ParamInfo(name = "Line-graph Geometry Color", style = "color-chooser", options = "value=java.awt.Color.magenta") Color gColor,
		@ParamGroupInfo(group = "Geometry|false|no description")
		@ParamInfo(name = "Line-graph Geometry Transparency", style = "slider", options = "min=0;max=100;") int gTransparency,
		@ParamGroupInfo(group = "Geometry|false|no description")
		@ParamInfo(name = "Bounding Box Color", style = "color-chooser", options = "value=java.awt.Color.green") Color mColor,
		@ParamGroupInfo(group = "Geometry|false|no description")
		@ParamInfo(name = "Bounding Box Transparency", style = "slider", options = "min=0;max=100;value=80") int mTransparency,

		@ParamGroupInfo(group = "Geometry")
		@ParamInfo(name = "Consensus Geometry Visible?", style="default", options="value=true") boolean bVisibleGeometry
	) {

		/// the Shape3DArray to visualize
		Shape3DArray result = new Shape3DArray();

		 VGeometry3D geom3d = new VGeometry3D(
		 density.getGeometry(),
		 new Color( mColor.getRed(), mColor.getGreen(), mColor.getBlue(), 255 - mTransparency),
		 null,1F,false, false, mTransparency > 0);
		 
		int transparencyVal = 254;

		if (dTransparency) {
			transparencyVal = 0;
		}

		Color dColorZero_real = new Color(dColorZero.getRed(), dColorZero.getGreen(), dColorZero.getBlue(), transparencyVal);
		Color dColorOne_real = new Color(dColorOne.getRed(), dColorOne.getGreen(), dColorOne.getBlue(), 254);
		Color gColor_real = new Color(gColor.getRed(), gColor.getGreen(), gColor.getBlue());

		DensityVisualizableFactory factory = new DensityVisualizableFactory();
		/**
		 * @todo factory needs to be enhanced by the XMLDensityUtil
		 * maybe (i. e. DensityVisulziable)
		 */
		final String selection = "XML";
		DensityVisualizable visualizer = factory.getDensityVisualizer(selection);
		DensityComputationContext densityComputationContext = new DensityComputationContext();
		densityComputationContext.setDensityComputationStrategy(new DensityComputationStrategyFactoryProducer().getDefaultAbstractDensityComputationStrategyFactory().getDefaultComputationStrategy(selection));
		visualizer.setContext(densityComputationContext);

		XMLDensityVisualizer xmlDensityVisualizer;
		
		if (representation.equalsIgnoreCase("CYLINDER")) {
			xmlDensityVisualizer = new XMLDensityVisualizer(XMLDensityUtil.getDefaultDiameterImpl());
		} else {
			xmlDensityVisualizer = new XMLDensityVisualizer(XMLDensityUtil.getDefaultImpl());
		}
		
		xmlDensityVisualizer.setContext(densityComputationContext);
		/**
		 * @todo setFiles could also be moved in the interface
		 */
		xmlDensityVisualizer.setFiles(new ArrayList<File>(Arrays.asList(swcFiles)));
		xmlDensityVisualizer.prepare(gColor_real, 0.01);

		/**
		 * @todo if we calculate geometry in the ComputeDensity it will
		 * get cached somehow! (this is by our implementation as this is
		 * expensive, but then leads here to multiple parent branch in
		 * java3d!)
		 */
		
		/// add the geometry if we want to visualize
		if (bVisibleGeometry) {
			/// parse the files
			xmlDensityVisualizer.parseStack();
			/// add line graph geometry 
			result.addAll(xmlDensityVisualizer.calculateGeometry());
		}

		/// add the density if we want to visualize
		if (bVisibleDensity) {
    			result.addAll(VisUtil.scaleDensity2Java3D(
			density.getDensity(), density.getGeometry(), percentage, dColorZero_real, dColorOne_real, true, 0.01));
			result.addAll( geom3d.generateShape3DArray() );
		}
		
		/**
		 * @todo geometry must also be scaled!!! (consistent to the density description!)
		 */
		return result;
	}
}
