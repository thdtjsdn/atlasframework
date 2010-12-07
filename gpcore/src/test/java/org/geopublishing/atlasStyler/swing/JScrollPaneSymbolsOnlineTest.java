package org.geopublishing.atlasStyler.swing;

import org.junit.Test;

import schmitzm.geotools.feature.FeatureUtil.GeometryForm;
import schmitzm.junit.TestingClass;
import schmitzm.swing.TestingUtil;
public class JScrollPaneSymbolsOnlineTest extends TestingClass {

	@Test
	public void testJScrollPaneSymbols() throws Throwable {

		if (TestingUtil.isInteractive()) {
			TestingUtil.testGui(
					new JScrollPaneSymbolsOnline(GeometryForm.POINT), 10);

			TestingUtil.testGui(
					new JScrollPaneSymbolsOnline(GeometryForm.LINE), 10);

			TestingUtil.testGui(new JScrollPaneSymbolsOnline(
					GeometryForm.POLYGON), 10);
		}

	}

}
