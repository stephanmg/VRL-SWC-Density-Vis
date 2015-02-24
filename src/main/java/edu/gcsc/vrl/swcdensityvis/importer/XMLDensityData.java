/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/// imports

import edu.gcsc.vrl.swcdensityvis.data.Edge;
import java.util.ArrayList;
import java.util.HashMap;
import javax.vecmath.Vector3f;


/**
 *
 * @author stephan
 */
public final class XMLDensityData implements DensityData {
	private final HashMap<String, ArrayList<Edge<Vector3f>>> data;
	
	/**
	 * @param data
	 * @brief 
	 */
	public XMLDensityData(HashMap<String, ArrayList<Edge<Vector3f>>> data) {
		this.data = data;
	}
	
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	public HashMap<String, ArrayList<Edge<Vector3f>>> getDensityData() {
		return this.data;
	}

}
