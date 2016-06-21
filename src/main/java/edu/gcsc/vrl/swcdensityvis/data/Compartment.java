/// package's name
package edu.gcsc.vrl.swcdensityvis.data;

/// imports
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief stores all compartment information from a geometry file
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
public class Compartment implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<String> compartments = new ArrayList<String>();

	/**
	 * @brief default ctor
	 */
	public Compartment() {
	}

	/**
	 * @brief set all compartments
	 * @param list
	 */
	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public void set_names(List<String> list) {
		this.compartments = list;
	}

	/**
	 * @brief get all compartments
	 * @return
	 */
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	public List<String> get_names() {
		return this.compartments;
	}

}
