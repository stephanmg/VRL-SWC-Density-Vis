/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// imports
import eu.mihosoft.vrl.reflection.Pair;
import javax.vecmath.Vector3d;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Stephan Grein <stephan.grein@gcsc.uni-frankfurt.de>
 * @brief information (struct-like) for SWC compartments
 */
@Getter @Setter
public class SWCCompartmentInformation {
	public final static int COLUMNS_SIZE = 7;
	private int index;
	private int type;
	private Vector3d coordinates;
	private Pair<Integer, Integer> connectivity;
	private double thickness;
}
