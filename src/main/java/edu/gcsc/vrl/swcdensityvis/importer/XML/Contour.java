/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.XML;

/// imports
import java.util.ArrayList;
import javax.vecmath.Vector3d;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author stephan
 */
@Getter @Setter @ToString
public class Contour {
	private String name;
	private ArrayList<Vector3d> points;
}
