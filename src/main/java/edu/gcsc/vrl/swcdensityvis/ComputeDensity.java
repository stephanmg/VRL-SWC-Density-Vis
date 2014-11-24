package edu.gcsc.vrl.swcdensityvis;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author stephan
 */

import edu.gcsc.vrl.densityvis.Density;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.v3d.VTriangleArray;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@ComponentInfo(name="ComputeSWCDensity", category="Neuro/SWC-Density-Vis")
public class ComputeDensity implements java.io.Serializable {
  private static final long serialVersionUID=1L;

  // add your code here

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
    @ParamInfo(name="depth", style="default", options="value=10;min=5") int depth
  ) {
	  HashMap<String, ArrayList<SWCCompartmentInformation>> cells = new HashMap<String, ArrayList<SWCCompartmentInformation>>();
	  try {
	  for (File f : folder.listFiles()) {
		  cells.put(f.getName(), SWCUtility.parse(f));
  	} }
	  catch (IOException e) {
		   eu.mihosoft.vrl.system.VMessage.exception("File not found", e.toString());
	  }
	  
  /**
   * @todo this needs to be adjusted
   */
      Density density = DensityUtil.computeDensity(cells, width, height, depth);
      VTriangleArray vta = new VTriangleArray();
      return new DensityResult(density, vta);
  }
}
