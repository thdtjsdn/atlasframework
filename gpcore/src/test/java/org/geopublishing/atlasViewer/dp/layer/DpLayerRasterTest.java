package org.geopublishing.atlasViewer.dp.layer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.net.URL;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.DataUtilities;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.map.DefaultMapLayer;
import org.geotools.styling.Style;
import org.junit.Ignore;
import org.junit.Test;

import schmitzm.geotools.grid.GridUtil;
import schmitzm.geotools.io.GeoImportUtil;
import schmitzm.geotools.io.GeoImportUtil.ARCASCII_IMPORT_TYPE;
import schmitzm.geotools.styling.StylingUtil;
import schmitzm.swing.TestingUtil;
import skrueger.geotools.io.GeoImportUtilURL;

public class DpLayerRasterTest {

	private static final String RASTER_GEOTIFF_WITH_SLD__SLDLOCATION = "/rasterGeotiffWithSLD/geotiffwithsld.sld";
	private static final Style RASTER_GEOTIFF_WITH_SLD__SLDSTYLE = StylingUtil
			.loadSLD(DpLayerRasterTest.class
					.getResource(RASTER_GEOTIFF_WITH_SLD__SLDLOCATION))[0];
	private static final String RASTER_GEOTIFF_WITH_SLD__TIFLOCATION = "/rasterGeotiffWithSLD/geotiffwithsld.tif";
	private static final URL RASTER_GEOTIFF_WITH_SLD__URL = DpLayerRasterTest.class
			.getResource(RASTER_GEOTIFF_WITH_SLD__TIFLOCATION);

	private static final String RASTER_GEOTIFF_RGBONLY__TIFLOCATION = "/rasterGeotiffRGBWithoutSLD/geotiff_rgb_ohnesld.tif";
	private static final URL RASTER_GEOTIFF_RGBONLY__URL = DpLayerRasterTest.class
			.getResource(RASTER_GEOTIFF_RGBONLY__TIFLOCATION);

	private static final String RASTER_AAIGRID_WITH_SLD__SLDLOCATION = "/rasterASCIIWithSLDTransparency/asciiWithSldTransparency.sld";
	private static final Style RASTER_AAIGRID_WITH_SLD__SLDSTYLE = StylingUtil
			.loadSLD(DpLayerRasterTest.class
					.getResource(RASTER_AAIGRID_WITH_SLD__SLDLOCATION))[0];
	private static final String RASTER_AAIGRID_WITH_SLD__ASCII_LOCATION = "/rasterASCIIWithSLDTransparency/asciiWithSldTransparency.asc";
	private static final URL RASTER_AAIGRID_WITH_SLD__URL = DpLayerRasterTest.class
			.getResource(RASTER_AAIGRID_WITH_SLD__ASCII_LOCATION);

	@Test
	public void testJustColorsGeotiffRGB_GeoTiffReaderWithFileObject()
			throws Throwable {
		URL url = RASTER_GEOTIFF_RGBONLY__URL;
		GeoTiffReader gc = new GeoTiffReader(DataUtilities.urlToFile(url));
		assertNotNull(gc);

		Style createDefaultStyle = GridUtil.createDefaultStyle();
		DefaultMapLayer mlayer = new DefaultMapLayer(gc, createDefaultStyle);

		checkMapLayer_GEOTIFF_RGB(mlayer);

		gc.dispose();
	}

	@Test
	public void testTransparencyOfGeotiffWithSLD_GeoTiffReaderWithFileObject()
			throws Throwable {
		URL url = RASTER_GEOTIFF_WITH_SLD__URL;
		GeoTiffReader gc = new GeoTiffReader(DataUtilities.urlToFile(url));
		assertNotNull(gc);

		DefaultMapLayer mlayer = new DefaultMapLayer(gc,
				RASTER_GEOTIFF_WITH_SLD__SLDSTYLE);

		checkMapLayer_GEOTIFF_WITH_SLD(mlayer);

		gc.dispose();
	}

	@Test
	public void testTransparencyOfGeotiffWithSLD_GeoTiffReaderWithURL2FileObject()
			throws Throwable {
		URL url = RASTER_GEOTIFF_WITH_SLD__URL;
		GeoTiffReader gc = new GeoTiffReader(url);
		assertNotNull(gc);

		DefaultMapLayer mlayer = new DefaultMapLayer(gc,
				RASTER_GEOTIFF_WITH_SLD__SLDSTYLE);

		checkMapLayer_GEOTIFF_WITH_SLD(mlayer);

		gc.dispose();
	}

	@Test
//	@Ignore
	public void testTransparencyOfAAIGridWithSLD_AAIGridReaderWithFileObject_USE_ARCGRIDREADER()
			throws Throwable {
		GeoImportUtilURL
				.setAsciiRasterImportMode(ARCASCII_IMPORT_TYPE.USE_ARCGRIDREADER);
		GridCoverage2D gc = GeoImportUtil
				.readGridFromArcInfoASCII(DataUtilities
						.urlToFile(RASTER_AAIGRID_WITH_SLD__URL));
		assertNotNull(gc);

		DefaultMapLayer mlayer = new DefaultMapLayer(gc,
				RASTER_AAIGRID_WITH_SLD__SLDSTYLE);

		checkMapLayer_AAIGrid_Transparency_with_Sld(mlayer);

		gc.dispose(true);
	}

	@Test
//	@Ignore
	public void testTransparencyOfAAIGridWithSLD_AAIGridReaderWithURL2FileObject_USE_ARCGRIDREADER()
			throws Throwable {
		GeoImportUtilURL
				.setAsciiRasterImportMode(ARCASCII_IMPORT_TYPE.USE_ARCGRIDREADER);
		GridCoverage2D gc = GeoImportUtilURL
				.readGridFromArcInfoASCII(RASTER_AAIGRID_WITH_SLD__URL);
		assertNotNull(gc);

		DefaultMapLayer mlayer = new DefaultMapLayer(gc,
				RASTER_AAIGRID_WITH_SLD__SLDSTYLE);

		checkMapLayer_AAIGrid_Transparency_with_Sld(mlayer);

		gc.dispose(true);
	}

	@Test
	public void testTransparencyOfAAIGridWithSLD_AAIGridReaderWithFileObject_USE_ARCGRIDRASTER()
			throws Throwable {
		GeoImportUtilURL
				.setAsciiRasterImportMode(ARCASCII_IMPORT_TYPE.USE_ARCGRIDRASTER);
		GridCoverage2D gc = GeoImportUtil
				.readGridFromArcInfoASCII(DataUtilities
						.urlToFile(RASTER_AAIGRID_WITH_SLD__URL));
		assertNotNull(gc);

		DefaultMapLayer mlayer = new DefaultMapLayer(gc,
				RASTER_AAIGRID_WITH_SLD__SLDSTYLE);

		checkMapLayer_AAIGrid_Transparency_with_Sld(mlayer);

		gc.dispose(true);
	}

	@Test
	public void testTransparencyOfAAIGridWithSLD_AAIGridReaderWithURL2FileObject_USE_ARCGRIDRASTER()
			throws Throwable {
		GeoImportUtilURL
				.setAsciiRasterImportMode(ARCASCII_IMPORT_TYPE.USE_ARCGRIDRASTER);
		GridCoverage2D gc = GeoImportUtilURL
				.readGridFromArcInfoASCII(RASTER_AAIGRID_WITH_SLD__URL);
		assertNotNull(gc);

		DefaultMapLayer mlayer = new DefaultMapLayer(gc,
				RASTER_AAIGRID_WITH_SLD__SLDSTYLE);

		checkMapLayer_AAIGrid_Transparency_with_Sld(mlayer);

		gc.dispose(true);
	}

	private void checkMapLayer_AAIGrid_Transparency_with_Sld(
			DefaultMapLayer mlayer) throws Throwable {
		BufferedImage bi = TestingUtil.visualize(mlayer);

		assertTrue("Some blue color at 20/30",
				TestingUtil.checkPixel(bi, 20, 30, 129, 180, 217, 255));
		assertTrue("Some blue color at 24/34",
				TestingUtil.checkPixel(bi, 24, 34, 124, 175, 215, 255));
		assertTrue("Transparency at 10/10",
				TestingUtil.checkPixel(bi, 10, 10, 0, 0, 0, 0));
	}

	public static void checkMapLayer_GEOTIFF_RGB(DefaultMapLayer mlayer)
			throws Throwable {
		BufferedImage bi = TestingUtil.visualize(mlayer);

		assertTrue("Some specific color at 50/50",
				TestingUtil.checkPixel(bi, 50, 50, 126, 221, 42, 255));
		assertTrue("Some red color at 1/1",
				TestingUtil.checkPixel(bi, 1, 1, 230, 76, 0, 255));
	}

	public static void checkMapLayer_GEOTIFF_WITH_SLD(DefaultMapLayer mlayer)
			throws Throwable {
		BufferedImage bi = TestingUtil.visualize(mlayer);

		assertTrue("Some specific color at 50/50",
				TestingUtil.checkPixel(bi, 50, 50, 161, 125, 74, 255));
		assertTrue("Transparent in the top left corner",
				TestingUtil.checkPixel(bi, 1, 1, 0, 0, 0, 0));
	}
}
