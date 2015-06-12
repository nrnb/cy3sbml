package org.cy3sbml.mapping;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class One2ManyMappingTest {

	One2ManyMapping<String, Long> map;
	
	@Before
	public void setUp(){
		map = new One2ManyMapping<String, Long>();
	}
	
	@After
	public void tearDown(){
		map = null;
	}
	
	@Test
	public void testContainsKey() {
		map.put("id1", new Long(10));
		assertTrue(map.containsKey("id1"));
		assertFalse(map.containsKey("id2"));
	}

	@Test
	public void testKeySet() {
		map.put("id1", new Long(10));
		map.put("id2", new Long(20));
		Set<String> keys = map.keySet();
		assertEquals(keys.size(), 2);
		assertTrue(keys.contains("id1"));
		assertTrue(keys.contains("id2"));
	}

	@Test
	public void testPut() {
		map.put("id1", new Long(10));
		assertTrue(map.containsKey("id1"));
		assertEquals(map.keySet().size(), 1);
	}

	@Test
	public void testGetValues() {
		map.put("id1", new Long(10));
		map.put("id1", new Long(20));
		map.put("id1", new Long(30));
		List<Long> values = map.getValues("id1");
		assertEquals(values.size(), 3);
		values = map.getValues("id2");
		assertEquals(values.size(), 0);
	}

	@Test
	public void testGetValuesListOf() {
		map.put("id1", new Long(10));
		map.put("id1", new Long(20));
		map.put("id1", new Long(30));
		map.put("id2", new Long(-10));
		map.put("id2", new Long(-20));
		map.put("id2", new Long(-30));
		
		List<String> keys = new LinkedList<String>();
		keys.add("id1");
		keys.add("id2");
		
		List<Long> values = map.getValues(keys);
		assertEquals(values.size(), 6);
	}

	@Test
	public void testCreateReverseMapping() {
		map.put("id1", new Long(10));
		map.put("id1", new Long(20));
		One2ManyMapping<Long, String> revMap = map.createReverseMapping();
		assertTrue(revMap.containsKey(new Long(10)));
		assertTrue(revMap.containsKey(new Long(20)));
	}

}