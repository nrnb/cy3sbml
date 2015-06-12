package org.cy3sbml.miriam;

import static org.junit.Assert.*;

import org.cy3sbml.ConnectionProxy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.miriam.lib.MiriamLink;

public class MiriamWebserviceTest {
	MiriamLink link;
	
	@Before
	public void setUp() {
		// If the tests fail, uncomment the following line
		// Necessary to pass through university proxy
		ConnectionProxy.setSystemProxy("proxy.charite.de", "8080");
		link = MiriamWebservice.getMiriamLink();
	}
	 
	@After
	public void tearDown() {
		link = null;
	}
	
	@Test
	public void testGetMiriamLink() {	
		assertNotNull(link);
	}
	
	@Test
	public void testGetChEBI() {
		String dataType = link.getDataTypeURI("ChEBI");
		assertNotNull(dataType);
	}
}