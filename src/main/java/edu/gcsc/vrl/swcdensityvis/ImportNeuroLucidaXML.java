/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// importsimport java.io.File;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector3d;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;

/**
 *
 * @author stephan
 */
public class ImportNeuroLucidaXML {
	private String file;
	/**
	 * @param file
	 */
	public ImportNeuroLucidaXML(String file) {
		setFile(file);
	}

	/**
	 * @brief setFile
	 * @param relativeFilePath 
	 */
	private void setFile(String relativeFilePath) {
		this.file = getClass().getResource(("/" + getClass().getPackage().toString().replaceAll("package", "").trim() + "/").replaceAll("\\.", "/") + "resources/" + relativeFilePath).getFile();
	}
	
	/**
	 * @brief getFile
	 * @return 
	 */
	private String getFile() {
		return this.file;
	}
	
	/**
	 * @brief parse
	 */
	public void parse() {
	  SAXBuilder builder = new SAXBuilder(XMLReaders.NONVALIDATING);
	  ArrayList<Contour> contours = new ArrayList<Contour>();
	  try {
	  System.err.println("parsing: " + this.file);
		Document document = builder.build(new File(getFile()));
		Element rootNode = document.getRootElement();
		System.err.println("rootNode: " + rootNode.toString());
 
		/// process contours
		for (Element node : rootNode.getChildren("contour")) {
		   Contour c = new Contour();
		   c.setName(node.getAttributeValue("name"));
		   ArrayList<Vector3d> points = new ArrayList<Vector3d>();
		   for (Element point : node.getChildren("point")) {
			   points.add(new Vector3d(Double.parseDouble(point.getAttributeValue("x")),
				   		  Double.parseDouble(point.getAttributeValue("y")),
				   		 Double.parseDouble(point.getAttributeValue("z"))));
				   	           
		   }
		   c.setPoints(points);
		   contours.add(c);
		   for (Vector3d vec : points) {
			   System.err.println(vec);
		   }
		}
		
		/**
		 * @todo implement
		 */
		/// process trees
		for (Element node : rootNode.getChildren("tree")) {
			Tree t = new Tree();
			t.setType(node.getAttributeValue("type"));
			ArrayList<Edge<Vector3d>> edges = new ArrayList<Edge<Vector3d>>();
			ArrayList<Element> points = (ArrayList<Element>) node.getChildren("point");
			for (int i = 0; i < points.size()-1; i+=2) {
			     edges.add(new Edge<Vector3d>(
				new Vector3d(Double.parseDouble(points.get(i).getAttributeValue("x")),
			   		  Double.parseDouble(points.get(i).getAttributeValue("y")),
			     		 Double.parseDouble(points.get(i).getAttributeValue("z"))),
				new Vector3d(Double.parseDouble(points.get(i+1).getAttributeValue("x")),
			   		  Double.parseDouble(points.get(i+1).getAttributeValue("y")),
			     		 Double.parseDouble(points.get(i+1).getAttributeValue("z")))));
			 }
			
			Vector3d point_before_branch = edges.get(edges.size()-1).getTo();
			if (!node.getChildren("branch").isEmpty()) {
				/// call parse(node.getChildren("branch"); -> i. e. get points as above, then add them,  etc, cf: http://stackoverflow.com/questions/13295621/recursive-xml-parser
				/**
				 * @todo process iteratively (think about using JAXB maybe here!)
				 * and make sure the data structure we must use, underlying the xml file
				 */
			} else {
				System.err.println("We don't have branches, finished!");
			}
		}
 
	  } catch (IOException io) {
		System.out.println(io.getMessage());
	  } catch (JDOMException jdomex) {
		System.out.println(jdomex.getMessage());
	  }
	}
	
	/**
	 * @brief main
	 * @param args 
	 */
	public static void main(String... args) {
		
		new ImportNeuroLucidaXML("files/test.xml").parse();
		new ImportNeuroLucidaXML("files/test2.xml").parse();
	}
}