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
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.model.events.RowSetRecord;
import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.view.model.CyNetworkView;
import org.sbml.jsbml.SBMLDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Control Panel for cy3sbml.
 */
public class ControlPanel extends JPanel implements CytoPanelComponent, HyperlinkListener, RowsSetListener{
	private static final Logger logger = LoggerFactory.getLogger(ControlPanel.class);
	private static final long serialVersionUID = 1L;

	private static ControlPanel uniqueInstance;
	
	private ServiceAdapter adapter;
	private NavigationTree navigationTree;
	private SBMLManager sbmlManager;
	
	private JTree sbmlTree;
	private JEditorPane textPane;

	private boolean active = false;	

	// TODO: better to refactor the information panel things -> put in separate class
	private long lastInformationThreadId = -1;
	
	
	public static synchronized ControlPanel getInstance(ServiceAdapter adapter){
		if (uniqueInstance == null){
			logger.info("ControlPanel created");
			uniqueInstance = new ControlPanel(adapter);
		}
		return uniqueInstance;
	}
	

	private ControlPanel(ServiceAdapter adapter){
		/** Construct the Navigation panel for cy3sbml. */
		this.adapter = adapter;
		this.sbmlManager = SBMLManager.getInstance();
		this.navigationTree = new NavigationTree();
		
		setLayout(new BorderLayout(0, 0));
		
		// --- Annotation Area ------------------
		textPane = new JEditorPane();
		textPane.setEditable(false);
		textPane.addHyperlinkListener(this);
		textPane.setFont(new Font("Dialog", Font.PLAIN, 11));
		textPane.setContentType("text/html");
		this.setHelp();
		
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
	
	/////////////////// CYTO PANEL COMPONENT ///////////////////////////////////
	
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
		// TODO add the icon
		return null;
	}

	@Override
	public String getTitle() {
		return "cy3sbml";
	}
	
	// TODO: refactor
	/*
    public void activate(){
		CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
		int index = cytoPanel.indexOfComponent(CySBML.NAME);
		if (index == -1){
			cytoPanel.add(CySBML.NAME, this);
			cytoPanel.setState(CytoPanelState.DOCK);
		}
		selectNavigationPanel();
		active = true;
	}
	
	public void deactivate(){
		CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
		int index = cytoPanel.indexOfComponent(CySBML.NAME);
		if (index != -1){
			cytoPanel.remove(index);
		}
		// Test if still other Components, otherwise hide
		if (cytoPanel.getCytoPanelComponentCount() == 0){
			cytoPanel.setState(CytoPanelState.HIDE);
		}
		active = false;
	}

	public boolean isActive(){
		return active;
	}
	
	public static void selectNavigationPanel(){
		CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
		cytoPanel.setSelectedIndex(cytoPanel.indexOfComponent(CySBML.NAME));
	}
	*/
		
	/////////////////// TEXT CONTENT ///////////////////////////////////
	public JEditorPane getTextPane(){
		return textPane;
	}
		
	public void setText(String text){
		textPane.setText(text);
	}
		
	public void setHelp(){
		try {
			logger.info("set help in control panel");
			URL url = new URL(ControlPanel.class.getResource("/info.html").toString());
			logger.info(url.toString());
			textPane.setPage(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public long getLastInfoThreadId(){
		return lastInformationThreadId;
	}
	
	/////////////////// HANDLE EVENTS ///////////////////////////////////
	
	// http://chianti.ucsd.edu/cytoscape-3.2.1/API/org/cytoscape/model/package-summary.html
	/** Handle node selection events in the table/network. */ 
	public void handleEvent(RowsSetEvent e) {
		try {
			
		CyNetwork network = adapter.cyApplicationManager.getCurrentNetwork();
		CyNetworkView view = adapter.cyApplicationManager.getCurrentNetworkView();
		
		Collection<RowSetRecord> rowsSet = e.getColumnRecords(CyNetwork.SELECTED);
		for (RowSetRecord record: rowsSet) {
			// Get the row that was set
			CyRow row = record.getRow(); 
			// What it was set to
			boolean selected = ((Boolean)record.getValue()).booleanValue();  
			CyNode node = network.getNode(row.get(CyIdentifiable.SUID, Long.class));
			
			// Do the test action
			if (selected){
				textPane.setText("Node selection event: (" + node.getSUID().toString() + ")");
			} else {
				textPane.setText("Unselected event");
			}
			
			// If not active or no associated SBMLDocument do nothing
			// TODO: check active state and manage active state
			// if (!active){
			// return;
			//
			
			// TODO: get the information for the mapped SBML node
			// TODO: work with notify and notifyAll
		}
		logger.info("Get selected SUIDs and NSBs");

		SBMLDocument document = sbmlManager.getCurrentSBMLDocument();
		if (document != null){
			navigationTree = new NavigationTree(document);
		
			// TODO: !!! This has to be done when networks are changed/views selected
			// TODO: !!! Only here for testing. DO NOT CREATE TREE FOR EVERY LOOKUP.
			List<Long> selectedSUIDs = getSUIDsForSelectedNodes(network);
			List<String> selectedNSBIds = getNSBIds(selectedSUIDs);
		
			// much more complicated with tree which has to be synchronized
			// makeNetworkAndTreeChanges(selectedNodeIds, selectedNamedSBaseIds);
			updateAnnotationInformation(selectedNSBIds);
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
	
	

	/////////////////// MIRIAM INFORMATION /////////////////////////////
	@Deprecated
	public void updateAnnotationInformation(List<String> ids){
		if (ids.size() > 0){
			String nsbId = ids.get(0);
			showNodeObjectInfo(navigationTree.getNamedSBaseById(nsbId));
		}
	}
	
   /** Create the information string for the SBML Node and display in the textPane. */ 
	private void showNodeObjectInfo(Object obj) {
	   Set<Object> objSet = new HashSet<Object>();
	   objSet.add(obj);
	   showNodesObjectInfo(objSet);
   }
   
   private void showNodesObjectInfo(Set<Object> objSet) {
	   NamedSBaseInfoThread thread = new NamedSBaseInfoThread(objSet, this);
	   lastInformationThreadId = thread.getId();
	   thread.start();
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
