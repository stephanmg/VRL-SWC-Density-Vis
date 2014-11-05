/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// imports
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.reflection.Pair;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import javax.vecmath.Vector3d;

/**
 *
 * @author Stephan Grein <stephan.grein@gcsc.uni-frankfurt.de>
 * @brief imports the SWC file (TODO needs probably to be split into more components)
 */
@ComponentInfo(name = "SWCDensityVisualization", category = "DensityVisualization/SWCDensityVisualization")
public class SWCDensityVisualization implements Serializable {
	private static final long serialVersionUID = 1L;
	private File m_file;
	private HashMap<String, ArrayList<CompartmentInfo>> m_compartments;

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
			try {
				parse(f);
			} catch(IOException e) {
				eu.mihosoft.vrl.system.VMessage.exception("Error while reading a SWC file: ", e.toString());
			}
		}
	}

	/**
	 * @brief parses the swc file
	 * @param file 
	 * @throws IOException
	 */
	@SuppressWarnings("NestedAssignment")
	private void parse(File file) throws IOException {
		ArrayList<CompartmentInfo> temp = new ArrayList<CompartmentInfo>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#")) {
					String[] columns = line.split(" ");
					assert columns.length != CompartmentInfo.COLUMNS_SIZE : "SWC not in standardized format, "
						+ "i. e. columns do not match the format specification.";
					CompartmentInfo info = new CompartmentInfo();
					info.setIndex(Integer.parseInt(columns[0]) - 1);
					info.setType(Integer.parseInt(columns[1]));
					info.setCoordinates(new Vector3d(Double.parseDouble(columns[2]),
						Double.parseDouble(columns[3]),
						Double.parseDouble(columns[4])));

					info.setThicknesses(Double.parseDouble(columns[5]));
					info.setConnectivity(new Pair<Integer, Integer>(Integer.parseInt(columns[6]),
						Integer.parseInt(columns[7])));
					temp.add(info);
				}
			}
		} catch (FileNotFoundException e) {
			eu.mihosoft.vrl.system.VMessage.exception("File was not found: ", e.toString());
		} catch (IOException e) {
			eu.mihosoft.vrl.system.VMessage.exception("Could not read from file: ", e.toString());
		} finally {
			br.close();
		}
		m_compartments.put(file.getName(), temp);
		eu.mihosoft.vrl.system.VMessage.info("Successfully parsed the SWC file.", "");
	}
}
