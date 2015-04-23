package mkoenig.internal;

/*
Copyright (c) 2014 Matthias Koenig

This library is free software; you can redistribute it and/or modify it
under the terms of the GNU Lesser General Public License as published
by the Free Software Foundation; either version 2.1 of the License, or
any later version.

This library is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
documentation provided hereunder is on an "as is" basis, and the
Institute for Systems Biology and the Whitehead Institute
have no obligations to provide maintenance, support,
updates, enhancements or modifications.  In no event shall the
Institute for Systems Biology and the Whitehead Institute
be liable to any party for direct, indirect, special,
incidental or consequential damages, including lost profits, arising
out of the use of this software and its documentation, even if the
Institute for Systems Biology and the Whitehead Institute
have been advised of the possibility of such damage.  See
the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this library; if not, write to the Free Software Foundation,
Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/


import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.session.CyNetworkNaming;
import org.osgi.framework.BundleContext;

import java.util.Properties;

import mkoenig.internal.SBMLFileFilter;
import mkoenig.internal.SBMLNetworkViewTaskFactory;

/**
 * cy3sbml - SBML Plugin for Cytoscape 3 based on JSBML.
 * 
 * @author Matthias Koenig (konigmatt@googlemail.com)
 * @date 2015-04-23
 * 
 */
  
public class CyActivator extends AbstractCyActivator {
	public static final String NAME = "cy3sbml"; 
	public static final String VERSION = "0.1";
	
	public CyActivator() {
		super();
	}

	public void printInfo(){
		System.out.println("********************************");
		System.out.println("cy3sbml");
		System.out.println("********************************");	
	}
	
	public void start(BundleContext bc) {
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
	
	/** Adding all the Cytoscape actions to the menu bar */
	private void addCytoscapeActions(){
		CyMenus menus = Cytoscape.getDesktop().getCyMenus();
    	menus.addCytoscapeAction(new ImportAction(getIconForName("import"), this));
    	menus.addCytoscapeAction(new BiomodelAction(getIconForName("biomodel"), this));
    	menus.addCytoscapeAction(new ValidationAction(getIconForName("validation"), this));
    	menus.addCytoscapeAction(new ChangeStateAction(getIconForName("changestate"), this));
    	menus.addCytoscapeAction(new HelpAction(getIconForName("help"), this));
		menus.addCytoscapeAction(new SaveLayoutAction(getIconForName("savelayout"), this));
		menus.addCytoscapeAction(new LoadLayoutAction(getIconForName("loadlayout"), this));
	}
	
}

