/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// imports
import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.densityvis.DensityResult;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.types.SelectionInputType;
import eu.mihosoft.vrl.v3d.VTriangleArray;
import eu.mihosoft.vrl.v3d.jcsg.Cube;
import eu.mihosoft.vrl.visual.VComboBox;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JComboBox;
import javax.vecmath.Vector3f;

/**
 * @brief computes the density
 */
@ComponentInfo(name="ComputeSWCDensity", category="Neuro/SWC-Density-Vis")
public class ComputeSWCDensity implements java.io.Serializable {
  private static final long serialVersionUID=1L;
  @OutputInfo(
    name="Density"
  )
  public DensityResult compute(
    @ParamInfo(
      name="Input folder",
      style="load-dialog",
      options="endings=[\"swc\"]; description=\"SWC files (.swc)\"") File folder,
    @ParamInfo(name="width", style="default", options="value=10;min=5") int width,
    @ParamInfo(name="height", style="default", options="value=10;min=5") int height,
    @ParamInfo(name="depth", style="default", options="value=10;min=5") int depth,
    @ParamInfo(name="selection", style="selection", options="value=[\"all\", \"undefined\", \"axon\", \"(basal) dendrite\", \"apical dendrite\", \"fork point\", \"end point\", \"custom\"") String choice
  ) {
	  HashMap<String, ArrayList<SWCCompartmentInformation>> cells = new HashMap<String, ArrayList<SWCCompartmentInformation>>();
	  try {
		  File[] swcFiles = folder.listFiles(new FilenameFilter()
		{
    		@Override
 		 public boolean accept(File dir, String name) {
        	 return name.endsWith(".swc");
		}});
		  
		  for (File f : swcFiles) {
			eu.mihosoft.vrl.system.VMessage.info("Parsing SWC file", f.toString());
		  	cells.put(f.getName(), SWCUtility.parse(f));
		  }
		  eu.mihosoft.vrl.system.VMessage.info("Computing density", "Total number of files for density computation: " + swcFiles.length);
		  /**
		   * @todo introduce computeDensity() function which resepects the String choice
		   */
		  SWCUtility.computeDensity(cells);
  	} 
	  catch (IOException e) {
		   eu.mihosoft.vrl.system.VMessage.exception("File not found", e.toString());
	  }
	  
      Density density = DensityUtil.computeDensity(cells, width, height, depth);
      Vector3f dim = SWCUtility.getDimensions(cells);
      VTriangleArray vta = new Cube(dim.x,dim.y,dim.z).toCSG().toVTriangleArray();
      return new DensityResult(density, vta);
  }
}
