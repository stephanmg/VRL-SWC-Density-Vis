/// package's name
package edu.gcsc.vrl.swcdensityvis.data;

/// imports
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief represent the selected files
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
public class FileSelection implements Serializable {
	private static final long serialVersionUID = 1L;
	private ArrayList<String> names;
	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public void set_names(List<String> names) {
		this.names = new ArrayList<String>(names);
	}
	
	/**
	 * @brief ctor
	 */
	public FileSelection() {
	}

	/**
	 * @brief get all section names
	 * @return
	 */
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	public List<String> get_names() {
		return this.names;
	}

}

