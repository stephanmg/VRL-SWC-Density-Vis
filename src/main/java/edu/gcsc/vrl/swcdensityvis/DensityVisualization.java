package edu.gcsc.vrl.swcdensityvis;

import edu.gcsc.vrl.swcdensityvis.importer.SWC.SWCCompartmentInformation;
import edu.gcsc.vrl.densityvis.VisUtil;
import edu.gcsc.vrl.densityvis.*;
import edu.gcsc.vrl.swcdensityvis.importer.DensityComputationContext;
import edu.gcsc.vrl.swcdensityvis.importer.DensityComputationStrategyFactoryProducer;
import edu.gcsc.vrl.swcdensityvis.importer.DensityVisualizable;
import edu.gcsc.vrl.swcdensityvis.importer.DensityVisualizableFactory;
import edu.gcsc.vrl.swcdensityvis.importer.XML.XMLDensityUtil;
import edu.gcsc.vrl.swcdensityvis.importer.XML.XMLDensityVisualizer;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.reflection.Pair;
import eu.mihosoft.vrl.types.MethodRequest;
import eu.mihosoft.vrl.types.VCanvas3D;
import eu.mihosoft.vrl.v3d.Shape3DArray;
import eu.mihosoft.vrl.v3d.VGeometry3D;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import static javax.media.j3d.GeometryArray.COLOR_3;
import static javax.media.j3d.GeometryArray.COLOR_4;
import static javax.media.j3d.GeometryArray.COORDINATES;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 *
 * @author stephan
 */
@ComponentInfo(name="DensityVisualization", category="Neuro/SWC-Density-Vis")
public class DensityVisualization implements java.io.Serializable {
  private static final long serialVersionUID=1L;

  @OutputInfo(style="shaped3darraycustom")
  public Shape3DArray visualizeDensity(
    @ParamGroupInfo(group="Visualization|false|no description") 
    @ParamInfo(name="Density") DensityResult density,
    @ParamGroupInfo(group="Visualization|false|no description") 
    @ParamInfo(name="Min Density", style="slider", options="min=1;max=100;") int percentage,
    
    @ParamGroupInfo(group="Visualization") 
    @ParamInfo(name="Density Color 0", style="color-chooser", options="value=java.awt.Color.blue") Color dColorZero,
    @ParamGroupInfo(group="Visualization") 
    @ParamInfo(name="Density Color 1", style="color-chooser", options="value=java.awt.Color.red") Color dColorOne,
    @ParamGroupInfo(group="Visualization") 
    @ParamInfo(name="Density Transparency", style="default", options="value=true") boolean dTransparency,
    
    
    @ParamGroupInfo(group="Geometry|false|no description") 
    @ParamInfo(name="Line-graph Geometry") 
    File[] swcFiles,
    
    @ParamGroupInfo(group="Geometry|false|no description") 
    @ParamInfo(name="Line-graph Geometry Color", style="color-chooser", options="value=java.awt.Color.magenta") Color gColor,

    @ParamGroupInfo(group="Geometry|false|no description")
    @ParamInfo(name="Line-graph Geometry Transparency", style="slider", options="min=0;max=100;") int gTransparency,

    @ParamGroupInfo(group="Geometry|false|no description") 
    @ParamInfo(name="Bounding Box Color", style="color-chooser", options="value=java.awt.Color.green") Color mColor,
    
    @ParamGroupInfo(group="Geometry|false|no description") 
    @ParamInfo(name="Bounding Box Transparency", style="slider", options="") int mTransparency
    ) {

    Shape3DArray result = new Shape3DArray();
    
    /*VGeometry3D geom3d = new VGeometry3D(
      density.getGeometry(),
      new Color( mColor.getRed(), mColor.getGreen(), mColor.getBlue(), 255 - mTransparency),
      null,1F,false, false, mTransparency > 0);*/

    int transparencyVal = 254;

    if (dTransparency) {
      transparencyVal = 0;
    }

    Color dColorZero_real = new Color(dColorZero.getRed(), dColorZero.getGreen(), dColorZero.getBlue(), transparencyVal);
    Color dColorOne_real = new Color(dColorOne.getRed(), dColorOne.getGreen(), dColorOne.getBlue(), 254);
    Color gColor_real = new Color(gColor.getRed(), gColor.getGreen(), gColor.getBlue());

DensityVisualizableFactory factory = new DensityVisualizableFactory();
		/**
		 * @todo factory needs to be enhanced by the XMLDensityUtil maybe (i. e. DensityVisulziable)
		 */
String selection = "XML";
		DensityVisualizable visualizer = factory.getDensityVisualizer(selection);
		DensityComputationContext densityComputationContext = new DensityComputationContext();
		densityComputationContext.setDensityComputationStrategy(new DensityComputationStrategyFactoryProducer().getDefaultAbstractDensityComputationStrategyFactory().getDefaultComputationStrategy(selection));
		visualizer.setContext(densityComputationContext);
		
		XMLDensityVisualizer xmlDensityVisualizer = new XMLDensityVisualizer(XMLDensityUtil.getDefaultImpl());
		xmlDensityVisualizer.setContext(densityComputationContext);
		xmlDensityVisualizer.setFiles(new ArrayList<File>(Arrays.asList(swcFiles))); 
		xmlDensityVisualizer.prepare(gColor_real, 0.01);
		/**
		 * @todo setFiles could be also moved to the interface!
		 */
		
		/**
		 * @todo if we calculate geometry in the ComputeDensity it will get cached somehow! (this is by our implementation as this is expensive, but then leads here to multiple parent branch in java3d!)
		 */
		xmlDensityVisualizer.parseStack();
		result.addAll(xmlDensityVisualizer.calculateGeometry());

    
    //result.addAll(VisUtil.density2Java3D(
     // density.getDensity(), density.getGeometry(), percentage, dColorZero_real, dColorOne_real, true, true));
   // result.addAll( geom3d.generateShape3DArray() );
	
    /// add line graph geometry 
    /**
     * @todo geometry must also be scaled!!!
     */
   
    return result;
  }
  /**
 * Converts color from AWT to Java3D.
 *
 * @param c color to convert
 * @return color as Java3D color
 */
	private static Color3f color2Color3f(Color c) {
		return new Color3f(c.getRed() / 255f,
			c.getGreen() / 255f,
			c.getBlue() / 255f);
	}

	/**
	 * @brief
	 * @param c
	 * @param w percentage 0 to 100 (0 means no transparency, 100 means full transparecny)
	 * @return 
	 */
	private static Color4f color2Color4f(Color c, int w) {
		return new Color4f(c.getRed() / 255f,
			c.getGreen() / 255f,
			c.getBlue() / 255f, 
			(1 - w/100f));
	}
}

