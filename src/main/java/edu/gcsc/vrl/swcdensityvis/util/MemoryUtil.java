/// package's name
package edu.gcsc.vrl.swcdensityvis.util;

/**
 * @brief memory utilities
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
public final class MemoryUtil {
	/// private static members
	private static final int MiB = (int) Math.pow(1024, 2);
	
	/**
	 * @brief ctor
	 */
	private MemoryUtil() {
		
	}
	
	/**
	 * @brief prints memory heap usage
	 */
	public static void printHeapMemoryUsage() {
        	Runtime runtime = Runtime.getRuntime();
         
        	System.err.println("Used Memory:"
 	           + (runtime.totalMemory() - runtime.freeMemory()) / MiB);
 
        	System.err.println("Free Memory:"
           	 + runtime.freeMemory() / MiB);
         
        	System.err.println("Total Memory:" + runtime.totalMemory() / MiB);
 
        	System.err.println("Max Memory:" + runtime.maxMemory() / MiB);
	}
}
