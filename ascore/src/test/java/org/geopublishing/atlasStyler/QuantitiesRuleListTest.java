package org.geopublishing.atlasStyler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

import org.geopublishing.atlasStyler.classification.QuantitiesClassification;
import org.geopublishing.atlasStyler.classification.QuantitiesClassification.METHOD;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import skrueger.geotools.StyledFeatureCollection;

import com.vividsolutions.jts.geom.Point;

public class QuantitiesRuleListTest {
	FeatureCollection<SimpleFeatureType, SimpleFeature> features;

	@Before
	public void beforeTests() {
		features = FeatureCollections.newCollection();
		SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
		tb.setName("Dummy");
		tb.add("the_geom", Point.class);
		tb.add("allsame", Integer.class);
		tb.add("random", Double.class);

		SimpleFeatureBuilder b = new SimpleFeatureBuilder(tb.buildFeatureType());

		for (int i = 0; i < 100; i++) {
			features.add(b.buildFeature(null,
					new Object[] { null, 111, Math.random() }));
		}
	}

	@Test
	public void testNormaleQuantiles()
			throws SchemaException, IOException, InterruptedException {
		StyledFeatureCollection sfc = new StyledFeatureCollection(features,
				"someId", "titel", (Style) null);

		GraduatedColorPointRuleList ruleList = new GraduatedColorPointRuleList(
				sfc);

		QuantitiesClassification quantitiesClassification = new QuantitiesClassification(
				sfc, "random");
		quantitiesClassification.setRecalcAutomatically(false);
		quantitiesClassification.classificationMethod = METHOD.QUANTILES;
		quantitiesClassification.setNumClasses(5);
		quantitiesClassification.calculateClassLimitsBlocking();
		
		TreeSet<Double> classLimits = quantitiesClassification.getClassLimits();

		assertEquals(6, classLimits.size());

		ruleList.setClassLimits(classLimits);
		List<Rule> rules = ruleList.getRules();
		// +1 NODATA rule
		assertEquals(5 + 1, rules.size());
		assertTrue(rules.get(0).getTitle().contains(" - "));

	}
	

	/**
	 * Creates an QuantileClassifier and runs it on data that just has the same
	 * values. So no classes can be calculated. Then we set it to the rulelist,
	 * and we expect the RL it to deal with it nicely.
	 */
	@Test
	public void testQuantilesWhereTheyCannotBeCreated()
			throws SchemaException, IOException, InterruptedException {
		StyledFeatureCollection sfc = new StyledFeatureCollection(features,
				"someId", "titel", (Style) null);

		GraduatedColorPointRuleList ruleList = new GraduatedColorPointRuleList(
				sfc);

		QuantitiesClassification quantitiesClassification = new QuantitiesClassification(
				sfc, "allsame");
		quantitiesClassification.setRecalcAutomatically(false);
		quantitiesClassification.classificationMethod = METHOD.QUANTILES;
		quantitiesClassification.setNumClasses(5);
		quantitiesClassification.calculateClassLimitsBlocking();
		TreeSet<Double> classLimits = quantitiesClassification.getClassLimits();

		assertEquals(1, classLimits.size());

		ruleList.setClassLimits(classLimits, true);
		List<Rule> rules = ruleList.getRules();
		// 1 NODATA rule and one rule that just fits the only value available 
		assertEquals(1 + 1, rules.size());
		assertEquals("111.0", rules.get(0).getTitle());
	}
}