/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// imports
import eu.mihosoft.vrl.reflection.Pair;
import javax.vecmath.Vector3d;

/**
 *
 * @author Stephan Grein <stephan.grein@gcsc.uni-frankfurt.de>
 * @brief information (struct-like) for SWC compartments
 */
public class CompartmentInfo {
	public final static int COLUMNS_SIZE = 7;
	private int index;
	private int type;
	private Vector3d coordinates;
	private Pair<Integer, Integer> connectivity;
	private Double thickness;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Vector3d getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Vector3d coordinates) {
		this.coordinates = coordinates;
	}

	public Pair<Integer, Integer> getConnectivity() {
		return connectivity;
	}

	public void setConnectivity(Pair<Integer, Integer> connectivity) {
		this.connectivity = connectivity;
	}

	public double getThicknesses() {
		return thickness;
	}

	public void setThicknesses(double thickness) {
		this.thickness = thickness;
	}
}
