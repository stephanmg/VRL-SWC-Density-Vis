/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gcsc.vrl.swcdensityvis;

/**
 * @brief utilities
 * @author stephan
 */
public class SwappablePairUtility {
	public static<T extends java.lang.Number> void swap(SwappablePair<T> spair) {
		T temp = spair.getFirst();
		spair.setFirst(spair.getSecond());
		spair.setSecond(temp);
	}
}
