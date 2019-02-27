/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// imports
import edu.gcsc.vrl.densityvis.*;
import edu.gcsc.vrl.swcdensityvis.importer.DensityComputationContext;
import edu.gcsc.vrl.swcdensityvis.importer.DensityVisualizable;
import edu.gcsc.vrl.swcdensityvis.importer.XML.XMLDensityUtil;
import edu.gcsc.vrl.swcdensityvis.importer.XML.XMLDensityVisualizer;
import edu.gcsc.vrl.swcdensityvis.types.LA.DenseMatrix;
import edu.gcsc.vrl.swcdensityvis.data.Shape3DArrayCustom;
import edu.gcsc.vrl.swcdensityvis.data.Compartment;
import edu.gcsc.vrl.swcdensityvis.importer.ProjectToAxisDensityDecorator;
import edu.gcsc.vrl.swcdensityvis.importer.XML.TreeDensityComputationStrategyXML;
import edu.gcsc.vrl.swcdensityvis.marching_cubes.MarchingCubes;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.math.Trajectory;
import eu.mihosoft.vrl.reflection.Pair;
import eu.mihosoft.vrl.v3d.VGeometry3D;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

/**
 * @brief DensityVisualization component
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
@ComponentInfo(name = "DensityVisualization", category = "Neuro/SWC-Density-Vis")
public class DensityVisualization implements java.io.Serializable {
	private DensityVisualizable xmlDensityVisualizer;
	/// sVUID
	private static final long serialVersionUID = 1L;

	/**
	 * @brief computes the geometry as cylinders/line and scales density and geometry
	 * @param density
	 * @param percentage
	 * @param dColorZero
	 * @param dColorOne
	 * @param dTransparency
	 * @param bVisibleDensity
	 * @param bScalebarVisible
	 * @param bCoordinateSystemVisible
	 * @param files
	 * @param representation
	 * @param mColor
	 * @param mTransparency
	 * @param bVisibleBoundingBox
	 * @param bVisibleGeometry
	 * @param compartment
	 * @param blurKernel
	 * @param fps
	 * @param spf
	 * @param videoFormat
	 * @param rotX
	 * @param rotY
	 * @param rotZ
	 * @param increment
	 * @return 
	 */
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
		
		@ParamGroupInfo(group = "Isosurfaces|false|no description")
		@ParamInfo(name = "Visible?", style="default", options="value=true") 
		boolean bIsoSurfaces,
		@ParamGroupInfo(group = "Isosurfaces")
		@ParamInfo(name = "Average [%]", style = "slider", options = "min=0;max=100;value=50") 
		int mAverage,
		@ParamGroupInfo(group = "Isosurfaces")
		@ParamInfo(name = "Deviation [%]", style = "slider", options = "min=0;max=100;value=1") 
		int mDeviation,

		@ParamGroupInfo(group = "Geometry|false|no description")
		@ParamInfo(name = "Neuron geometry") File[] files,
		@ParamGroupInfo(group = "Geometry|false|no description")
		@ParamInfo(name = "Representation", style = "selection", options = "value=[\"cylinder\", \"schematic\"]") String representation,
		@ParamGroupInfo(group = "Geometry")
		@ParamInfo(name = "Geometry Visible?", style = "default", options = "value=true") boolean bVisibleGeometry,
		
		@ParamGroupInfo(group = "Geometry|false|no description")
		@ParamInfo(name = "Bounding Box Visible?", style = "default", options = "value=true") boolean bVisibleBoundingBox,
		@ParamGroupInfo(group = "Geometry|false|no description")
		@ParamInfo(name = "Bounding Box Color", style = "color-chooser", options = "value=java.awt.Color.green") Color mColor,
		@ParamGroupInfo(group = "Geometry|false|no description")
		@ParamInfo(name = "Bounding Box Transparency", style = "slider", options = "min=0;max=100;value=80") int mTransparency,
		
		@ParamInfo(name = "Compartment Types", typeName = "Compartment Type", style="default", options = "file_tag=\"geometry\"")
		Compartment compartment,
		
		@ParamGroupInfo(group = "Canvas|false|no description")
		@ParamInfo(name = "Scalebar Visible?", style = "default", options = "value=false") boolean bScalebarVisible,
		@ParamGroupInfo(group = "Canvas|false|no description")
		@ParamInfo(name = "Coordinate System Visible?", style = "default", options = "value=false") boolean bCoordinateSystemVisible,
		@ParamGroupInfo(group = "Output|false|animation and rotational view; Blur|false|Rotation")
		
		@ParamInfo(name = "Blurring Kernel", typeName = "Blurring Kernel", style = "default", options = "cols=3; rows=3; "
			+ "values=\"0.0625, 0.125, 0.0625, 0.125, 0.250, 0.125, 0.0625, 0.125, 0.0625\"") DenseMatrix blurKernel,
		@ParamGroupInfo(group = "Output|false|animation and rotational view; Animation|false|Animation")
		@ParamInfo(name = "FPS", style="slider", options = "min=0;max=100;value=30") int fps,
		@ParamGroupInfo(group = "Output|false|animation and rotational view; Animation|false|Animation")
		@ParamInfo(name = "SPF", style="slider", options = "min=0;max=100;value=1") int spf,
		@ParamGroupInfo(group = "Output|false|animation and rotational view; Animation|false|Animation")
		@ParamInfo(name = "File type", typeName = "Filetype", style = "selection", options = "value=[\"AVI\","
			+ " \"MPG\", \"MOV\"]") String videoFormat,
		@ParamGroupInfo(group = "Output|false|animation and rotational view; Rotation|false|Rotation")
		@ParamInfo(name = "rotX", style="slider", options = "min=0;max=255;value=1") double rotX,
		@ParamGroupInfo(group = "Output|false|animation and rotational view; Rotation|false|Rotation")
		@ParamInfo(name = "rotY", style="slider", options = "min=0;max=255;value=1") double rotY,
		@ParamGroupInfo(group = "Output|false|animation and rotational view; Rotation|false|Rotation")
		@ParamInfo(name = "rotZ", style="slider", options = "min=0;max=255;value=1") double rotZ,
		@ParamGroupInfo(group = "Output|false|animation and rotational view; Rotation|false|Rotation")
		@ParamInfo(name = "increment", style="slider", options = "min=0;max=255;value=1") double increment
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

		/// Note: EdgeStrategy is slow... shouldn't be the default anymore
		/// Note: This can be solved with a factory pattern again.
		DensityVisualizable xmlDensityVisualizer;
		if (representation.equalsIgnoreCase("CYLINDER")) {
			xmlDensityVisualizer = new XMLDensityVisualizer(XMLDensityUtil.getDefaultDiameterImpl());
		} else {
			xmlDensityVisualizer = new XMLDensityVisualizer(XMLDensityUtil.getDefaultImpl());
		}

		/// TODO: Pass parameters don't use hardcoded 100, 100, 100 for depth, width, height
		/// TODO: This is actually also not necessary, this is because we need to calculate the geometry
		xmlDensityVisualizer.setContext(new DensityComputationContext(new TreeDensityComputationStrategyXML(100, 100, 100)));
		xmlDensityVisualizer.setFiles(new ArrayList<File>(Arrays.asList(files)));
		xmlDensityVisualizer.prepare(null, 0.01, compartment);

		/**
		 * TODO: Normalization of Density: Check Tree/Edge implementation: 
		 * Need to scale with total length of neurons not with average length!
		 * Note: computeDensity is necessary, since bounding box gets calculated via this too. 
		 * This will have to be changed since this increases runtime by a factor two.
		 */
		xmlDensityVisualizer.parseStack();
		xmlDensityVisualizer.computeDensity();
		this.xmlDensityVisualizer = xmlDensityVisualizer;
		if (bVisibleGeometry) {
			System.err.println("Calculating the geometries as Java3d objects.");
			result.addAll(xmlDensityVisualizer.calculateGeometry());
			/// TODO: This slows down for the Cylinder strategy: 
			/// Need to create one large array as in Schematic strategy
			/// And also, we need to add contours to the schematic strategy
		}

		
		/// TODO: Factor this out into the IsoSurfaceDensityVisualizerDecorator!
		/// xmlDensityVisualizer = new IsosurfaceDensityVisualizerDecorator();
		if (bIsoSurfaces) {
			List<? extends VoxelSet> voxels = density.getDensity().getVoxels();
			Shape3D isosurface;
			isosurface = new MarchingCubes().MC(voxels, xmlDensityVisualizer, 0.01f, percentage);
			Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
			Material mat = new Material(black, black, black, black, 70.0f);
			mat.setCapability(Material.ALLOW_COMPONENT_WRITE);
			Appearance ap = new Appearance();
			ap.setMaterial(mat);
			isosurface.setAppearance(ap);
			result.add(isosurface);
		}

	 	/// TODO: This scales the density as mentioned before in ComputeDensity, make this an option, e.g. 1% = 0.01.
		if (bVisibleDensity) {
			result.addAll(VisUtil.scaleDensity2Java3D(
				density.getDensity(), density.getGeometry(), percentage, dColorZero_real, dColorOne_real, true, 0.01));
		}

		/// add geometry bounding box is demanded
		if (bVisibleBoundingBox) {
			result.addAll(geom3d.generateShape3DArray());
		}

		/// add scale bar if demanded
		if (bScalebarVisible) {
			result.setScalebarVisible(bScalebarVisible);
		}

		/// add coordinate system if demanded
		if (bCoordinateSystemVisible) {
			result.setCoordinateSystemVisible(bCoordinateSystemVisible);
		}
		
		/// measure time
		long startTime2 = System.currentTimeMillis();
		long estimatedTime = System.currentTimeMillis() - startTime2;
		System.err.println("Time has passed: " + estimatedTime);
		
		/// set bounding box for result shape3darray
		@SuppressWarnings("unchecked")
		Pair<Vector3f, Vector3f> bb = (Pair<Vector3f, Vector3f>)xmlDensityVisualizer.getBoundingBox();
		result.setBoundingBox(bb);

		/// Center and dimension of geometries
		System.err.println("Center: " + xmlDensityVisualizer.getCenter());
		System.err.println("Dimension: " + xmlDensityVisualizer.getDimension());
		
		/// Bounding min and max of geometries
		System.err.println("Bounding box: " + bb.getFirst());
		System.err.println("Bounding box: " + bb.getSecond());
		
		/// measure time and memory
		long estimatedTime2 = System.currentTimeMillis() - startTime;
		System.err.println("Time has passed: " + estimatedTime2);
		System.err.println("Now adding " + result.size() + "number of Shape3D elements. This may take a while!");
		System.err.println("Memory used now: " + (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()));
		return result;
	}
	
	/**
	 * @brief get the projected trajectory
	 * @param axis
	 * @return 
	 */
	public Trajectory getTrajectory(
		@ParamInfo(name = "Project to axis") 
		String axis
	) {
		return new ProjectToAxisDensityDecorator(xmlDensityVisualizer).getAxis(axis);
	}
}
