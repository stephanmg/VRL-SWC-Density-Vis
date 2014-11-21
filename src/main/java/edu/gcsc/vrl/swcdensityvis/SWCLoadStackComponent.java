/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// imports
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Stephan Grein <stephan.grein@gcsc.uni-frankfurt.de>
 * @brief imports the SWC file (TODO needs probably to be split into more components)
 */
@ComponentInfo(name = "SWCDensityVisualization", category = "DensityVisualization/SWCDensityVisualization")
public class SWCLoadStackComponent implements Serializable {
	private static final long serialVersionUID = 1L;
	private File m_file;
	private HashMap<String, ArrayList<SWCCompartmentInformation>> m_compartments;

	/**
	 * @brief set filename
	 * @param file path to SWC file 
	 */
	public void get_file(
		@ParamInfo(name = "Filename") File file
	) {
		m_file = file;
	}

	/**
	 * @brief parse a folder of SWC files
	 * @param folder 
	 */
	public void parse_stack(
		@ParamInfo(name = "Folder") File folder
	) {
		File[] swcFiles = folder.listFiles(new FilenameFilter()
		{
    		@Override
 		 public boolean accept(File dir, String name) {
        	 return name.endsWith(".xml");
		}});
		
		for (File f : swcFiles) {
			parse(f);
		}
	}

		private void parse(
		@ParamInfo(name = "File") File file
	) {
		ArrayList<SWCCompartmentInformation> temp;
		try {
		   temp = SWCUtility.parse(file);
		} catch (FileNotFoundException e) {
			eu.mihosoft.vrl.system.VMessage.exception("File was not found: ", e.toString());
			return;
		} catch (IOException e) {
			eu.mihosoft.vrl.system.VMessage.exception("Could not read from file: ", e.toString());
			return;
		}
		m_compartments.put(file.getName(), temp);
		eu.mihosoft.vrl.system.VMessage.info("Successfully parsed the SWC file.", "");
	}
}

