//package org.geopublishing.atlasStyler;
//
//import java.awt.Color;
//import java.io.IOException;
//import java.util.List;
//import java.util.TreeSet;
//
//import javax.xml.transform.TransformerException;
//
//import org.geotools.data.simple.*;
//import org.geopublishing.atlasStyler.classification.CLASSIFICATION_METHOD;
//import org.geopublishing.atlasStyler.classification.FeatureClassification;
//import org.geopublishing.atlasStyler.rulesLists.AbstractRulesList.RulesListType;
//import org.geopublishing.atlasStyler.rulesLists.GraduatedColorPointRuleList;
//import org.geopublishing.atlasStyler.swing.AsTestingUtil;
//import org.geotools.feature.DefaultFeatureCollections;
//import org.geotools.feature.SchemaException;
//import org.geotools.feature.simple.SimpleFeatureBuilder;
//import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
//import org.geotools.styling.FeatureTypeStyle;
//import org.geotools.styling.Rule;
//import org.geotools.styling.Style;
//import org.junit.Before;
//import org.junit.Test;
//
//import com.vividsolutions.jts.geom.Point;
//
//import de.schmitzm.geotools.GTUtil;
//import de.schmitzm.geotools.styling.StyledFeatureCollection;
//import de.schmitzm.geotools.styling.StylingUtil;
//import de.schmitzm.geotools.testing.GTTestingUtil;
//import de.schmitzm.testing.TestingClass;
//
//public class QuantitiesRuleListTest extends TestingClass {
//	SimpleFeatureCollection features;
//
//	@Before
//	public void beforeTests() {
//		features = DefaultFeatureCollections.newCollection();
//		SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
//		tb.setName("Dummy");
//		tb.add("the_geom", Point.class);
//		tb.add("allsame", Integer.class);
//		tb.add("random", Double.class);
//
//		SimpleFeatureBuilder b = new SimpleFeatureBuilder(tb.buildFeatureType());
//
//		for (int i = 0; i < 100; i++) {
//			features.add(b.buildFeature(null,
//					new Object[] { null, 111, Math.random() }));
//		}
//	}
//
//	@Test
//	public void testNormaleQuantiles() throws SchemaException, IOException,
//			InterruptedException, TransformerException {
//		StyledFeatureCollection sfc = new StyledFeatureCollection(features,
//				"someId", "titel", (Style) null);
//
//		GraduatedColorPointRuleList ruleList = new GraduatedColorPointRuleList(
//				sfc, true);
//
//		FeatureClassification quantitiesClassification = new FeatureClassification(
//				sfc, "random");
//		quantitiesClassification.setRecalcAutomatically(false);
//		quantitiesClassification.setMethod(CLASSIFICATION_METHOD.QUANTILES);
//		quantitiesClassification.setNumClasses(5);
//		quantitiesClassification.calculateClassLimitsBlocking();
//
//		TreeSet<Double> classLimits = quantitiesClassification.getClassLimits();
//
//		assertEquals(6, classLimits.size());
//
//		ruleList.setClassLimits(classLimits);
//		List<Rule> rules = ruleList.getRules();
//		// +1 NODATA rule
//		assertEquals(5 + 1, rules.size());
//		assertTrue(rules.get(0).getTitle().contains(" - "));
//
//		assertTrue(StylingUtil.validates(ruleList.getFTS()));
//
//	}
//
//	/**
//	 * Creates an QuantileClassifier and runs it on data that just has the same
//	 * values. So no classes can be calculated. Then we set it to the rulelist,
//	 * and we expect the RL it to deal with it nicely.
//	 * 
//	 * @throws TransformerException
//	 */
//	@Test
//	public void testQuantilesWhereTheyCannotBeCreated() throws SchemaException,
//			IOException, InterruptedException, TransformerException {
//		StyledFeatureCollection sfc = new StyledFeatureCollection(features,
//				"someId", "titel", (Style) null);
//
//		GraduatedColorPointRuleList ruleList = new GraduatedColorPointRuleList(
//				sfc, true);
//
//		FeatureClassification quantitiesClassification = new FeatureClassification(
//				sfc, "allsame");
//		quantitiesClassification.setRecalcAutomatically(false);
//		quantitiesClassification.setMethod(CLASSIFICATION_METHOD.QUANTILES);
//		quantitiesClassification.setNumClasses(5);
//		quantitiesClassification.calculateClassLimitsBlocking();
//		TreeSet<Double> classLimits = quantitiesClassification.getClassLimits();
//
//		assertEquals(1, classLimits.size());
//
//		ruleList.setClassLimits(classLimits, true);
//		List<Rule> rules = ruleList.getRules();
//		// 1 NODATA rule and one rule that just fits the only value available
//		assertEquals(1 + 1, rules.size());
//		assertEquals("111.0",
//				GTUtil.descriptionTitle(rules.get(0).getDescription()));
//
//		assertTrue(StylingUtil.validates(ruleList.getFTS()));
//
//	}
//
//	@Test
//	public void testImportSld_14() throws IOException, TransformerException {
//		AtlasStylerVector as = new AtlasStylerVector(
//				GTTestingUtil.TestDatasetsVector.arabicInHeader
//						.getFeatureSource());
//
//		as.importStyle(AsTestingUtil.TestDatasetsSld.textRulesDefaultLocalizedPre16
//				.getStyle());
//
//		assertTrue(as.getLastChangedRuleList() instanceof QuantitiesRuleList);
//		QuantitiesRuleList colorRl = (QuantitiesRuleList) as
//				.getLastChangedRuleList();
//
//		List<FeatureTypeStyle> featureTypeStyles = as.getStyle()
//				.featureTypeStyles();
//
//		assertEquals(2, featureTypeStyles.size());
//
//		// Text rules imported:
//		FeatureTypeStyle textFs = featureTypeStyles.get(1);
//		List<Rule> textRs = textFs.rules();
//		assertEquals(3, textRs.size());
//
//		// Colors!
//		FeatureTypeStyle colorsFs = featureTypeStyles.get(0);
//		List<Rule> colorsRs = colorsFs.rules();
//		assertEquals(7, colorsRs.size());
//
//		assertEquals(0.0, colorRl.getClassLimits().first(), 0.1);
//		assertEquals(35000., colorRl.getClassLimits().last(), 0.1);
//
//		// CHeck colors
//		assertEquals(new Color(17, 17, 17), colorRl.getColors()[0]);
//		assertEquals(new Color(252, 141, 89), colorRl.getColors()[1]);
//		assertEquals(new Color(254, 224, 139), colorRl.getColors()[2]);
//
//		assertEquals(CLASSIFICATION_METHOD.MANUAL, colorRl.getMethod());
//
//		assertEquals("en{- 3 MBps/billion capita}", colorRl.getRuleTitles()
//				.get(0));
//
//		assertEquals(RulesListType.QUANTITIES_COLORIZED_POLYGON,
//				colorRl.getType());
//		assertEquals("SURFACE", colorRl.getValue_field_name());
//		assertEquals(null, colorRl.getNormalizer_field_name());
//		// Just [ SURFACE IS NULL ] is not a valid OR, so its doubled
//		assertEquals(
//				"[[ ALL_LABEL_CLASSES_ENABLED = ALL_LABEL_CLASSES_ENABLED ] AND [[ SURFACE IS NULL ] OR [ SURFACE IS NULL ]]]",
//				colorRl.getNoDataFilter().toString());
//
//		assertTrue(StylingUtil.validates(colorRl.getFTS()));
//
//	}
//}
