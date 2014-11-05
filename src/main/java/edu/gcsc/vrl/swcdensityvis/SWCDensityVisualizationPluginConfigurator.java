/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gcsc.vrl.swcdensityvis;

import eu.mihosoft.vrl.system.InitPluginAPI;
import eu.mihosoft.vrl.system.PluginAPI;
import eu.mihosoft.vrl.system.PluginDependency;
import eu.mihosoft.vrl.system.PluginIdentifier;
import eu.mihosoft.vrl.system.VPluginAPI;
import eu.mihosoft.vrl.system.VPluginConfigurator;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class SWCDensityVisualizationPluginConfigurator extends VPluginConfigurator{

    public SWCDensityVisualizationPluginConfigurator() {
       // identification of plugin, description and copyright
       setIdentifier(new PluginIdentifier("SWCDensityVisualizationPlugin", "0.0"));
       
       setDescription("Visualizes the density of a bunch of SWC files");
       
       setCopyrightInfo("SWCDensityVisualizationPlugin",
               "(c) Stephan Grein", "www", "license", "license text");

       // allow export package
       // exportPackage("edgu.gcsc.vrl.swcdensityvis");
       
       // specify dependencies
       // addDependency(new PluginDependency("VRL", "0.4.0", "0.4.0"));
    }
    
    @Override
    public void register(PluginAPI api) {
       // register plugin with canvas
       if (api instanceof VPluginAPI) {
           VPluginAPI vapi = (VPluginAPI) api;
           vapi.addComponent(SWCDensityVisualization.class);
           // vapi.addTypeRepresentation(MyType.class);
       }
   }

    @Override
   public void unregister(PluginAPI api) {
       // nothing to unregister
   }

    @Override
    public void init(InitPluginAPI iApi) {
       // nothing to init
   }
 }
