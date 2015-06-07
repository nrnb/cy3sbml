package org.cy3sbml;


import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.service.util.AbstractCyActivator;
import org.osgi.framework.BundleContext;

import java.util.Properties;

import org.cy3sbml.SBMLFileFilter;
import org.cy3sbml.SBMLNetworkViewTaskFactory;
import org.cy3sbml.gui.NavControlPanel;


public class CyActivator extends AbstractCyActivator {
	public CyActivator() {
		super();
	}

	public void printInfo(){
		System.out.println("Start *** cy3sbml ***");	
	}
	
	public void start(BundleContext bc) {
		try {
		printInfo();
		// register the file reader
		CyNetworkFactory cyNetworkFactoryServiceRef = getService(bc,CyNetworkFactory.class);
		CyNetworkViewFactory cyNetworkViewFactoryServiceRef = getService(bc,CyNetworkViewFactory.class);
		StreamUtil streamUtilRef = getService(bc,StreamUtil.class);
		
		SBMLFileFilter sbmlFilter = new SBMLFileFilter("SBML files (*.xml)",streamUtilRef);
		SBMLNetworkViewTaskFactory sbmlNetworkViewTaskFactory = new SBMLNetworkViewTaskFactory(sbmlFilter,cyNetworkFactoryServiceRef,cyNetworkViewFactoryServiceRef);
		
		Properties sbmlNetworkViewTaskFactoryProps = new Properties();
		sbmlNetworkViewTaskFactoryProps.setProperty("readerDescription","SBML (Cy3SBML) file reader");
		sbmlNetworkViewTaskFactoryProps.setProperty("readerId","cy3sbmlNetworkViewReader");
		registerService(bc,sbmlNetworkViewTaskFactory,InputStreamTaskFactory.class, sbmlNetworkViewTaskFactoryProps);
		
		
		// register the Control Panel

		CySwingApplication cytoscapeDesktopService = getService(bc, CySwingApplication.class);
		
		NavControlPanel navControlPanel = NavControlPanel.getInstance();
		ControlPanelAction controlPanelAction = new ControlPanelAction(cytoscapeDesktopService, navControlPanel);
		
		registerService(bc, navControlPanel, CytoPanelComponent.class, new Properties());
		registerService(bc, controlPanelAction, CyAction.class, new Properties());

		} catch (Exception e){
			e.printStackTrace();
		}
		
		
		
		/*
		// make the menu item
		CyNetworkNaming cyNetworkNamingServiceRef = getService(bc,CyNetworkNaming.class);
		CyNetworkManager cyNetworkManagerServiceRef = getService(bc,CyNetworkManager.class);
		CyNetworkViewManager cyNetworkViewManagerServiceRef = getService(bc,CyNetworkViewManager.class);
		
		CreateNetworkViewTaskFactory createNetworkViewTaskFactory = new CreateNetworkViewTaskFactory(cyNetworkNamingServiceRef, cyNetworkFactoryServiceRef,cyNetworkManagerServiceRef, cyNetworkViewFactoryServiceRef,cyNetworkViewManagerServiceRef);
				
		Properties createNetworkViewTaskFactoryProps = new Properties();
		createNetworkViewTaskFactoryProps.setProperty("preferredMenu","Apps.Samples");
		createNetworkViewTaskFactoryProps.setProperty("title","Test CySBML3 new");
		registerService(bc,createNetworkViewTaskFactory,TaskFactory.class, createNetworkViewTaskFactoryProps);
		*/
	}
}

