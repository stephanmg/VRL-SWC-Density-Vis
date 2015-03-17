/// package's name
package edu.gcsc.vrl.swcdensityvis.data;

/// imports
import java.util.ArrayList;

/**
 * @brief provides information of the loaded folder (i. e. the files contained in the folder)
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
public class FolderInfo {
	private final ArrayList<String> m_sectionNames = new ArrayList<String>();
	private int m_noSections = 0;
	
	/**
	 * @brief ctor
	 */
	public FolderInfo() {
	}
	
	/**
	 * @brief sets the number of sections
	 * @param noSections number of sections
	 */
	public synchronized void set_num_sections(int noSections) {
		m_noSections = noSections;
	}
	
	/**
	 * @brief sets the names of sections
	 * @param sectionNames names of sections
	 */
	public synchronized void set_names_sections(ArrayList<String> sectionNames) {
		m_sectionNames.addAll(sectionNames);
	}
	
	/**
	 * @brief gets the number of sections
	 * @return 
	 */
	public synchronized int get_num_sections() {
		return m_noSections;
	}
	
	/**
	 * @brief gets the names of sections
	 * @return 
	 */
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	public synchronized ArrayList<String> get_names_sections() {
		return m_sectionNames;
	}
}
