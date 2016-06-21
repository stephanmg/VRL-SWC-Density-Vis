/// package's name
package edu.gcsc.vrl.swcdensityvis.data;

/// imports
import java.util.HashSet;
import java.util.Set;

/**
 * @brief stores the compartment information from geometry file
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
public class CompartmentInfo {
	private final Set<String> m_compartmentNames = new HashSet<String>();
	private int m_noCompartments = 0;
	
	/**
	 * @brief default ctor
	 */
	public CompartmentInfo() {
	}
	
	/**
	 * @brief sets the number of compartments
	 * @param noCompartments number of compartments
	 */
	public synchronized void set_num_compartments(int noCompartments) {
		m_noCompartments = noCompartments;
	}
	
	/**
	 * @brief sets the names of compartments
	 * @param compartmentNames names of compartments
	 */
	public synchronized void set_names_compartments(Set<String> compartmentNames) {
		m_compartmentNames.addAll(compartmentNames);
	}
	
	/**
	 * @brief gets the number of compartments
	 * @return 
	 */
	public synchronized int get_num_compartments() {
		return m_noCompartments;
	}
	
	/**
	 * @brief gets the names of compartments
	 * @return 
	 */
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	public synchronized Set<String> get_names_compartments() {
		return m_compartmentNames;
	}
}
