/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// imports
import eu.mihosoft.vrl.reflection.Pair;
import javax.vecmath.Vector3d;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Stephan Grein <stephan.grein@gcsc.uni-frankfurt.de>
 * @brief information (struct-like) for SWC compartments
 */
@Getter @Setter @ToString
public class SWCCompartmentInformation {
	public final static int COLUMNS_SIZE = 7;
	private int index;
	/* 
	 * the type is required for a filter option in the visualization, 
	 * i. e. display only the compartments which match a filter in e. g.
	 * a checkout or dropdown selection in the GUI frontend
         */
	private int type; 
	private Vector3d coordinates;
	private Pair<Integer, Integer> connectivity;
	private double thickness;
}
