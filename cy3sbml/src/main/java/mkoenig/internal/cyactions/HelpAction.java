package cysbml.cyactions;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Action;
import javax.swing.ImageIcon;

import cytoscape.util.CytoscapeAction;
import cytoscape.util.OpenBrowser;

import mkoenig.internal.CyActivator


/** Opens the Help in a Browser. */
@SuppressWarnings("serial")
public class HelpAction extends AbstractCyAction {
	public static final String HELP_URL = "http://www.charite.de/sysbio/people/koenig/software/cysbml/index.html";
	
	
    public HelpAction(CySwingApplication cySwingApplication) {
    	
    	// set icon
    	ImageIcon icon = new ImageIcon(getClass().getResource("/images/tiger.jpg"));
    	putValue(LARGE_ICON_KEY, icon);
    	// set description
    	putValue(Action.SHORT_DESCRIPTION, CyActivator.NAME + " Help");
    }
    
    /** This method is called when the user selects the menu item.*/
    public void actionPerformed(ActionEvent ae) {
    	URL url;
		try {
			url = new URL(HELP_URL);
			OpenBrowser.openURL(url.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    }
    
	public boolean isInToolBar() {
		return true;
	}
	public boolean isInMenuBar() {
		return false;
	}
}