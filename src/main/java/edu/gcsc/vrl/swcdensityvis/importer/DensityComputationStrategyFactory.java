/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/**
 * 
 * @author stephan
 */
public final class DensityComputationStrategyFactory {
	/**
	 * 
	 * @param densityComputationType
	 * @return 
	 */
	public DensityComputationStrategy getDensityComputation(String densityComputationType) {
		return create(densityComputationType);
	}		
	
	/**
	 * 
	 * @param densityComputationType
	 * @return 
	 */
	private DensityComputationStrategy create(String densityComputationType) {
		if (densityComputationType.equalsIgnoreCase("TREE")) {
			return new TreeDensityComputation();
		} else if (densityComputationType.equalsIgnoreCase("EDGE")) {
			return new EdgeDensityComputation();
		} else {
			return new DefaultDensityComputation();
		}
	}

	public DensityComputationStrategy getDefaultDensityComputation() {
		return new DefaultDensityComputation();
	}
}
	