package org.cy3sbml.actions;

import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;

import org.cy3sbml.gui.ResultsPanel;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeStateAction extends AbstractCyAction{
	private static final Logger logger = LoggerFactory.getLogger(ChangeStateAction.class);
	private static final long serialVersionUID = 1L;
	
	public ChangeStateAction(CySwingApplication cySwingApplication){
		super("ChangeStateAction");
		
		ImageIcon icon = new ImageIcon(getClass().getResource("/images/changestate.png"));
		putValue(LARGE_ICON_KEY, icon);
		
		this.putValue(SHORT_DESCRIPTION, "show/hide cy3sbml");
		setToolbarGravity((float) 90.0);
	}
		
	public boolean isInToolBar() {
		return true;
	}
	public boolean isInMenuBar() {
		return false;
	}
		
	@Override
	public void actionPerformed(ActionEvent event) {
		logger.debug("actionPerformed");
		ResultsPanel panel = ResultsPanel.getInstance();
		panel.changeState();
	}
}
