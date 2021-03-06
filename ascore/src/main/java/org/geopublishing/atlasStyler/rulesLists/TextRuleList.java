/*******************************************************************************
 * Copyright (c) 2010 Stefan A. Tzeggai.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Stefan A. Tzeggai - initial API and implementation
 ******************************************************************************/
package org.geopublishing.atlasStyler.rulesLists;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.geopublishing.atlasStyler.ASUtil;
import org.geopublishing.atlasStyler.AtlasStyler;
import org.geopublishing.atlasStyler.AtlasStyler.LANGUAGE_MODE;
import org.geopublishing.atlasStyler.AtlasStylerVector;
import org.geopublishing.atlasStyler.RuleChangedEvent;
import org.geotools.filter.AndImpl;
import org.geotools.filter.BinaryComparisonAbstract;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Font;
import org.geotools.styling.LabelPlacement;
import org.geotools.styling.PointPlacement;
import org.geotools.styling.Rule;
import org.geotools.styling.TextSymbolizer;
import org.geotools.styling.visitor.DuplicatingStyleVisitor;
import org.opengis.filter.Filter;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.PropertyName;

import de.schmitzm.geotools.FilterUtil;
import de.schmitzm.geotools.GTUtil;
import de.schmitzm.geotools.feature.FeatureUtil;
import de.schmitzm.geotools.feature.FeatureUtil.GeometryForm;
import de.schmitzm.geotools.gui.XMapPane;
import de.schmitzm.geotools.styling.StyledFeaturesInterface;
import de.schmitzm.geotools.styling.StylingUtil;
import de.schmitzm.i18n.Translation;

public class TextRuleList extends AbstractRulesList {

	/** All default text rule names start with this **/
	public static final String DEFAULT_CLASS_RULENAME = "DEFAULT";

	public static final Filter DEFAULT_FILTER_ALL_OTHERS = FilterUtil.ALLWAYS_TRUE_FILTER;

	final static protected Logger LOGGER = Logger.getLogger(TextRuleList.class);

	/**
	 * A Filter to mark that not ALL classes have been disabled by the
	 * {@link TextRuleList}{@link #setEnabled(boolean)} method. This filter is
	 * not used anymore and only for backward compatibility. Will be removed in
	 * 2.0
	 **/
	public static final PropertyIsEqualTo oldClassesDisabledFilter = RulesListInterface.oldAllClassesDisabledFilter;

	/**
	 * A Filter to mark that not ALL classes have been disabled by the
	 * {@link TextRuleList}{@link #setEnabled(boolean)} method. This filter is
	 * not used anymore and only for backward compatibility. Will be removed in
	 * 2.0
	 **/
	public static final PropertyIsEqualTo oldClassesEnabledFilter = RulesListInterface.OldAllClassesEnabledFilter;

	/**
	 * A piece of {@link Filter} that is only true, if the rendering language is
	 * set to the given language. @see XMapPane#setRenderLanguage
	 * 
	 * @param lang
	 *            if <code>null</code>, the rule will be true when no specific
	 *            rendering language has been set.
	 */
	public static Filter classLanguageFilter(String lang) {

		if (lang == null)
			lang = XMapPane.ENV_LANG_DEFAULT;

		// set argument to set a default return value of 0
		Expression exEnv = ff.function("env", ff.literal(XMapPane.ENV_LANG),
				ff.literal(XMapPane.ENV_LANG_DEFAULT));

		Filter filter = ff.equals(ff.literal(lang), exEnv);

		return filter;

	}

	/** Stores whether the class specific {@link TextSymbolizer}s are enabled **/
	private final List<Boolean> classesEnabled = new ArrayList<Boolean>();

	/** Stores all {@link Filter}s for all classes **/
	private final List<Filter> classesFilters = new ArrayList<Filter>();

	/**
	 * Stores whether the class is only valid for a special language. If
	 * <code>null</code>, a class is not language-specific
	 **/
	private final List<String> classesLanguages = new ArrayList<String>();

	/** Stores all maxScale parameters for the classes **/
	List<Double> classesMaxScales = new ArrayList<Double>();

	/** Stores all minScale parameters for all classes **/
	List<Double> classesMinScales = new ArrayList<Double>();

	private List<String> classesRuleNames = new ArrayList<String>();

	/** Stores all {@link TextSymbolizer}s for all classes **/
	List<TextSymbolizer> classesSymbolizers = new ArrayList<TextSymbolizer>();

	/**
	 * @deprecated move the selIdx out of this class
	 */
	@Deprecated
	private int selIdx = 0;

	final private StyledFeaturesInterface<?> styledFeatures;

	public TextRuleList(StyledFeaturesInterface<?> styledFeatures,
			boolean withDefaults) {
		super(RulesListType.TEXT_LABEL, GeometryForm.ANY);
		this.styledFeatures = styledFeatures;
		if (withDefaults)
			addDefaultClass();
	}

	public TextRuleList(StyledFeaturesInterface<?> styledFeatures,
			GeometryForm geometryForm, boolean withDefaults) {
		super(RulesListType.TEXT_LABEL, geometryForm);
		this.styledFeatures = styledFeatures;
	}

	/**
	 * 
	 * @param ts
	 * @param ruleName
	 * @param filter
	 * @param enabled
	 * @param lang
	 * @param minScale
	 *            <code>null</code> allowed will fall-back to 0.
	 * @param maxScale
	 *            <code>null</code> allowed will fall-back to Double.MAX_VALUE
	 * @return the index of the newly added class
	 */
	public int addClass(TextSymbolizer ts, String ruleName, Filter filter,
			boolean enabled, String lang, Double minScale, Double maxScale) {

		int index = classesSymbolizers.size();

		pushQuite();
		try {

			setClassSymbolizer(index, ts);
			setClassRuleName(index, ruleName);
			setClassFilter(index, filter);
			setClassEnabled(index, enabled);
			setClassLang(index, lang);

			setClassMinMaxScales(index, minScale, maxScale);

			setSelIdx(index);

			return index;

		} finally {
			popQuite(new RuleChangedEvent("Added a new class with position "
					+ index + " and ruleName " + ruleName, this));
		}
	}

	/**
	 * Adds filters that indicate whether this class or all classes are
	 * disabled. Is added as the last filters so they are easily identified.
	 * 
	 * @param idx
	 *            the index of the label class (0=default)
	 * 
	 * @return modified filter
	 * 
	 * @see #parseAndRemoveEnabledDisabledFilters(Filter, int)
	 */
	protected Filter addClassEnabledDisabledFilters(Filter filter, int idx) {

		filter = addLanguageFilter(filter, idx);

		// Is this class enabled?
		if (isClassEnabled(idx)) {
			filter = ff.and(StylingUtil.LABEL_CLASS_ENABLED_FILTER, filter);
		} else {
			filter = ff.and(StylingUtil.LABEL_CLASS_DISABLED_FILTER, filter);
		}

		return filter;
	}

	/**
	 * @return the index of the newly added class
	 */
	public int addDefaultClass() {
		return addDefaultClass(null);
	}

	/**
	 * @return the index of the newly added class
	 */
	public int addDefaultClass(String lang) {

		TextSymbolizer defaultTextSymbolizer = createDefaultTextSymbolizer();

		pushQuite();

		try {
			if (existsClass(DEFAULT_FILTER_ALL_OTHERS, lang)) {
				LOGGER.debug("Not adding a default class for " + lang
						+ " because an equal class already exits!");
				return -1;
			}

			String ruleName = DEFAULT_CLASS_RULENAME;
			if (lang != null)
				ruleName += "_" + lang;

			return addClass(defaultTextSymbolizer, ruleName,
					DEFAULT_FILTER_ALL_OTHERS, true, lang, null, null);

		} finally {
			popQuite(new RuleChangedEvent("Added a default TextSymbolizer",
					this));
		}
	}

	private Filter addLanguageFilter(Filter filter, int idx) {
		// Is this class language specific?
		if (getClassLang(idx) != null) {
			filter = ff.and(classLanguageFilter(classesLanguages.get(idx)),
					filter);
		} else {
		}
		return filter;
	}

	/**
	 * @return the number of text classes defined.
	 */
	public int countClasses() {
		return classesFilters.size();
	}

	private TextSymbolizer createDefaultTextSymbolizer() {

		// If we already have a default symbolizer, we will return a duplication
		// of it.
		if (getSymbolizers().size() > 0) {
			DuplicatingStyleVisitor duplicatingStyleVisitor = new DuplicatingStyleVisitor(
					StylingUtil.STYLE_FACTORY);
			duplicatingStyleVisitor.visit(getClassSymbolizer(0));
			return (TextSymbolizer) duplicatingStyleVisitor.getCopy();
		}

		// final String[] fonts = g.getAvailableFontFamilyNames();
		// TODO Use the default classes font if available??
		// TODO better default font!! with multiple families
		Font sldFont = ASUtil.SB
				.createFont("Times New Roman", false, false, 11);
		List<String> valueFieldNamesPrefereStrings = FeatureUtil
				.getValueFieldNamesPrefereStrings(getStyledFeatures()
						.getSchema(), false);

		TextSymbolizer ts = ASUtil.SB.createTextSymbolizer();

		if (valueFieldNamesPrefereStrings.size() != 0) {
			ts.setLabel(FeatureUtil.FILTER_FACTORY2
					.property(valueFieldNamesPrefereStrings.get(0)));
		}

		ts.setFill(StylingUtil.STYLE_BUILDER.createFill(Color.black));

		// TODO better default font!! with multiple families
		ts.setFont(sldFont);

		// For Polygons we set the PointPlacement option X to 50%
		// by default
		if (FeatureUtil.getGeometryForm(getStyledFeatures().getSchema()) == GeometryForm.POLYGON) {
			LabelPlacement labelPlacement = ts.getLabelPlacement();
			if (labelPlacement instanceof PointPlacement) {
				PointPlacement pointPlacement = (PointPlacement) labelPlacement;
				pointPlacement.getAnchorPoint().setAnchorPointX(
						FeatureUtil.FILTER_FACTORY2.literal(".5"));
			}
		}

		return ts;
	}

	/**
	 * @return <code>true</code> if another class with the same filter and name
	 *         already exists.
	 */
	public boolean existsClass(Filter filter, String lang) {

		for (int i = 0; i < countClasses(); i++) {
			if (getClassLang(i) == null && lang != null)
				continue;
			if (getClassLang(i) != null && !getClassLang(i).equals(lang))
				continue;

			if (filter != null
					&& getClassFilter(i).toString().equals(filter.toString()))
				return true;
		}

		return false;
	}

	private List<Filter> getClassesFilters() {
		return classesFilters;
	}

	public Filter getClassFilter(int index) {
		if (index > classesFilters.size() - 1)
			return null;
		return classesFilters.get(index);
	}

	public String getClassLang(int index) {
		if (index > classesLanguages.size() - 1)
			return null;
		return classesLanguages.get(index);
	}

	public TextSymbolizer getClassSymbolizer(int index) {
		if (index > classesSymbolizers.size() - 1)
			return null;
		return classesSymbolizers.get(index);
	}

	/**
	 * A list of languages that a default class has already been defined for
	 * 
	 * @return
	 */
	public ArrayList<String> getDefaultLanguages() {
		ArrayList<String> usedLangs = new ArrayList<String>();

		if (AtlasStyler.getLanguageMode() == LANGUAGE_MODE.OGC_SINGLELANGUAGE)
			return usedLangs;

		for (String lang : AtlasStyler.getLanguages()) {
			if (existsClass(DEFAULT_FILTER_ALL_OTHERS, lang)) {
				usedLangs.add(lang);
			}
		}
		return usedLangs;
	}

	/**
	 * @deprecated move the selIdx out of this class
	 */
	@Deprecated
	public String getRuleName() {
		return classesRuleNames.get(selIdx);
	}

	public String getRuleName(int index) {
		if (index > classesRuleNames.size() - 1)
			return null;
		return classesRuleNames.get(index);
	}

	public List<String> getRuleNames() {
		return classesRuleNames;
	}

	@Override
	public List<Rule> getRules() {

		// LOGGER.debug("Enabled Textrule = " + isEnabled());

		ArrayList<Rule> rules = new ArrayList<Rule>();

		for (int i = 0; i < classesSymbolizers.size(); i++) {
			TextSymbolizer tSymbolizer = classesSymbolizers.get(i);

			// The filter stored for this class. This filer already contains the
			// exclusion of NODATA values.
			Filter filter = classesFilters.get(i);

			// if (i == 0 || filter.equals(FILTER_DEFAULT_ALL_OTHERS_ID)) {
			if (getRuleName(i).startsWith(DEFAULT_CLASS_RULENAME)) {
				// The default symbolizer get's a special filter= not (2ndFilter
				// OR 3rdFilter OR 4thFilter) if other rules are defined.

				if (getRuleNames().size() > 1) {

					List<Filter> ors = new ArrayList<Filter>();
					for (int j = 0; j < getRuleNames().size(); j++) { // Default
						Filter otherFilter = getClassFilter(j);

						if (j == i || j == 0)
							continue;

						ors.add(addLanguageFilter(otherFilter, j));
					}

					if (ors.size() > 0) {
						filter = ASUtil.ff2.not(FilterUtil
								.correctOrForValidation(ASUtil.ff2.or(ors)));
					}

				} else {
					// The default filter includes all, IF no other rules have
					// been defined.
					filter = DEFAULT_FILTER_ALL_OTHERS;
				}
			}

			Rule rule = ASUtil.SB.createRule(tSymbolizer);
			rule.setName(getRuleNames().get(i));
			rule.setMinScaleDenominator(classesMinScales.get(i));
			rule.setMaxScaleDenominator(classesMaxScales.get(i));

			// If there is a second label attribute, make a second Rule that
			// only displays the first label in case that the second is null
			final PropertyName firstPropertyName = StylingUtil
					.getFirstPropertyName(styledFeatures.getSchema(),
							tSymbolizer);
			final PropertyName secondPropertyName = StylingUtil
					.getSecondPropertyName(styledFeatures.getSchema(),
							tSymbolizer);
			if (secondPropertyName != null) {
				// Attention: This structure is also in AtlasStyler.import

				Filter secondLabelNodataFilter = ff.isNull(secondPropertyName);
				for (Object nodata : getStyledFeatures()
						.getAttributeMetaDataMap()
						.get(secondPropertyName.getPropertyName())
						.getNodataValues()) {
					secondLabelNodataFilter = ff.or(
							ff.equals(secondPropertyName, ff.literal(nodata)),
							secondLabelNodataFilter);
				}

				Filter secondNotEmpty = ASUtil.ff2.not(secondLabelNodataFilter);
				filter = ASUtil.ff.and(secondNotEmpty, filter);
				filter = addClassEnabledDisabledFilters(filter, i);
				filter = addAbstractRlSettings(filter);
				rule.setFilter(filter);

				rules.add(rule);

				// Not the fallbackrule in case that the second attribute IS
				// empty.
				DuplicatingStyleVisitor duplicatingRuleVisitor = new DuplicatingStyleVisitor();
				duplicatingRuleVisitor.visit(rule);
				Rule fallbackRule = (Rule) duplicatingRuleVisitor.getCopy();
				fallbackRule.setName(RULENAME_DONTIMPORT);
				filter = ASUtil.ff.and(secondLabelNodataFilter, filter);
				filter = addClassEnabledDisabledFilters(filter, i);
				filter = addAbstractRlSettings(filter);
				fallbackRule.setFilter(filter);
				final TextSymbolizer ts2 = (TextSymbolizer) fallbackRule
						.symbolizers().get(0);
				StylingUtil.setDoublePropertyName(ts2, firstPropertyName, null);
				rules.add(fallbackRule);
			} else {
				// No second labeling property set
				filter = addClassEnabledDisabledFilters(filter, i);
				filter = addAbstractRlSettings(filter);
				rule.setFilter(filter);
				rules.add(rule);
			}

		}

		return rules;
	}

	/**
	 * @deprecated move the selIdx out of this class
	 */

	@Deprecated
	public int getSelIdx() {
		return selIdx;
	}

	public StyledFeaturesInterface<?> getStyledFeatures() {
		return styledFeatures;
	}

	/**
	 * @return the {@link TextSymbolizer} selected in the combo box
	 * @see #getSelIdx()
	 * @deprecated move the selIdx out of this class
	 */
	@Deprecated
	public TextSymbolizer getSymbolizer() {
		return getClassSymbolizer(selIdx);
	}

	public List<TextSymbolizer> getSymbolizers() {
		return classesSymbolizers;
	}

	// /**
	// * Are all {@link TextSymbolizer} classes disabled/enabled.
	// */
	// public boolean isEnabled() {
	// return enabled;
	// }

	/**
	 * @return <code>true</code> is at least one DEFAULT rule exists.
	 */
	public boolean hasDefault() {
		for (String s : getRuleNames()) {
			if (s != null && s.startsWith(DEFAULT_CLASS_RULENAME))
				return true;
		}
		return false;
	}

	public void importClassesFromStyle(RulesListInterface symbRL,
			Component owner) {

		pushQuite();

		try {

			if (symbRL instanceof UniqueValuesRuleList) {
				UniqueValuesRuleList uniqueRL = (UniqueValuesRuleList) symbRL;
				if (uniqueRL.getNumClasses() <= (uniqueRL.isWithDefaultSymbol() ? 1
						: 0)) {
					if (owner != null) {
						JOptionPane
								.showMessageDialog(
										owner,
										AtlasStylerVector
												.R("TextRulesList.Labelclass.Action.LoadClassesFromSymbols.Error.NoClasses"));
					}
					return;
				}

				if (getRuleNames().size() > 1) {

					if (owner != null) {
						int res = JOptionPane
								.showConfirmDialog(
										owner,
										AtlasStylerVector
												.R("TextRulesList.Labelclass.Action.LoadClassesFromSymbols.AskOverwrite",
														getRuleNames().size() - 1,
														(uniqueRL.getValues()
																.size() - (uniqueRL
																.isWithDefaultSymbol() ? 1
																: 0))),
										AtlasStylerVector
												.R("TextRulesList.Labelclass.Action.LoadClassesFromSymbols.AskOverwrite.Title"),
										JOptionPane.YES_NO_OPTION);
						if (res != JOptionPane.YES_OPTION)
							return;
					}

					removeAllClassesButFirst();
				}

				for (int i = (uniqueRL.isWithDefaultSymbol() ? 1 : 0); i < uniqueRL
						.getValues().size(); i++) {
					Object val = uniqueRL.getValues().get(i);

					// Copy the first rules's settings to the new class:
					TextSymbolizer defaultTextSymbolizer = createDefaultTextSymbolizer();

					getSymbolizers().add(defaultTextSymbolizer);

					PropertyIsEqualTo filter = ASUtil.ff2.equals(ASUtil.ff2
							.property(uniqueRL.getPropertyFieldName()),
							ASUtil.ff2.literal(val));

					getClassesFilters().add(filter);

					classesLanguages.add(null);

					setClassEnabled(1 + i
							- (uniqueRL.isWithDefaultSymbol() ? 1 : 0), true);

					getRuleNames().add(uniqueRL.getLabels().get(i));

					classesMaxScales.add(uniqueRL.getSymbols().get(i)
							.getMaxScaleDenominator());
					classesMinScales.add(uniqueRL.getSymbols().get(i)
							.getMinScaleDenominator());
				}
			}

			/***********************************************************************
			 * Importing Rules form GraduatedColorRuleList is a bit different
			 */
			else if (symbRL instanceof GraduatedColorRuleList) {
				GraduatedColorRuleList gradRL = (GraduatedColorRuleList) symbRL;
				if (gradRL.getNumClasses() <= 0) {
					if (owner != null) {
						JOptionPane
								.showMessageDialog(
										owner,
										AtlasStylerVector
												.R("TextRulesList.Labelclass.Action.LoadClassesFromSymbols.Error.NoClasses"));
					}
					return;
				}

				if (getRuleNames().size() > 1) {

					if (owner != null) {
						int res = JOptionPane
								.showConfirmDialog(
										owner,
										AtlasStylerVector
												.R("TextRulesList.Labelclass.Action.LoadClassesFromSymbols.AskOverwrite",
														getRuleNames().size() - 1,
														gradRL.getRules()
																.size()),
										AtlasStylerVector
												.R("TextRulesList.Labelclass.Action.LoadClassesFromSymbols.AskOverwrite.Title"),
										JOptionPane.YES_NO_OPTION);

						if (res != JOptionPane.YES_OPTION)
							return;
					}
					removeAllClassesButFirst();

				}

				int idx = 1; // the default rule is left untouched
				for (Rule r : gradRL.getRules()) {
					idx++;
					setClassEnabled(idx, true);

					// Copy the first rules's settings to the new class:
					TextSymbolizer defaultTextSymbolizer = createDefaultTextSymbolizer();
					StylingUtil.copyAllValues(defaultTextSymbolizer,
							getClassSymbolizer(0));
					getSymbolizers().add(defaultTextSymbolizer);

					Filter filter = r.getFilter();

					getClassesFilters().add(filter);

					classesMinScales.add(gradRL.getTemplate()
							.getMinScaleDenominator());
					classesMaxScales.add(gradRL.getTemplate()
							.getMaxScaleDenominator());

					// The title could be a Translation.. but we only want a
					// string!
					Translation t = new Translation();
					t.fromOneLine(GTUtil.descriptionTitle(r.getDescription()));
					getRuleNames().add(t.toString());
				}

				JOptionPane
						.showMessageDialog(
								owner,
								AtlasStylerVector
										.R("TextRulesList.Labelclass.Action.LoadClassesFromSymbols.SuccesMsg",
												getRuleNames().size() - 1));

			}
		} finally {
			popQuite();
		}

	}

	/**
	 * Parses a list of {@link Rule}s and configures this {@link TextRuleList}
	 * with it.
	 */
	@Override
	public void importRules(List<Rule> rules) {

		pushQuite();
		try {

			int idx = 0;
			for (Rule rule : rules) {

				Filter filter = rule.getFilter();
				try {
					filter = parseAndRemoveEnabledDisabledFilters(filter, idx);

					// When using two label properties, we have two rules for
					// one
					// label class. So we drop it.
					String ruleName = rule.getName();
					if (ruleName != null
							&& ruleName.equals(RULENAME_DONTIMPORT))
						continue;

					if ( (ruleName != null && ruleName.startsWith(DEFAULT_CLASS_RULENAME))
							|| (filter != null && filter.equals(oldClassesEnabledFilter))
							|| (filter != null && filter.equals(oldClassesDisabledFilter))
							|| idx == 0) {
						// Do not store the filter imported for default rules.
						getClassesFilters().add(DEFAULT_FILTER_ALL_OTHERS);

						// If this has been an old default rule, update the
						// ruleName to the new
						ruleName = DEFAULT_CLASS_RULENAME;
						if (getClassLang(idx) != null)
							ruleName += "_" + getClassLang(idx);
					} else {
						getClassesFilters().add(filter);
					}

					// getClassLang(idx);

					final TextSymbolizer textSymb = (TextSymbolizer) rule
							.getSymbolizers()[0];

					getSymbolizers().add(textSymb);
					getRuleNames().add(ruleName);

					classesMaxScales.add(rule.getMaxScaleDenominator());
					classesMinScales.add(rule.getMinScaleDenominator());

					idx++;
				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.error("Error parsing textSymbolizerClassfilter '  "
							+ filter + "  '", e);
				}
			}

			/**
			 * @deprecated move the selIdx out of this class
			 */
			setSelIdx(0);

		} finally {
			popQuite();
		}

	}

	public Boolean isClassEnabled(int index) {
		if (index > classesEnabled.size() - 1)
			return null;
		return classesEnabled.get(index);
	}

	/**
	 * Interprets the filters added by
	 * {@link #addClassEnabledDisabledFilters(Filter, int)}
	 * 
	 * @param idx
	 *            the index of the label class (0=default)
	 * 
	 * @return The simpler filter that was inside all the mess.
	 */
	protected Filter parseAndRemoveEnabledDisabledFilters(Filter filter, int idx) {

		final Filter fullFilter = filter;

		filter = parseAbstractRlSettings(filter);

		/**
		 * Interpreting whether the class is disable/enables
		 */
		try {

			if (filter.equals(Filter.EXCLUDE)) {
				setClassEnabled(idx, false);
			} else if (filter.equals(Filter.INCLUDE)) {
				setClassEnabled(idx, true);
			} else if (filter.equals(oldClassesEnabledFilter)) {
				setClassEnabled(idx, true);
			} else if (filter.equals(oldClassesDisabledFilter)) {
				setClassEnabled(idx, false);
			} else {
				List<?> andChildren = ((AndImpl) filter).getChildren();
				if (andChildren.get(0).equals(
						StylingUtil.LABEL_CLASS_DISABLED_FILTER)) {
					setClassEnabled(idx, false);
				} else if (andChildren.get(0).equals(
						StylingUtil.LABEL_CLASS_ENABLED_FILTER)) {
					setClassEnabled(idx, true);
				} else {
					throw new RuntimeException(andChildren.get(0).toString()
							+ "\n" + fullFilter.toString());
				}
				filter = (Filter) andChildren.get(1);
			}

		} catch (Exception e) {
			setClassEnabled(idx, true);
			LOGGER.warn(
					"Couldn't interpret whether this TextRulesList CLASS is disabled or enabled. Assuming it is enabled.",
					e);
		}

		filter = parseAndRemoveLanguageFilter(filter, idx);

		return filter;
	}

	private Filter parseAndRemoveLanguageFilter(Filter filter, int idx) {
		/**
		 * Interpreting whether this class is language specific
		 */

		LOGGER.debug("filter: " + filter);

		if (!(filter instanceof AndImpl)) {
			setClassLang(idx, null);
			return filter;
		}

		try {
			AndImpl andFilter = (AndImpl) filter;

			List<?> andChildren = andFilter.getChildren();
			// System.out.println(filter);

			Filter envEqualsLang = (Filter) andChildren.get(0);

			Function envFunction = (Function) ((BinaryComparisonAbstract) envEqualsLang)
					.getExpression2();

			Expression langExp = ((BinaryComparisonAbstract) envEqualsLang)
					.getExpression1();

			if (!envFunction.getName().equals("env"))
				throw new RuntimeException();

			setClassLang(idx, langExp.toString());

			filter = (Filter) andChildren.get(1);

		} catch (Exception e) {
			setClassLang(idx, null);
		}
		return filter;
	}

	@Override
	public void parseMetaInfoString(String metaInfoString, FeatureTypeStyle fts) {
		// Does nothing
	}

	private void removeAllClassesButFirst() {
		TextSymbolizer backupS = getClassSymbolizer(0);
		getSymbolizers().clear();
		getSymbolizers().add(backupS);

		String backupRN = getRuleNames().get(0);
		getRuleNames().clear();
		getRuleNames().add(backupRN);

		Filter backupFR = getClassFilter(0);
		getClassesFilters().clear();
		getClassesFilters().add(backupFR);

		Double backupMin = classesMinScales.get(0);
		classesMinScales.clear();
		classesMinScales.add(backupMin);

		Double backupMax = classesMaxScales.get(0);
		classesMaxScales.clear();
		classesMaxScales.add(backupMax);

		Boolean backupEnabled = classesEnabled.get(0);
		classesEnabled.clear();
		classesEnabled.add(backupEnabled);
	}

	/**
	 * Remove a text symbolizer class.
	 */
	public void removeClass(int idx) {
		getSymbolizers().remove(idx);
		getClassesFilters().remove(idx);
		getRuleNames().remove(idx);
		removeClassMinScale(idx);
		removeClassMaxScale(idx);
		removeClassEnabled(idx);
	}

	/**
	 * @param classIdx
	 *            Label class idx, 0 = default/all others
	 */
	public void removeClassEnabled(int classIdx) {
		classesEnabled.remove(classIdx);
	}

	/**
	 * @param classIdx
	 *            Label class idx, 0 = default/all others
	 */
	public void removeClassMaxScale(int classIdx) {
		classesMaxScales.remove(classIdx);
	}

	/**
	 * @param classIdx
	 *            Label class idx, 0 = default/all others
	 */
	public void removeClassMinScale(int classIdx) {
		classesMinScales.remove(classIdx);
	}

	public void setClassEnabled(int index, boolean b) {
		while (classesEnabled.size() - 1 < index) {
			classesEnabled.add(true);
		}
		classesEnabled.set(index, b);
		fireEvents(new RuleChangedEvent(
				"a text symbolizer class enablement has been set to " + b, this));
	}

	public void setClassFilter(int index, Filter filter) {
		while (classesFilters.size() - 1 < index) {
			classesFilters.add(Filter.EXCLUDE);
		}

		classesFilters.set(index, filter);
		fireEvents(new RuleChangedEvent(
				"a text symbolizer class FILTER has been set to " + filter,
				this));
	}

	/**
	 * @param lang
	 *            may be <code>null</code>
	 */
	void setClassLang(int index, String lang) {

		while (classesLanguages.size() - 1 < index) {
			classesLanguages.add(null);
		}

		if (XMapPane.ENV_LANG_DEFAULT.equals(lang)) {
			lang = null;
		}

		if (index == 0 && lang != null)
			throw new RuntimeException(
					"The default class may not be language specific");

		classesLanguages.set(index, lang);
		fireEvents(new RuleChangedEvent(
				"a text symbolizer CLASS language has been set to " + lang,
				this));
	}

	public void setClassMaxScale(int index, Double maxValue) {
		while (classesMaxScales.size() - 1 < index) {
			classesMaxScales.add(0.);
		}

		if (maxValue == null)
			maxValue = Double.MAX_VALUE;
		classesMaxScales.set(index, maxValue);

		fireEvents(new RuleChangedEvent(
				"a text CLASS MaxScale has been set to " + maxValue, this));
	}

	public void setClassMinMaxScales(int index, Double minValue, Double maxValue) {
		setClassMinScale(index, minValue);
		setClassMaxScale(index, maxValue);
	}

	public void setClassMinScale(int index, Double minValue) {
		while (classesMinScales.size() - 1 < index) {
			classesMinScales.add(0.);
		}
		if (minValue == null)
			minValue = 0.;
		classesMinScales.set(index, minValue);

		fireEvents(new RuleChangedEvent(
				"a text CLASS MinScale has been set to " + minValue, this));
	}

	private void setClassRuleName(int index, String ruleName) {
		while (classesRuleNames.size() - 1 < index) {
			classesRuleNames.add("");
		}
		classesRuleNames.set(index, ruleName);

		fireEvents(new RuleChangedEvent(
				"a text CLASS rulename has been set to " + ruleName, this));
	}

	private void setClassSymbolizer(int index, TextSymbolizer ts) {
		while (classesSymbolizers.size() - 1 < index) {
			classesSymbolizers.add(ts);
		}
		classesSymbolizers.set(index, ts);

		fireEvents(new RuleChangedEvent(
				"a text CLASS symbolizer has been set to " + ts, this));
	}

	public void setRuleNames(List<String> ruleNames) {
		this.classesRuleNames = ruleNames;
		// No Event needs to be fired! Its just a name...
	}

	/**
	 * @deprecated move the selIdx out of this class
	 */
	@Deprecated
	public void setSelIdx(int selIdx) {
		this.selIdx = selIdx;
	}

	public void setSymbolizers(List<TextSymbolizer> symbolizers) {
		this.classesSymbolizers = symbolizers;
		fireEvents(new RuleChangedEvent("setSymbolizers", this));
	}

}
