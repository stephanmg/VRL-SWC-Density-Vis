package edu.gcsc.vrl.swcdensityvis;

import edu.gcsc.vrl.densityvis.VisUtil;
import edu.gcsc.vrl.densityvis.*;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.v3d.Shape3DArray;
import eu.mihosoft.vrl.v3d.VGeometry3D;
import java.awt.Color;

/**
 *
 * @author stephan
 */
@ComponentInfo(name="SWCDensityVisualization", category="Neuro/SWC-Density-Vis")
public class SWCDensityVisualization implements java.io.Serializable {
  private static final long serialVersionUID=1L;

  @OutputInfo(style="vcanvas3d")
  @MethodInfo(
		valueName = " ",
		valueTypeName = " "
	)
  public Shape3DArray visualizeDensity(

    @ParamInfo(name="Density") DensityResult density,
    @ParamGroupInfo(group="Visualization|false|no description") 
    @ParamInfo(name="Min Density", style="slider", options="value=20;min=10;max=100;") int percentage,
    @ParamGroupInfo(group="Visualization") 
    @ParamInfo(name="Membrane Color", style="color-chooser", options="value=java.awt.Color.green") Color mColor,
    @ParamGroupInfo(group="Visualization") 
    @ParamInfo(name="Membrane Transparency", style="slider", options="") int mTransparency,
    @ParamGroupInfo(group="Visualization") 
    @ParamInfo(name="Density Color 1  ", style="color-chooser", options="value=java.awt.Color.blue") Color dColorZero,
    @ParamGroupInfo(group="Visualization") 
    @ParamInfo(name="Density Color 2  ", style="color-chooser", options="value=java.awt.Color.blue") Color dColorOne,
    @ParamGroupInfo(group="Visualization") 
    @ParamInfo(name="Density Transparency", style="default", options="value=true") boolean dTransparency
    ) {

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

    result.addAll(VisUtil.density2Java3D(
      density.getDensity(), density.getGeometry(), percentage, dColorZero_real, dColorOne_real, true, true));
    result.addAll( geom3d.generateShape3DArray() );
    
    return result;
  }
}

