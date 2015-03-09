/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.XML;

/// imports
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author stephan
 */
public final class XMLFileUtil {

	private XMLFileUtil() {

	}

	private static void copyFiles(File source, File dest) throws IOException {
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
	 * @brief checks for broken xml file, maybe not the best check
	 *        since it relys on the line number, i. e. line 2 of xml file!
	 * @todo make the check better
	 * @param filename
	 */
	static void fixXMLFile(String filename) {
		try {
			final String newLine = System.getProperty("line.separator");
			File xml = new File(filename);
			FileReader fr = new FileReader(xml);
			String line;
			StringBuilder content_new = new StringBuilder();
			String[] tokens = filename.split("\\.(?=[^\\.]+$)");
			copyFiles(new File(filename), new File(tokens[0] + "_BAK_" + System.currentTimeMillis() + "." + tokens[1]));
			BufferedReader br = new BufferedReader(fr);
			int count = 1;
			while ((line = br.readLine()) != null) {
				if (count == 2) {
					content_new.append("<mbf>");
				} else {
					content_new.append(line);
				}
				content_new.append(newLine);
				count++;
			}
			fr.close();

			FileWriter fw = new FileWriter(xml);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content_new.toString());
			bw.close();
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + e);
		} catch (IOException e) {
			System.err.println("Unexpected IO Exception: " + e);

		}
	}
}
