package org.geopublishing.atlasStyler.rulesLists;

import javax.swing.JComponent;

import org.geotools.styling.ColorMap;

import de.schmitzm.geotools.styling.StyledRasterInterface;

public class RasterRulesList_Ramps extends RasterRulesListColormap {

	public RasterRulesList_Ramps(StyledRasterInterface<?> styledRaster,
			boolean withDefaults) {
		super(RulesListType.RASTER_COLORMAP_RAMPS, styledRaster,
				ColorMap.TYPE_RAMP);
	}

	@Override
	protected void test(int numClasses) {
		// TODO Auto-generated method stub

	}

	@Override
	public void applyOpacity() {
		// TODO Auto-generated method stub

	}

	@Override
	public void importColorMap(ColorMap cm) {
		// TODO Auto-generated method stub

	}

	@Override
	public void applyPalette(JComponent parentGui) {
		// TODO Auto-generated method stub

	}

}
