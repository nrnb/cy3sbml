package org.cy3sbml.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.cy3sbml.SBMLManager;
import org.cy3sbml.ServiceAdapter;
import org.cy3sbml.mapping.NavigationTree;
import org.cy3sbml.mapping.One2ManyMapping;
import org.cy3sbml.miriam.NamedSBaseInfoThread;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.model.events.RowSetRecord;
import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.view.model.CyNetworkView;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.NamedSBase;
import org.sbml.jsbml.SBMLDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Results Panel for cy3sbml registered as Cytoscape Results Panel.
 * This is the main display area for information.
 */
public class ResultsPanel extends JPanel implements CytoPanelComponent, HyperlinkListener, RowsSetListener{
	private static final Logger logger = LoggerFactory.getLogger(ResultsPanel.class);
	private static final long serialVersionUID = 1L;

	CytoPanel cytoPanelEast;
	private static ResultsPanel uniqueInstance;
	
	private ServiceAdapter adapter;
	private NavigationTree navigationTree;
	private SBMLManager sbmlManager;
	
	private JTree sbmlTree;
	private JEditorPaneSBML textPane;

	
	public static synchronized ResultsPanel getInstance(ServiceAdapter adapter){
		if (uniqueInstance == null){
			logger.info("ControlPanel created");
			uniqueInstance = new ResultsPanel(adapter);
		}
		return uniqueInstance;
	}
	public static synchronized ResultsPanel getInstance(){
		return uniqueInstance;
	}
	
	/** Constructor */
	private ResultsPanel(ServiceAdapter adapter){
		/** Construct the Navigation panel for cy3sbml. */
		this.adapter = adapter;
		this.cytoPanelEast = adapter.cySwingApplication.getCytoPanel(CytoPanelName.EAST);
		this.sbmlManager = SBMLManager.getInstance();
		this.navigationTree = new NavigationTree();
		
		setLayout(new BorderLayout(0, 0));
		
		// --- Annotation Area ------------------
		textPane = new JEditorPaneSBML();
		textPane.addHyperlinkListener(this);
		
		JScrollPane annotationScrollPane = new JScrollPane();
		annotationScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		annotationScrollPane.setViewportView(textPane);

		this.add(textPane);
		/*
		// --- Navigation Tree  ------------------
		sbmlTree = new JTree();
		sbmlTree.setVisibleRowCount(12);
		sbmlTree.setEditable(false);
		
		// TODO: listen to tree events
		// sbmlTree.addTreeSelectionListener(this);
		
		sbmlTree.setFont(new Font("Dialog", Font.PLAIN, 10));
		
		// TODO: handle the navigation tree
		// setNavigationTreeInJTree();
		
		JScrollPane treeScrollPane = new JScrollPane();
		treeScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		treeScrollPane.setViewportView(sbmlTree);	
		
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.2);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		// set coponents
		splitPane.setRightComponent(annotationScrollPane);
		splitPane.setLeftComponent(treeScrollPane);
		
		this.add(splitPane);
		*/
		
		// set the size
		Dimension size = this.getSize();
		this.setSize(400, size.height);
	}
	
	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.EAST;
	}
	
	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(getClass().getResource("/images/cy3sbml_icon.png"));
		// return null;
	}

	@Override
	public String getTitle() {
		return "cy3sbml ";
	}
	
	public boolean isActive(){
		return (cytoPanelEast.getState() != CytoPanelState.HIDE);
	}

    public void activate(){
    	logger.info("Activate cy3sbml ControlPanel");
		// If the state of the cytoPanelWest is HIDE, show it
		if (cytoPanelEast.getState() == CytoPanelState.HIDE) {
			cytoPanelEast.setState(CytoPanelState.DOCK);
		}	

		// Select my panel
		select();
    }
		
	public void deactivate(){
		logger.info("Deactivate cy3sbml ControlPanel");
		// Test if still other Components in Control Panel, otherwise hide
		// the complete panel
		if (cytoPanelEast.getCytoPanelComponentCount() == 1){
			cytoPanelEast.setState(CytoPanelState.HIDE);
		}
	}

	public void select(){
		int index = cytoPanelEast.indexOfComponent(this);
		if (index == -1) {
			return;
		}
		cytoPanelEast.setSelectedIndex(index);
	}
		
	/////////////////// TEXT CONTENT ///////////////////////////////////
	public JEditorPaneSBML getTextPane(){
		return textPane;
	}
	
	/////////////////// HANDLE EVENTS ///////////////////////////////////
	
	/** Handle node selection events in the table/network. 
	 * The RowsSet event is quit broad (happens a lot in network generation and layout, so 
	 * make sure to minimize the unnecessary action here.
	 * I.e. only act on the Event if everything in the right state.
	 * 
	 * RowSetEvent:
	 * An Event object generated when an event occurs to a RowSet object. A RowSetEvent object is 
	 * generated when a single row in a rowset is changed, the whole rowset is changed, or the 
	 * rowset cursor moves.
	 * When an event occurs on a RowSet object, one of the RowSetListener methods will be sent 
	 * to all registered listeners to notify them of the event. An Event object is supplied to the 
	 * RowSetListener method so that the listener can use it to find out which RowSet object is 
	 * the source of the event.
	 * 
	 * http://chianti.ucsd.edu/cytoscape-3.2.1/API/org/cytoscape/model/package-summary.html
	 */ 
	public void handleEvent(RowsSetEvent e) {
		try {
		if (!isActive()){
			return;
		}
		CyNetwork network = adapter.cyApplicationManager.getCurrentNetwork();
		CyNetworkView view = adapter.cyApplicationManager.getCurrentNetworkView();
		if (network == null || view == null){
			return;
		}
		
		// TODO: get the information for the mapped SBML node
		// TODO: work with notify and notifyAll
		
		// TODO: this is performed multiple times due to 
		
		List<CyNode> selectedNodes = CyTableUtil.getNodesInState(network, CyNetwork.SELECTED, true);
		logger.info("--- SELECTION ---");
		for (CyNode n: selectedNodes){
			logger.info(n.getSUID().toString());
		}
		logger.info("-----------------");
		
		// TODO: Listen to network changed events and update the SBML
		SBMLDocument document = sbmlManager.getCurrentSBMLDocument();
		
		if (document != null){
			/*
			// Test the display of information
			Model model = document.getModel();
			HashSet<Object> nsbSet = new HashSet<Object>();
			for (Compartment c: model.getListOfCompartments()){
				nsbSet.add(c);
			}
			logger.info("Show information for compartment");		
			textPane.showNSBInfo(nsbSet);
			*/
			logger.info("Get selected SUIDs and NSBs");
			// TODO: !!! This has to be done when networks are changed/views selected
			// TODO: !!! Only here for testing. DO NOT CREATE TREE FOR EVERY LOOKUP.
			navigationTree = new NavigationTree(document);
			
			List<Long> selectedSUIDs = getSUIDsForSelectedNodes(network);
			List<String> selectedNSBIds = getNSBIds(selectedSUIDs);
		
			// much more complicated with tree which has to be synchronized
			// makeNetworkAndTreeChanges(selectedNodeIds, selectedNamedSBaseIds);
			if (selectedNSBIds.size() > 0){
				// Get the actual NamedSBase objects
				// only use the first in the row
				// TODO: better handling of multiple selections
				String nsbId = selectedNSBIds.get(0);
				NamedSBase nsb = navigationTree.getNamedSBaseById(nsbId);
				textPane.showNSBInfo(nsb);
			}
		}
		
		} catch (Throwable t){
			logger.error("Error in handling node selection in CyNetwork");
			t.printStackTrace();
		}
	}
	
	private LinkedList<Long> getSUIDsForSelectedNodes(CyNetwork network){
		List<CyNode> selectedNodes = CyTableUtil.getNodesInState(network, CyNetwork.SELECTED, true);
		LinkedList<Long> suids = new LinkedList<Long>();	
		for (CyNode node : selectedNodes){
			suids.add(node.getSUID());
		}
		return suids;
	}
	
	private List<Long> getSUIDs(List<String> NSBIds){ 
		One2ManyMapping<String, Long> mapping = sbmlManager.getCurrentNSB2CyNodeMapping();
		return mapping.getValues(NSBIds);
	}
	private List<String> getNSBIds(List<Long> suids){ 
		One2ManyMapping<Long, String> mapping = sbmlManager.getCurrentCyNode2NSBMapping();
		return mapping.getValues(suids);
	}
	

	// TODO: handle the focusing of CyNetworks
	/*
	public void propertyChange(PropertyChangeEvent e) {		
		if (e.getPropertyName().equalsIgnoreCase(CytoscapeDesktop.NETWORK_VIEW_FOCUSED))
		{	
			updateNavigationTree();
			
			//Change the target for the network Event Listener
			CyNetwork network = Cytoscape.getCurrentNetwork();
			if (network != null)
				network.removeSelectEventListener(this);
			network = Cytoscape.getCurrentNetwork();
			if (network != null) {
				network.addSelectEventListener(this);
			}
		}
		
		if (e.getPropertyName().equalsIgnoreCase(Cytoscape.NETWORK_DESTROYED)){
			String deletedNetworkKey = Cytoscape.getCurrentNetwork().getIdentifier();
			sbmlDocuments.removeDocument(deletedNetworkKey);
			updateNavigationTree();
		}
		if (e.getPropertyName().equalsIgnoreCase(Cytoscape.SESSION_LOADED)){
			sbmlDocuments.updateDocuments();
			updateNavigationTree();
		}
	}
	*/
	

	/** Handle hyperlink events in the info TextPane. */
	public void hyperlinkUpdate(HyperlinkEvent evt) {
		/* Open link in browser. */
		URL url = evt.getURL();
		if (url != null) {
			if (evt.getEventType() == HyperlinkEvent.EventType.ENTERED) {
				
			} else if (evt.getEventType() == HyperlinkEvent.EventType.EXITED) {
				
			} else if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				adapter.openBrowser.openURL(url.toString());
			}
		}	
	}

}