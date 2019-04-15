/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// imports
import edu.gcsc.vrl.swcdensityvis.importer.SWC.SWCLoadStackComponent;
import edu.gcsc.vrl.swcdensityvis.importer.SWC.SWCDensityVisualization;
import edu.gcsc.vrl.swcdensityvis.importer.SWC.ComputeSWCDistance;
import edu.gcsc.vrl.swcdensityvis.importer.SWC.ComputeSWCDensity;
import edu.gcsc.vrl.swcdensityvis.types.LA.*;
import edu.gcsc.vrl.swcdensityvis.types.common.FileSelectionType;
import edu.gcsc.vrl.swcdensityvis.types.common.LoadFolderFileType;
import edu.gcsc.vrl.swcdensityvis.types.common.Shape3DArrayCustomType;
import edu.gcsc.vrl.swcdensityvis.types.common.CompartmentType;
import edu.gcsc.vrl.swcdensityvis.types.common.LoadCompartmentFileType;
import eu.mihosoft.vrl.io.IOUtil;
import eu.mihosoft.vrl.io.VJarUtil;
import eu.mihosoft.vrl.io.VersionInfo;
import eu.mihosoft.vrl.lang.visual.CompletionUtil;
import eu.mihosoft.vrl.system.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.awt.image.BufferedImage;
import lombok.extern.log4j.Log4j;

/**
 * @brief SWC-Density-Vis plugin configurator
 * @author stephanmg <stephan@syntaktische-zucker.de>
 */
@Log4j
public class DensityVisualizationPluginConfigurator extends VPluginConfigurator {
	/// templates
	private File templateProjectSrc1;
	private File templateProjectSrc2;
	private File templateProjectSrc3;

	/**
	 * @brief configurates plugin 
	 */
	public DensityVisualizationPluginConfigurator() {
		// identification of plugin, description and copyright
		setIdentifier(new PluginIdentifier("SWC-Density-Vis-Plugin", "0.4.8"));

		setDescription("Visualizes the density of a bunch of neuroanatomically-traced SWC/XML or ASC files");

		setCopyrightInfo("SWC-Density-Vis Plugin",
			"(c) stephan", "www.syntaktischer-zucker.de", "LGPLv3", "");

		// allow export package to use by other projects
		exportPackage("edu.gcsc.vrl.swcdensityvis");

		// specify dependencies (These dependencies might be not satisfied?!)
		addDependency(new PluginDependency("VRL", "0.4.2.7", VersionInfo.UNDEFINED));
		addDependency(new PluginDependency("VRL-JFreeChart", "0.2.4", VersionInfo.UNDEFINED));
		addDependency(new PluginDependency("Density-Vis-Plugin", "0.2", VersionInfo.UNDEFINED));
	}

	/**
	 * @brief register components and types
	 * @see VPluginConfigurator#register(eu.mihosoft.vrl.system.PluginAPI) 
	 * @param api 
	 */
	@Override
	public void register(PluginAPI api) {
		/// register plugin with canvas
		if (api instanceof VPluginAPI) {
			/// plugin API
			VPluginAPI vapi = (VPluginAPI) api;
			
			/// components
			vapi.addComponent(SWCLoadStackComponent.class);
			vapi.addComponent(ComputeSWCDensity.class);
			vapi.addComponent(ComputeSWCDistance.class);
			vapi.addComponent(ComputeDensity.class);
			vapi.addComponent(SWCDensityVisualization.class);
			vapi.addComponent(DensityVisualization.class);
			vapi.addComponent(DenseMatrixMatrixTestComponent.class);
			vapi.addComponent(DenseMatrixVectorTestComponent.class);
			vapi.addComponent(DenseMatrixArrayTestComponent.class);
			vapi.addComponent(DenseVectorArrayTestComponent.class);
			vapi.addComponent(DenseMatrix.class);
			vapi.addComponent(DenseMatrixFactory.class);
			vapi.addComponent(DenseVectorFactory.class);
			vapi.addComponent(SparseCCSMatrix.class);
			vapi.addComponent(SparseCRSMatrix.class);
			vapi.addComponent(SparseMatrixFactory.class);
			vapi.addComponent(SparseVectorFactory.class);
			vapi.addComponent(SparseVector.class);
			

			/// types
			vapi.addTypeRepresentation(DenseMatrixType.class);
			vapi.addTypeRepresentation(DenseMatrixSilentType.class);
			vapi.addTypeRepresentation(DenseMatrixArrayType.class);
			vapi.addTypeRepresentation(SparseCCSMatrixType.class);
			vapi.addTypeRepresentation(SparseCRSMatrixType.class);
			vapi.addTypeRepresentation(Shape3DArrayCustomType.class);
			vapi.addTypeRepresentation(LoadCompartmentFileType.class);
			vapi.addTypeRepresentation(CompartmentType.class);
			vapi.addTypeRepresentation(FileSelectionType.class);
			vapi.addTypeRepresentation(LoadFolderFileType.class);
		}
	}

	/**
	 * @brief unregister components and types
	 * @see VPluginConfigurator#unregister(eu.mihosoft.vrl.system.PluginAPI) 
	 * @param api 
	 */
	@Override
	public void unregister(PluginAPI api) {
		/// nothing to unregister
	}

	/**
	 * @brief install components, types and templates
	 * @see VPluginConfigurator#install(eu.mihosoft.vrl.system.InitPluginAPI) 
	 * @param api
	 */
	@Override
	public void install(InitPluginAPI api) {
		new File(api.getResourceFolder(), "template-01.vrlp").delete();
		new File(api.getResourceFolder(), "template-02.vrlp").delete();
		new File(api.getResourceFolder(), "template-03.vrlp").delete();
	}

	/**
	 * @brief init plugin
	 * @see VPluginConfigurator#init(eu.mihosoft.vrl.system.InitPluginAPI) 
	 * @param api 
	 */
	@Override
	public void init(InitPluginAPI api) {
		CompletionUtil.registerClassesFromJar(VJarUtil.getClassLocation(DensityVisualizationPluginConfigurator.class));
		initTemplateProject(api);
	}

	/**
	 * @brief init template project files
	 * @param api 
	 */
	private void initTemplateProject(InitPluginAPI api) {
		templateProjectSrc1 = new File(api.getResourceFolder(), "template-01.vrlp");
		templateProjectSrc2 = new File(api.getResourceFolder(), "template-02.vrlp");
		templateProjectSrc3 = new File(api.getResourceFolder(), "template-03.vrlp");

		if (!templateProjectSrc1.exists()) {
			saveProjectTemplate("/edu/gcsc/vrl/swcdensityvis/resources/projects/" + "template-01.vrlp");
		}

		if (!templateProjectSrc2.exists()) {
			saveProjectTemplate("/edu/gcsc/vrl/swcdensityvis/resources/projects/" + "template-02.vrlp");
		}

		if (!templateProjectSrc3.exists()) {
			saveProjectTemplate("/edu/gcsc/vrl/swcdensityvis/resources/projects/" + "template-03.vrlp");
		}

		/// 1st template
		api.addProjectTemplate(new ProjectTemplate() {
			@Override
			public String getName() {
				return "SWC-Density-Vis - Template 1 (SWC)";
			}

			@Override
			public File getSource() {
				return templateProjectSrc1;
			}

			@Override
			public String getDescription() {
				return "SWC-Density-Vis Template Project 1 - This"
					+ "tests the basic SWC density visualization"
					+ "as well as the line-graph geometry is visualized";
			}

			@Override
			public BufferedImage getIcon() {
				return null;
			}
		});


		/// 2nd template
		api.addProjectTemplate(new ProjectTemplate() {
			@Override
			public String getName() {
				return "SWC-Density-Vis - Template 2 (Matrices)";
			}

			@Override
			public File getSource() {
				return templateProjectSrc2;
			}

			@Override
			public String getDescription() {
				return "SWC-Density-Vis Template Project 2 - This"
					+ "tests the various matrix/matrix and "
					+ "matrix/vector operations and visualization"
					+ "of the previous";
			}

			@Override
			public BufferedImage getIcon() {
				return null;
			}
		});
		

		/// 3rd template
		api.addProjectTemplate(new ProjectTemplate() {
			@Override
			public String getName() {
				return "SWC-Density-Vis - Template 3 (XML)";
			}

			@Override
			public File getSource() {
				return templateProjectSrc3;
			}

			@Override
			public String getDescription() {
				return "SWC-Density-Vis Template Project 3 - This"
					+ " tests the XML geometry file import and"
					+ " density visualization and visualization"
					+ " of the neuronal topology/morphology";
			}

			@Override
			public BufferedImage getIcon() {
				return null;
			}
		});
	}
	
	/**
	 * @brief save the project template as a file
	 * @param str 
	 */
	private void saveProjectTemplate(String str) {
		InputStream in = DensityVisualizationPluginConfigurator.class.getResourceAsStream(str);
		
		try {
			IOUtil.saveStreamToFile(in, new File(str));
		} catch (FileNotFoundException ex) {
			log.error(ex);
		} catch (IOException ex) {
			log.error(ex);
		}
	}
}
