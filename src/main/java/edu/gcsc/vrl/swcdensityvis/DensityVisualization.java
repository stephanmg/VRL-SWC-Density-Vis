/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// imports
import edu.gcsc.vrl.densityvis.*;
import edu.gcsc.vrl.swcdensityvis.importer.DensityComputationContext;
import edu.gcsc.vrl.swcdensityvis.importer.DensityComputationStrategyFactoryProducer;
import edu.gcsc.vrl.swcdensityvis.importer.DensityVisualizable;
import edu.gcsc.vrl.swcdensityvis.importer.DensityVisualizableFactory;
import edu.gcsc.vrl.swcdensityvis.importer.XML.XMLDensityUtil;
import edu.gcsc.vrl.swcdensityvis.importer.XML.XMLDensityVisualizer;
import edu.gcsc.vrl.swcdensityvis.types.LA.DenseMatrix;
import edu.gcsc.vrl.swcdensityvis.data.Shape3DArrayCustom;
import edu.gcsc.vrl.swcdensityvis.data.Compartment;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.v3d.VGeometry3D;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.io.FilenameUtils;

/**
 * @brief DensityVisualization component
 * @author stephan <stephan@syntaktischer-zucker.de>
 */
@ComponentInfo(name = "DensityVisualization", category = "Neuro/SWC-Density-Vis")
public class DensityVisualization implements java.io.Serializable {

	/// sVUID
	private static final long serialVersionUID = 1L;

	@OutputInfo(style = "shaped3darraycustomtype", name = " ", typeName = " ")
	public Shape3DArrayCustom visualizeDensity(
		@ParamGroupInfo(group = "Visualization|false|no description")
		@ParamInfo(name = "Density") DensityResult density,
		@ParamGroupInfo(group = "Visualization|false|no description")
		@ParamInfo(name = "Min Density [%]", style = "slider", options = "min=0;max=100;") int percentage,
		@ParamGroupInfo(group = "Visualization")
		@ParamInfo(name = "Density Color 0", style = "color-chooser", options = "value=java.awt.Color.blue") Color dColorZero,
		@ParamGroupInfo(group = "Visualization")
		@ParamInfo(name = "Density Color 1", style = "color-chooser", options = "value=java.awt.Color.red") Color dColorOne,
		@ParamGroupInfo(group = "Visualization")
		@ParamInfo(name = "Density Transparency", style = "default", options = "value=true") boolean dTransparency,
		@ParamGroupInfo(group = "Visualization")
		@ParamInfo(name = "Density Visible?", style = "default", options = "value=true") boolean bVisibleDensity,
		@ParamGroupInfo(group = "Visualization")
		@ParamInfo(name = "Scalebar Visible?", style = "default", options = "value=false") boolean bScalebarVisible,
		@ParamGroupInfo(group = "Visualization")
		@ParamInfo(name = "Coordinate System Visible?", style = "default", options = "value=false") boolean bCoordinateSystemVisible,
		@ParamGroupInfo(group = "Geometry|false|no description")
		@ParamInfo(name = "Line-graph Geometry") File[] files,
		@ParamGroupInfo(group = "Geometry|false|no description")
		@ParamInfo(name = "Representation", style = "selection", options = "value=[\"cylinder\", \"schematic\"]") String representation,
		@ParamGroupInfo(group = "Geometry|false|no description")
		@ParamInfo(name = "Bounding Box Color", style = "color-chooser", options = "value=java.awt.Color.green") Color mColor,
		@ParamGroupInfo(group = "Geometry|false|no description")
		@ParamInfo(name = "Bounding Box Transparency", style = "slider", options = "min=0;max=100;value=80") int mTransparency,
		@ParamGroupInfo(group = "Geometry|false|no description")
		@ParamInfo(name = "Bounding Box Visible?", style = "default", options = "value=true") boolean bVisibleBoundingBox,
		@ParamGroupInfo(group = "Geometry")
		@ParamInfo(name = "Consensus Geometry Visible?", style = "default", options = "value=true") boolean bVisibleGeometry,
		@ParamGroupInfo(group = "Geometry")
		@ParamInfo(name = "Name", typeName = "Compartment name", style = "default", options = "file_tag=\"geometry\"") Compartment compartment,
		@ParamGroupInfo(group = "Output|false|animation and rotational view; Blur|false|Rotation")
		@ParamInfo(name = "Blurring Kernel", typeName = "Blurring Kernel", style = "default", options = "cols=3; rows=3; "
			+ "values=\"0.0625, 0.125, 0.0625, 0.125, 0.250, 0.125, 0.0625, 0.125, 0.0625\"") DenseMatrix blurKernel,
		@ParamGroupInfo(group = "Output|false|animation and rotational view; Animation|false|Animation")
		@ParamInfo(name = "FPS") Integer fps,
		@ParamGroupInfo(group = "Output|false|animation and rotational view; Animation|false|Animation")
		@ParamInfo(name = "SPF") Integer spf,
		@ParamGroupInfo(group = "Output|false|animation and rotational view; Animation|false|Animation")
		@ParamInfo(name = "File type", typeName = "Filetype", style = "selection", options = "value=[\"AVI\","
			+ " \"MPG\", \"MOV\"]") String videoFormat,
		@ParamGroupInfo(group = "Output|false|animation and rotational view; Rotation|false|Rotation")
		@ParamInfo(name = "rotX") double rotX,
		@ParamGroupInfo(group = "Output|false|animation and rotational view; Rotation|false|Rotation")
		@ParamInfo(name = "rotY") double rotY,
		@ParamGroupInfo(group = "Output|false|animation and rotational view; Rotation|false|Rotation")
		@ParamInfo(name = "rotZ") double rotZ,
		@ParamGroupInfo(group = "Output|false|animation and rotational view; Rotation|false|Rotation")
		@ParamInfo(name = "increment") double increment
	) {
		/// the Shape3DArray to visualize
		Shape3DArrayCustom result = new Shape3DArrayCustom();
		result.setFps(fps);
		result.setSpf(spf);
		result.setBlurKernel(blurKernel);
		result.setVideoFormat(videoFormat);
		result.setRotationParams(new double[]{rotX, rotY, rotZ, increment});

		VGeometry3D geom3d = new VGeometry3D(
			density.getGeometry(),
			new Color(mColor.getRed(), mColor.getGreen(), mColor.getBlue(), 255 - (int) ((255.0 / 100) * mTransparency)), null, 1F, false, false, mTransparency > 0);

		int transparencyVal = 254;
		if (dTransparency) {
			transparencyVal = 0;
		}
		

		Color dColorZero_real = new Color(dColorZero.getRed(), dColorZero.getGreen(), dColorZero.getBlue(), transparencyVal);
		Color dColorOne_real = new Color(dColorOne.getRed(), dColorOne.getGreen(), dColorOne.getBlue(), 254);

		/// instantiate the strategy and the visualizer ...
		DensityVisualizableFactory factory = new DensityVisualizableFactory();
		String fileType = FilenameUtils.getExtension(files[0].toString());
		DensityVisualizable visualizer = factory.getDensityVisualizer(fileType);
		DensityComputationContext densityComputationContext = new DensityComputationContext();
		densityComputationContext.setDensityComputationStrategy(new DensityComputationStrategyFactoryProducer().getDefaultAbstractDensityComputationStrategyFactory().getDefaultComputationStrategy(fileType));
		visualizer.setContext(densityComputationContext);

		/// for now manually
		DensityVisualizable xmlDensityVisualizer;
		if (representation.equalsIgnoreCase("CYLINDER")) {
			xmlDensityVisualizer = new XMLDensityVisualizer(XMLDensityUtil.getDefaultDiameterImpl());
		} else {
			xmlDensityVisualizer = new XMLDensityVisualizer(XMLDensityUtil.getDefaultImpl());
		}

		/// exclude compartments and scale, use colors provided by XML file
		xmlDensityVisualizer.setContext(densityComputationContext);
		xmlDensityVisualizer.setFiles(new ArrayList<File>(Arrays.asList(files)));
		xmlDensityVisualizer.prepare(null, 0.01, compartment);

		/**
		 * @todo normalize density per voxel to [0,1] not any number ...
		 * i. e. we need percentage
		 */
		/// add the consenus line-graph geometry (single geometry file)
		if (bVisibleGeometry) {
			/// parse the files
			xmlDensityVisualizer.parseStack();
			/// add line graph geometry 
			result.addAll(xmlDensityVisualizer.calculateGeometry());
		}

		/// @todo set bounding box for result (Shape3DCustom)
		/// this makes easy to set the scale bar and the coordinate axes
		/// in the Shape3DCustomReimplmentationType!
		/// add the density if we want to visualize
		if (bVisibleDensity) {
			result.addAll(VisUtil.scaleDensity2Java3D(
				density.getDensity(), density.getGeometry(), percentage, dColorZero_real, dColorOne_real, true, 0.01));
		}

		/// add the bounding box which includes all line-graph geometries
		if (bVisibleBoundingBox) {
			result.addAll(geom3d.generateShape3DArray());
		}

		/// add scale bar if demanded
		result.setScalebarVisible(bScalebarVisible);

		/// add coordinate system if demanded
		result.setCoordinateSystemVisible(bCoordinateSystemVisible);
		
		/// compute density
		xmlDensityVisualizer.computeDensity();
		
		/// TODO: below can fail if we dont use cylinder strategy
		/// that is, the bounding box is calculated by means of the density.
		/// the XMLDensityVisualizerImpl does not implement the computeDensity 
		/// function (which loads the cells into a member), this member is used 
		/// to calculate the bounding box of the line graph geometry only
		/// good idea maybe to throw directly in computeDensity of xmldensityvisualizerimpl
		/*result.setBoundingBox(
			new Pair<Vector3f, Vector3f>(
				(Vector3f) xmlDensityVisualizer.getCenter(),
				(Vector3f) xmlDensityVisualizer.getDimension())
		);*/

		System.err.println("Center: " + xmlDensityVisualizer.getCenter());
		System.err.println("Dimension: " + xmlDensityVisualizer.getDimension());
		
		/// TODO Can use center and dimension to set bounding box below!!!
		/**
		 * @todo must be implemented correct for the
		 * DensityVisualizerImpl: getBoundingBox wrong! /// set bounding
		 * box to result ///
		 * result.set_bounding_box(xmlDensityVisualizer.getBoundingBox());
		 */
		/*

		/**
		 * @note geometry must also be scaled consistent to the density
		 * description in ComputeDensity - this is done.
		 */
		return result;
	}
}
