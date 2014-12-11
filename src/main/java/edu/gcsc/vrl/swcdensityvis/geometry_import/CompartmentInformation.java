/// package's name
package edu.gcsc.vrl.swcdensityvis.geometry_import;

/// imports
import eu.mihosoft.vrl.reflection.Pair;
import javax.vecmath.Vector3f;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author stephan
 */
@Getter
@Setter
@ToString
public abstract class CompartmentInformation {
	@SuppressWarnings("ProtectedField")
	protected int index;
	@SuppressWarnings("ProtectedField")
	protected int type;
	@SuppressWarnings("ProtectedField")
	protected Vector3f coordinates;
	@SuppressWarnings("ProtectedField")
	protected Pair<Integer, Integer> connectivity;
	@SuppressWarnings("ProtectedField")
	protected double thickness;
}
