/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// imports
import edu.gcsc.vrl.swcdensityvis.data.FileSelection;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.File;
import java.io.Serializable;

/**
 * @brief a component for quick test of the visual representation of a type
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
@ComponentInfo(name = "TestComponent", category = "Neuro/SWC-Density-Vis")
public class TestComponent implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @brief ctor
	 */
	public TestComponent() {
		
	}

	
	public void input(
	) {
		
	}

	/**
	 * 
	 * @param selection
	 * @param hoc_file
	 */
	@MethodInfo(name="Selection", hide=false)
	public void selection(
		@ParamInfo(name = "Sections", typeName="The compartment of the multi-compartmental model loaded", style = "default", options = "hoc_tag=\"gridFile\"; visibleElements=3") FileSelection selection,
		@ParamInfo(name = "Load", typeName="Load any hoc geometry", style = "my-folder-load-dialog", options = "hoc_tag=\"gridFile\"; file_type=\"xml\"; displayFullPath=false") File hoc_file
	
	) {
		
	}
}
