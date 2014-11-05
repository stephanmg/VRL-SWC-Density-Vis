/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// imports
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.reflection.Pair;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;
import javax.vecmath.Vector3d;

/**
 *
 * @author Stephan Grein <stephan.grein@gcsc.uni-frankfurt.de>
 */
@ComponentInfo(name="SWCDensityVisualization", category="DensityVisualization/SWCDensityVisualization")
public class SWCDensityVisualization implements Serializable {
    //private static final Logger log = Logger.getLogger(SWCDensityVisualization.class.getName());
    private static final long serialVersionUID = 1L;
	 private String m_file;
	 private ArrayList<CompartmentInfo> m_compartments;
    
    public String doSomething() {
        return "Plugin works!";
    }

	 /**
	  * @brief set filename
	  */
	 public void get_file(
			@ParamInfo(name="Command String", style="code") String file
	 ) {
			m_file = file;
	 }
    
	 /**
	  * @brief parses the swc file
	  * @throws IOException
	  */
    public void parse() throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(m_file));
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
					info.setConnectivity(new Pair(Integer.parseInt(columns[6]), 
							  								Integer.parseInt(columns[7])));
					m_compartments.add(info);
				}
			}
		} catch (FileNotFoundException e) {
			eu.mihosoft.vrl.system.VMessage.exception("File was not found: ", e.toString());		  
		} catch (Exception e) {
         eu.mihosoft.vrl.system.VMessage.exception("Could not read from file: ", e.toString());
		} finally {
			br.close();
		}
	eu.mihosoft.vrl.system.VMessage.info("Successfully parsed the SWC file.", "");
	}
}