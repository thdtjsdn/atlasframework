package org.geopublishing.atlasStyler;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Set;

import org.geopublishing.atlasStyler.rulesLists.UniqueValuesRuleList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.schmitzm.geotools.testing.GTTestingUtil;
import de.schmitzm.testing.TestingClass;
public class UniqueValuesRuleListTest extends TestingClass {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testUniqueValues() throws IOException {

		AtlasStylerVector atlasStyler = new AtlasStylerVector(
				GTTestingUtil.TestDatasetsVector.kreise.getFeatureSource());
		final UniqueValuesRuleList rl = atlasStyler.getRlf()
				.createUniqueValuesPolygonRulesList(true);

		assertEquals(0, rl.getValues().size());
		Set<Object> newUniques = rl.getAllUniqueValuesThatAreNotYetIncluded();
		assertEquals(3, newUniques.size());

		rl.addUniqueValue(newUniques.iterator().next());

		assertEquals(1, rl.getValues().size());
		assertEquals(2, rl.getAllUniqueValuesThatAreNotYetIncluded().size());
	}
}
