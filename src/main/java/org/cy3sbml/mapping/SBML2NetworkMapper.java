package org.cy3sbml.mapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.sbml.jsbml.SBMLDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** Here the mapping between SBML documents and Cytoscape networks is stored.
 * When an SBML is read and networks are created, than the respective 
 * SBMLDocument is added to the Mapping for all created networks.
 * Networks are identified via their network suids.
 * 
 * This implements only the mapping store. 
 * The update of the current status has to be managed by the SBMLManager via listening
 * to NetworkSelected events.
 */
public class SBML2NetworkMapper {
	private static final Logger logger = LoggerFactory.getLogger(SBML2NetworkMapper.class);
	
	/* Currently used SBMLdocument and mapping information. */
	private Long currentSUID;
	
	/* Store of all SBMLdocuments and mapping information. */
	private Map<Long, SBMLDocument> documentMap;
	private Map<Long, One2ManyMapping<String, Long>> NSBToNodeMappingMap;
	private Map<Long, One2ManyMapping<Long, String>> nodeToNSBMappingMap;
	
	public SBML2NetworkMapper(){
		logger.info("SBML2NetworkMapper created");
		initMaps();
		initCurrent();
	}
	
	private void initCurrent(){
		currentSUID = null;
	}
	
	private void initMaps(){
		documentMap = new HashMap<Long, SBMLDocument>();
		NSBToNodeMappingMap = new HashMap<Long, One2ManyMapping<String, Long>>();
		nodeToNSBMappingMap = new HashMap<Long, One2ManyMapping<Long, String>>();
	}
	
	/** Updates the current SBMLDocument and mappings.
	 * This function has to be triggered to update the underlying mappings
	 * and SBMLDocuments used for display.
	 */
	public void setCurrentSUID(Long suid){
		if (suid != null && documentMap.containsKey(suid)){
			//? Problem with documentMap
			currentSUID = suid;
		} else {
			initCurrent();
		}
		logger.info("Current network set to: " + currentSUID);
	}
	
	public Set<Long> keySet(){
		return documentMap.keySet();
	}
	
	public void putDocument(Long suid, SBMLDocument doc, NamedSBase2CyNodeMapping mapping){
		documentMap.put(suid,  doc);
		NSBToNodeMappingMap.put(suid, mapping);
		nodeToNSBMappingMap.put(suid, mapping.createReverseMapping());
		logger.info("Network put: " + suid.toString());
	}
	
	public void removeDocument(Long deletedNetworkSUID){
		logger.info("Network remove:" + deletedNetworkSUID.toString());
		if (currentSUID == deletedNetworkSUID){
			initCurrent();
		}
		documentMap.remove(deletedNetworkSUID);
		NSBToNodeMappingMap.remove(deletedNetworkSUID);
		nodeToNSBMappingMap.remove(deletedNetworkSUID);
	}
	
	public Long getCurrentSUID(){
		return currentSUID;
	}
	
	public SBMLDocument getCurrentDocument(){
		if (currentSUID == null){
			logger.warn("No current SUID set. SBMLDocument can not be retrieved !");
			return null;
		} else {
			return documentMap.get(currentSUID);	
		}
	}
	
	public One2ManyMapping<Long, String> getCurrentCyNode2NSBMapping(){
		if (currentSUID == null){
			logger.warn("No current SUID set. Mapping can not be retrieved !");
			return null;
		} else {
			return nodeToNSBMappingMap.get(currentSUID);	
		}
	}
	
	public One2ManyMapping<String, Long> getCurrentNSBToCyNodeMapping(){
		if (currentSUID == null){
			logger.warn("No current SUID set. Mapping can not be retrieved !");
			return null;
		} else {
			return NSBToNodeMappingMap.get(currentSUID);	
		}
	}
	
	public SBMLDocument getDocumentForSUID(Long suid){
		SBMLDocument doc = null;
		if (documentMap.containsKey(suid)){
			doc = documentMap.get(suid);
		}
		return doc;
	}
	
	public Map<Long, SBMLDocument> getDocumentMap(){
		return documentMap;
	}
	
	public String toString(){
		String info = "\n--- SBML2NetworkMapping ---\n";
		for (Long key: documentMap.keySet()){
			info += String.format("%s -> %s\n", key.toString(), documentMap.get(key).toString());
		}
		info += "-------------------------------";
		return info;
	}
}