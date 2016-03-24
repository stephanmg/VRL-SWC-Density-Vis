/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// imports
import edu.gcsc.vrl.swcdensityvis.data.FileSelectionType;
import edu.gcsc.vrl.swcdensityvis.data.LoadFolderFileType;
import edu.gcsc.vrl.swcdensityvis.importer.SWC.SWCLoadStackComponent;
import edu.gcsc.vrl.swcdensityvis.importer.SWC.SWCDensityVisualization;
import edu.gcsc.vrl.swcdensityvis.importer.SWC.ComputeSWCDistance;
import edu.gcsc.vrl.swcdensityvis.importer.SWC.ComputeSWCDensity;
import edu.gcsc.vrl.swcdensityvis.types.LA.DenseMatrix;
import edu.gcsc.vrl.swcdensityvis.types.LA.DenseMatrixArrayTestComponent;
import edu.gcsc.vrl.swcdensityvis.types.LA.DenseMatrixArrayType;
import edu.gcsc.vrl.swcdensityvis.types.LA.DenseMatrixFactory;
import edu.gcsc.vrl.swcdensityvis.types.LA.DenseMatrixMatrixTestComponent;
import edu.gcsc.vrl.swcdensityvis.types.LA.DenseMatrixSilentType;
import edu.gcsc.vrl.swcdensityvis.types.LA.DenseMatrixType;
import edu.gcsc.vrl.swcdensityvis.types.LA.DenseMatrixVectorTestComponent;
import edu.gcsc.vrl.swcdensityvis.types.LA.DenseVectorArrayTestComponent;
import edu.gcsc.vrl.swcdensityvis.types.LA.DenseVectorFactory;
import edu.gcsc.vrl.swcdensityvis.types.LA.Shape3DArrayTypeCustom;
import edu.gcsc.vrl.swcdensityvis.types.LA.SparseCCSMatrix;
import edu.gcsc.vrl.swcdensityvis.types.LA.SparseCCSMatrixType;
import edu.gcsc.vrl.swcdensityvis.types.LA.SparseCRSMatrix;
import edu.gcsc.vrl.swcdensityvis.types.LA.SparseCRSMatrixType;
import edu.gcsc.vrl.swcdensityvis.types.LA.SparseMatrixFactory;
import edu.gcsc.vrl.swcdensityvis.types.LA.SparseVector;
import edu.gcsc.vrl.swcdensityvis.types.LA.SparseVectorFactory;
import eu.mihosoft.vrl.io.IOUtil;
import eu.mihosoft.vrl.io.VJarUtil;
import eu.mihosoft.vrl.io.VersionInfo;
import eu.mihosoft.vrl.lang.visual.CompletionUtil;
import eu.mihosoft.vrl.system.InitPluginAPI;
import eu.mihosoft.vrl.system.PluginAPI;
import eu.mihosoft.vrl.system.PluginDependency;
import eu.mihosoft.vrl.system.PluginIdentifier;
import eu.mihosoft.vrl.system.ProjectTemplate;
import eu.mihosoft.vrl.system.VPluginAPI;
import eu.mihosoft.vrl.system.VPluginConfigurator;
import eu.mihosoft.vrl.system.VRLPlugin;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stephan Grein <stephan.grein@gcsc.uni-frankfurt.de>
 * @brief plugin configurator
 */
public class DensityVisualizationPluginConfigurator extends VPluginConfigurator {

	private File templateProjectSrc;
	private File templateProjectSrc2;
	private File templateProjectSrc3;

	public DensityVisualizationPluginConfigurator() {
		// identification of plugin, description and copyright
		setIdentifier(new PluginIdentifier("SWC-Density-Vis-Plugin", "0.4.5"));

		setDescription("Visualizes the density of a bunch of neuroanatomically-traced SWC/XML/ASC files");

		setCopyrightInfo("SWCDensityVisualizationPlugin",
			"(c) Stephan Grein", "www", "license", "license text");

		// allow export package to use by other projects
		exportPackage("edu.gcsc.vrl.swcdensityvis");

		// specify dependencies
		addDependency(new PluginDependency("VRL", "0.4.2.7", VersionInfo.UNDEFINED));
		addDependency(new PluginDependency("VRL-JFreeChart", "0.2.4", VersionInfo.UNDEFINED));
		addDependency(new PluginDependency("Density-Vis-Plugin", "0.2", VersionInfo.UNDEFINED));
	}

	@Override
	public void register(PluginAPI api) {
		// register plugin with canvas
		if (api instanceof VPluginAPI) {
			VPluginAPI vapi = (VPluginAPI) api;
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
			vapi.addTypeRepresentation(DenseMatrixType.class);
			vapi.addTypeRepresentation(DenseMatrixSilentType.class);
			vapi.addTypeRepresentation(DenseMatrixArrayType.class);
			vapi.addTypeRepresentation(SparseCCSMatrixType.class);
			vapi.addTypeRepresentation(SparseCRSMatrixType.class);
			vapi.addTypeRepresentation(Shape3DArrayTypeCustom.class);
			
			vapi.addComponent(TestComponent.class);
			vapi.addTypeRepresentation(FileSelectionType.class);
			vapi.addTypeRepresentation(LoadFolderFileType.class);
		}
	}

	@Override
	public void unregister(PluginAPI api) {
		// nothing to unregister
	}

	@Override
	public void install(InitPluginAPI iApi) {
		// ensure template projects are updated
		new File(iApi.getResourceFolder(), "template-01.vrlp").delete();
		new File(iApi.getResourceFolder(), "template-02.vrlp").delete();
		new File(iApi.getResourceFolder(), "template-03.vrlp").delete();
	}

	@Override
	public void init(InitPluginAPI iApi) {

		CompletionUtil.registerClassesFromJar(VJarUtil.getClassLocation(DensityVisualizationPluginConfigurator.class));

		initTemplateProject(iApi);
	}

	private void initTemplateProject(InitPluginAPI iApi) {
		templateProjectSrc = new File(iApi.getResourceFolder(), "template-01.vrlp");
		templateProjectSrc2 = new File(iApi.getResourceFolder(), "template-02.vrlp");
		templateProjectSrc3 = new File(iApi.getResourceFolder(), "template-03.vrlp");

		if (!templateProjectSrc.exists()) {
			saveProjectTemplate();
		}

		if (!templateProjectSrc2.exists()) {
			saveProjectTemplate2();
		}

		if (!templateProjectSrc3.exists()) {
			saveProjectTemplate3();
		}

		iApi.addProjectTemplate(new ProjectTemplate() {

			@Override
			public String getName() {
				return "SWC-Density-Vis - Template 1 (SWC)";
			}

			@Override
			public File getSource() {
				return templateProjectSrc;
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

		iApi.addProjectTemplate(new ProjectTemplate() {
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
		iApi.addProjectTemplate(new ProjectTemplate() {
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

	private void saveProjectTemplate() {
		InputStream in = DensityVisualizationPluginConfigurator.class.getResourceAsStream(
			"/edu/gcsc/vrl/swcdensityvis/resources/projects/template-01.vrlp");
		try {
			IOUtil.saveStreamToFile(in, templateProjectSrc);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(VRLPlugin.class.getName()).
				log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(VRLPlugin.class.getName()).
				log(Level.SEVERE, null, ex);
		}
	}

	private void saveProjectTemplate2() {
		InputStream in = DensityVisualizationPluginConfigurator.class.getResourceAsStream(
			"/edu/gcsc/vrl/swcdensityvis/resources/projects/template-02.vrlp");
		try {
			IOUtil.saveStreamToFile(in, templateProjectSrc2);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(VRLPlugin.class.getName()).
				log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(VRLPlugin.class.getName()).
				log(Level.SEVERE, null, ex);
		}
	}

	private void saveProjectTemplate3() {
		InputStream in = DensityVisualizationPluginConfigurator.class.getResourceAsStream(
			"/edu/gcsc/vrl/swcdensityvis/resources/projects/template-03.vrlp");
		try {
			IOUtil.saveStreamToFile(in, templateProjectSrc3);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(VRLPlugin.class.getName()).
				log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(VRLPlugin.class.getName()).
				log(Level.SEVERE, null, ex);
		}
	}
}
