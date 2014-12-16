/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// imports
import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.densityvis.DensityResult;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.v3d.VTriangleArray;
import eu.mihosoft.vrl.v3d.jcsg.Cube;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import javax.vecmath.Vector3f;

/**
 * @brief computes the density
 */
@ComponentInfo(name = "ComputeSWCDensity", category = "Neuro/SWC-Density-Vis")
public class ComputeSWCDensity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@MethodInfo(
		valueName = "Density",
		valueTypeName = "Density"
	)
	public DensityResult compute(
		@ParamGroupInfo(group = "Common options|true|Compute the density for the image (stack); Folder|true|Input folder")
		@ParamInfo(name = "Input folder", typeName = "Location of SWC files", style = "load-folder-dialog", options = "endings=[\"swc\"]; description=\"SWC files (.swc)\"") File folder,
		@ParamGroupInfo(group = "Common options|true|Compute the density for the image (stack); Dimensions|true|Dimensions")
		@ParamInfo(name = "Width", typeName = "Width of sampling cube", style = "slider", options = "min=1;max=100") int width,
		@ParamGroupInfo(group = "Common options|true|Compute the density for the image (stack); Dimensions|true|Dimensions")
		@ParamInfo(name = "Height", typeName = "Height of sampling cube", style = "slider", options = "min=1;max=100") int height,
		@ParamGroupInfo(group = "Common options|true|Compute the density for the image (stack); Dimensions|true|Dimensions")
		@ParamInfo(name = "Depth", typeName = "Depth of sampling cube", style = "slider", options = "min=1;max=100") int depth,
		@ParamGroupInfo(group = "Advanced options|true|Compute the density for the image (stack); Compartment|true|Compartment")
		@ParamInfo(name = "Type", typeName = "Compartment", style = "selection", options = "value=[\"all\", \"undefined\", \"axon\", \"(basal) dendrite\", \"apical dendrite\", \"fork point\", \"end point\", \"custom\"]") String choice,
		@ParamGroupInfo(group = "Advanced options|false|Compute the density for the image (stack); Scaling|false|Scaling")
		@ParamInfo(name = "Custom scaling mode", typeName = "Enable custom scaling", options = "value=false", style = "default") boolean bCustomScale,
		@ParamGroupInfo(group = "Advanced options|false|Compute the density for the image (stack); Scaling|false|Scaling")
		@ParamInfo(name = "Factor", typeName = "Custom scaling factor", options = "value=1.0") double custom_scaling_factor
	) {
		HashMap<String, ArrayList<SWCCompartmentInformation>> cells = new HashMap<String, ArrayList<SWCCompartmentInformation>>();
		try {
			File[] swcFiles = folder.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".swc");
				}
			});

			for (File f : swcFiles) {
				eu.mihosoft.vrl.system.VMessage.info("Parsing SWC file", f.toString());
				cells.put(f.getName(), SWCUtility.parse(f));
			}
			eu.mihosoft.vrl.system.VMessage.info("Computing density", "Total number of files for density computation: " + swcFiles.length);

		} catch (IOException e) {
			eu.mihosoft.vrl.system.VMessage.exception("File not found", e.toString());
		}

		/// geometry must fit front and back plane (0.1 and 100 is the limiting interval)
		Vector3f dim = SWCUtility.getDimensions(cells);
		double backplane = 100.0;
		double frontplane = 0.1;
		double scaling_factor = (backplane - frontplane) / (Collections.max(Arrays.asList(dim.x, dim.y, dim.z)));
		/// auto scale mode
		if (!bCustomScale) {
			SWCUtility.scaleAndTransformGeometry(cells, new Vector3f(0.1f, 0.1f, 0.1f), new Vector3f(100.0f - 0.1f, 100.0f - 0.1f, 100.0f - 0.1f));
			System.err.println("scaling_factor: " + scaling_factor);
		} else {
			SWCUtility.scaleAndTransformGeometry(cells, new Vector3f(0.1f, 0.1f, 0.1f), new Vector3f(100.0f - 0.1f, 100.0f - 0.1f, 100.0f - 0.1f), (float) custom_scaling_factor);
			System.err.println("custom_scaling_factor: " + scaling_factor);
		}
		/// least coordinates
		System.err.println("least coordinates: " + SWCUtility.getBoundingBox(cells).getSecond());

		/// density must respect new rescaled geometry and therefore fit in cuboid
		Density density = DensityUtil.computeDensity(cells, width, height, depth, choice);
		Vector3f new_dim = SWCUtility.getDimensions(cells);
		VTriangleArray vta = new Cube(2 * new_dim.x, 2 * new_dim.y, 2 * new_dim.z).toCSG().toVTriangleArray();

		/// return density
		return new DensityResult(density, vta);
	}
}
