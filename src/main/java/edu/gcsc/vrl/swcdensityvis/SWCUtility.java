/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gcsc.vrl.swcdensityvis;

import eu.mihosoft.vrl.reflection.Pair;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.vecmath.Vector3d;

/**
 *
 * @author stephan
 * @brief utilites for SWC files
 */
public final class SWCUtility {
	/**
	 * @brief private ctor since utility classs should be final and private
	 */
	private SWCUtility() {
		
	}

	/**
	 * @brief parses a swc file
	 * @param file 
	 * @return list of compartment information for this file
	 * @throws IOException
	 */
	@SuppressWarnings("NestedAssignment")
	public static ArrayList<SWCCompartmentInformation> parse(File file) throws IOException {
		ArrayList<SWCCompartmentInformation> temp = new ArrayList<SWCCompartmentInformation>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#")) {
					String[] columns = line.split(" ");
					assert columns.length != SWCCompartmentInformation.COLUMNS_SIZE : "SWC not in standardized format, "
						+ "i. e. columns do not match the format specification.";
					SWCCompartmentInformation info = new SWCCompartmentInformation();
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
		} finally {
			br.close();
		}
		return temp;
	}
	
	/**
	 * @brief parses a bunch of swc file
	 * @param folder 
	 * @return hashmap of arraylist of information for each compartment in each file
	 * @throws IOException
	 */
	@SuppressWarnings("NestedAssignment")
	public static HashMap<String, ArrayList<SWCCompartmentInformation>> parseStack(File folder) throws IOException { 
		HashMap<String, ArrayList<SWCCompartmentInformation>> compartments = null;
		File[] swcFiles = folder.listFiles(new FilenameFilter()
		{
    		@Override
 		 public boolean accept(File dir, String name) {
        	 return name.endsWith(".xml");
		}});
		
		for (File f : swcFiles) {
			compartments.put(f.getName(), parse(f));
		}
		
	return compartments;
	}
	
	
	/**
	 * @brief get bounding box for an anonymous cell
	 * @param cell
	 * @return pair min max coordinates 3d
	 */
	public static Pair<Vector3d, Vector3d> getBoundingBox(final ArrayList<SWCCompartmentInformation> cell) {
		return getBoundingBox(	new HashMap<String, ArrayList<SWCCompartmentInformation>>()
						{{ put("Anonymous cell", cell); }}
					);
	}
	
	/**
	 * @brief get bounding box for a bunch of cells
	 * @param cells input cells 
	 * @return pair min max coordinates 3d
	 */
	public static Pair<Vector3d, Vector3d> getBoundingBox(HashMap<String, ArrayList<SWCCompartmentInformation>> cells) {
		ArrayList<Double> temp_x = null;
		ArrayList<Double> temp_y = null;
		ArrayList<Double> temp_z = null;
		
		for (Map.Entry<String, ArrayList<SWCCompartmentInformation>> entry : cells.entrySet()) {
			for (SWCCompartmentInformation info : entry.getValue()) {
				temp_x.add(info.getCoordinates().x);
				temp_y.add(info.getCoordinates().y);
				temp_z.add(info.getCoordinates().z);
			}
		}
		
		return new Pair<Vector3d, Vector3d>
			(
			new Vector3d(Collections.min(temp_x), Collections.min(temp_y), Collections.min(temp_z)), 
			new Vector3d(Collections.max(temp_x), Collections.max(temp_y), Collections.max(temp_z))
			);

	}
	
	/**
	 * @brief get dimensions (width, height and depth) for the cells 
	 * @param cells
	 * @return the dimensions as a vector
	 */
	public static Vector3d getDimensions(HashMap<String, ArrayList<SWCCompartmentInformation>> cells) {
		Pair<Vector3d, Vector3d> bounding = getBoundingBox(cells);
		return new Vector3d(
			bounding.getFirst().x - bounding.getSecond().x,
			bounding.getFirst().y - bounding.getSecond().y,
			bounding.getFirst().z - bounding.getSecond().z	
		);
	}
}
	
	