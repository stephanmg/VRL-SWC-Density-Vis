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
import edu.gcsc.vrl.swcdensityvis.importer.XML.TreeDensityComputationStrategyXML;
import edu.gcsc.vrl.swcdensityvis.marching_cubes.MarchingCubes;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.reflection.Pair;
import eu.mihosoft.vrl.v3d.VGeometry3D;
import eu.mihosoft.vrl.v3d.VGeometry3DAppearance;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.media.j3d.Appearance;
import javax.media.j3d.Shape3D;
import javax.vecmath.Vector3f;
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
		
		long startTime = System.currentTimeMillis();
		/// the Shape3DArray to visualize on the Canvas
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

		/// instantiate the strategy and the visualizer 
		DensityVisualizableFactory factory = new DensityVisualizableFactory();
		String fileType = FilenameUtils.getExtension(files[0].toString());
		DensityVisualizable visualizer = factory.getDensityVisualizer(fileType);
		/// Note: EdgeStrategy is slow... shouldn't be the default anymore
		/*
		DensityComputationContext densityComputationContext = new DensityComputationContext();
		densityComputationContext.setDensityComputationStrategy(new DensityComputationStrategyFactoryProducer().
		getDefaultAbstractDensityComputationStrategyFactory().getDefaultComputationStrategy(fileType));
		visualizer.setContext(densityComputationContext);
		*/
		/// TODO: Pass parameters not hardcoded 100, 100, 100 dimensions
		visualizer.setContext(new DensityComputationContext(new TreeDensityComputationStrategyXML(100, 100, 100)));

		/// Note: Could be solved with a Factory
		DensityVisualizable xmlDensityVisualizer;
		if (representation.equalsIgnoreCase("CYLINDER")) {
			xmlDensityVisualizer = new XMLDensityVisualizer(XMLDensityUtil.getDefaultDiameterImpl());
		} else {
			xmlDensityVisualizer = new XMLDensityVisualizer(XMLDensityUtil.getDefaultImpl());
		}

		/// exclude compartments and scale, use colors provided by XML file
		/// xmlDensityVisualizer.setContext(densityComputationContext);
		/// Density parameters have to be passed by argument, not hard-coded 100, 100, 100!
		xmlDensityVisualizer.setContext(new DensityComputationContext(new TreeDensityComputationStrategyXML(100, 100, 100)));
		xmlDensityVisualizer.setFiles(new ArrayList<File>(Arrays.asList(files)));
		xmlDensityVisualizer.prepare(null, 0.01, compartment);

		/**
		 * @todo normalize density per voxel to [0,1] not any number ...
		 * i. e. we need percentage
		 */
		/// parse the files
		xmlDensityVisualizer.parseStack();
		/// add the consenus line-graph geometry (single geometry file)
		if (bVisibleGeometry) {
			/// > 500,000 Elements -> Slow!
			/// add line graph geometry 
			System.err.println("Calculating geometry!");
			result.addAll(xmlDensityVisualizer.calculateGeometry());
		}
		
		/// @TODO calculating the density here should not be necessary to recompute, but we need to do for now because we need the boundingbox from the visualizer, since we don't pass the visualizer
		xmlDensityVisualizer.computeDensity();
		xmlDensityVisualizer.getBoundingBox();
		
		/// Need to get bounding box of cylinder strategy: EdgeDensityComputationStrategy called for schematic 
		/// But this is slow. Need to get the bounding box from the cylinder strategy -> but for cylinder strategy the schematic geometry is slow -> change in teh XMLDensityVisualizerDiamImpl then can use also cylinders again.
		/// And even though we calculate twice -> pass Bounding Box maybe from ComputeDensity?
		
		/// add isosurfaces TODO add switch to allow on/off -> Move this into ComputeDensity
		List<? extends VoxelSet> voxels = density.getDensity().getVoxels();
		///Shape3D isosurface = new MarchingCubes().run_MC_with_threads(voxels, xmlDensityVisualizer);
		Shape3D isosurface;
		isosurface = new MarchingCubes().testMC2(voxels, xmlDensityVisualizer, 0.01f);
		/// TODO Can add a lighting model here: http://www.java3d.org/appearance.html
		// appearance ap = new Appearance();
		/// isosurface.setAppearance(ap);
		result.add(isosurface);

		/// @todo set bounding box for result (Shape3DCustom)
		/// this makes easy to set the scale bar and the coordinate axes
		/// in the Shape3DCustomReimplmentationType!
		/// add the density if we want to visualize
		/// @todo: if we scale, density, contours and geometry have to be scaled ALL, not only a single one of them
		
	 	/// TODO: 0.01 is visual scale for output on Canvas -> pass scaling parameter -> dont scale the density boxes
		if (bVisibleDensity) {
			result.addAll(VisUtil.scaleDensity2Java3D(
				density.getDensity(), density.getGeometry(), percentage, dColorZero_real, dColorOne_real, true, 0.01));
		}

		/// TODO: this 0.01 has to be changed also in ComputeDensity for the VTriangleArray cubes (line 144),
	 	/// otherwise this will give too small cube bounding box on the canvas

		/// add the bounding box which includes all line-graph geometries
		/// TODO: Geometry is unscaled here, we need to scale it too if we scale density above... with parameter...
		if (bVisibleBoundingBox) {
			result.addAll(geom3d.generateShape3DArray());
		}

		/// add scale bar if demanded
		/// result.setScalebarVisible(bScalebarVisible);

		/// add coordinate system if demanded
		/// result.setCoordinateSystemVisible(bCoordinateSystemVisible);
		
		/// compute density
		long startTime2 = System.currentTimeMillis();
		///xmlDensityVisualizer.computeDensity();
		long estimatedTime = System.currentTimeMillis() - startTime2;
		System.err.println("Time has passed: " + estimatedTime);
		
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

		/// TODO EdgeDensityComputationStrategy is SLOW for some reason -> should not be the default anymore
		/// TODO Returns clearly wrong results... -> See TreeDensityStrategy which outputs correct bounding box!
		/// Can use same bounding box for the density as density relies on this...
		/// THIS IS OKAY, we pass only one file... not *all* files, that'S why this is wrong!!!
		System.err.println("Center: " + xmlDensityVisualizer.getCenter());
		System.err.println("Dimension: " + xmlDensityVisualizer.getDimension());
		Pair<Vector3f, Vector3f> bb = (Pair<Vector3f, Vector3f>)xmlDensityVisualizer.getBoundingBox();
		System.err.println("Bounding box: " + bb.getFirst());
		System.err.println("Bounding box: " + bb.getSecond());
		
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
		long estimatedTime2 = System.currentTimeMillis() - startTime;
		System.err.println("Time has passed: " + estimatedTime2);
		System.err.println("Now adding " + result.size() + "number of Shape3D elements. This may take a while!");
		System.err.println("Memory used now: " + (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()));
		return result;
	}
}
