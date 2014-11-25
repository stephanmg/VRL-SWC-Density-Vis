/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// imports
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
public class SWCDensityVisualizationPluginConfigurator extends VPluginConfigurator {
    private File templateProjectSrc;
	public SWCDensityVisualizationPluginConfigurator() {
		// identification of plugin, description and copyright
		setIdentifier(new PluginIdentifier("SWC-Density-Vis-Plugin", "0.0"));

		setDescription("Visualizes the density of a bunch of SWC files");

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
			// vapi.addTypeRepresentation(MyType.class);
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
    }
	

	@Override
 	   public void init(InitPluginAPI iApi) {

        CompletionUtil.registerClassesFromJar(
                VJarUtil.getClassLocation(SWCDensityVisualizationPluginConfigurator.class));
        
        initTemplateProject(iApi);
    }
    
	
	private void initTemplateProject(InitPluginAPI iApi) {
        templateProjectSrc = new File(iApi.getResourceFolder(), "template-01.vrlp");

        if (!templateProjectSrc.exists()) {
            saveProjectTemplate();
        }

        iApi.addProjectTemplate(new ProjectTemplate() {

            @Override
            public String getName() {
                return "SWC-Density-Vis - Template 1";
            }

            @Override
            public File getSource() {
                return templateProjectSrc;
            }

            @Override
            public String getDescription() {
                return "SWC-Density-Vis Template Project 1";
            }

            @Override
            public BufferedImage getIcon() {
                return null;
            }
        });
	}
	
	 private void saveProjectTemplate() {
        InputStream in = SWCDensityVisualizationPluginConfigurator.class.getResourceAsStream(
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

}
