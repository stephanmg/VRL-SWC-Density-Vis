package edu.gcsc.vrl.swcdensityvis;


import edu.gcsc.vrl.densityvis.Density;
import eu.mihosoft.vrl.annotation.ObjectInfo;
import eu.mihosoft.vrl.v3d.VTriangleArray;

/**
 * Encapsulates density information for easier handling of co
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@ObjectInfo(serialize=false, serializeParam=false)
public class DensityResult {
    private transient VTriangleArray geometry;
    private transient Density density;

    /**
     * Constructor.
     * @param density
     * @param geometry 
     */
    public DensityResult(Density density, VTriangleArray geometry) {
        this.geometry = geometry;
        this.density = density;
    }

    /**
     * @return density information
     */
    public Density getDensity() {
        return density;
    }

    /**
     * @return geometry
     */
    public VTriangleArray getGeometry() {
        return geometry;
    }
    
    
}
