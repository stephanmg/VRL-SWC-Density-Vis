/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.XML;

/// imports
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @brief XML file utilites
 * @author stephan
 */
public final class XMLFileUtil {
	private static final String NEWLINE = System.getProperty("line.separator");

	/**
	 * @brief 
	 */
	private XMLFileUtil() {

	}

	/**
	 * @brief copy files
	 * @param source
	 * @param dest
	 * @throws IOException 
	 */
	@SuppressWarnings("NestedAssignment")
	private static void copyFile(File source, File dest) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} finally {
			is.close();
			os.close();
		}
	}

	/**
	 * @brief checks for broken xml file, maybe not the best check since it
	 * relys on the line number, i. e. line 2 of xml file!
	 * @todo make the check better
	 * @param filename
	 * @throws IOException
	 */
	@SuppressWarnings("NestedAssignment")
	public static void fixXMLFile(String filename) throws IOException {
		FileWriter fw = null;
		FileReader fr = null;
		try {
			fr = new FileReader(new File(filename));
			BufferedReader br = new BufferedReader(fr);
			
			// backup input file
			String[] tokens = filename.split("\\.(?=[^\\.]+$)");
			copyFile(new File(filename), new File(tokens[0] + "_BAK_" + System.currentTimeMillis() + "." + tokens[1]));
			
			// read and correct the xml file
			String line;
			StringBuilder content = new StringBuilder();
			int count = 1;
			while ((line = br.readLine()) != null) {
				if (count == 2) {
					content.append("<mbf>");
				} else {
					content.append(line);
				}
				content.append(NEWLINE);
				count++;
			}
			fw = new FileWriter(new File(filename));
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content.toString());
		} finally {
			fr.close();
			fw.close();
		}
	}
}
