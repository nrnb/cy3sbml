package org.cy3sbml.miriam;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.ext.qual.QualConstants;
import org.sbml.jsbml.ext.qual.QualModelPlugin;
import org.cy3sbml.gui.JEditorPaneSBML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Generates information for web resources in separate thread. */
public class NamedSBaseInfoThread extends Thread{
	private static final Logger logger = LoggerFactory.getLogger(NamedSBaseInfoThread.class);
	
	Collection<Object> objSet;
	JEditorPaneSBML textPane;
	public String info;
	   
    public NamedSBaseInfoThread(Collection<Object> objSet, JEditorPaneSBML textPane) {
        this.objSet = objSet;
        this.textPane = textPane;
        info = "";
    }

    public void run() {
    	if (textPane != null){
    		// Info creating mode
    		for (Object obj : objSet){	
    			NamedSBaseInfoFactory infoFac = new NamedSBaseInfoFactory(obj);
    			try {
					infoFac.createInfo();
				} catch (XMLStreamException e) {
					logger.error("Creating info for object failed");
					e.printStackTrace();
				}
    			info += infoFac.getInfo();
    			textPane.updateText(this);
    		}
    	} else {
    		// Cache filling mode
    		for (Object obj : objSet){	
    			NamedSBaseInfoFactory infoFac = new NamedSBaseInfoFactory(obj);
    			infoFac.cacheMiriamInformation();
    		}
    	}
    }
        
	/** Reads the annotation information in the Miriam Cash 
	 * TODO: full set of preloads (fbc, core), do during miriam refactoring 
	 */
	public static void preloadAnnotationsForSBMLDocument(SBMLDocument document){
		Model model = document.getModel();
		logger.debug("Preload Miriam for <compartments>");
		preloadAnnotationForListOf(model.getListOfCompartments());
		logger.debug("Preload Miriam for <species>");
		preloadAnnotationForListOf(model.getListOfSpecies());
		logger.debug("Preload Miriam for <reactions>");
		preloadAnnotationForListOf(model.getListOfReactions());
		
		QualModelPlugin qModel = (QualModelPlugin) model.getExtension(QualConstants.namespaceURI);
		if (qModel != null){
			logger.debug("Preload Miriam for <qualitativeSpecies>");
			preloadAnnotationForListOf(qModel.getListOfQualitativeSpecies());
			logger.debug("Preload Miriam for <transitions>");
			preloadAnnotationForListOf(qModel.getListOfTransitions());
		}
	}
	
	private static void preloadAnnotationForListOf(@SuppressWarnings("rawtypes") ListOf list){
		Set<Object> nsbSet = new HashSet<Object>();
		for (Object nsb: list){
			nsbSet.add(nsb);
		}
		NamedSBaseInfoThread thread = new NamedSBaseInfoThread((Collection<Object>) nsbSet, null);
		thread.start();
	}
}