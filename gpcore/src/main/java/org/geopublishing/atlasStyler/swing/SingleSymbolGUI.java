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
package org.geopublishing.atlasStyler.swing;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;
import org.geopublishing.atlasStyler.ASUtil;
import org.geopublishing.atlasStyler.AtlasStyler;
import org.geopublishing.atlasStyler.RuleChangeListener;
import org.geopublishing.atlasStyler.RuleChangedEvent;
import org.geopublishing.atlasStyler.SingleRuleList;
import org.geopublishing.atlasViewer.swing.AVSwingUtil;
import org.geotools.styling.Symbolizer;

import schmitzm.lang.LangUtil;
import schmitzm.swing.SwingUtil;
import skrueger.i8n.Translation;
import skrueger.swing.TranslationAskJDialog;
import skrueger.swing.TranslationEditJPanel;

public class SingleSymbolGUI extends AbstractRuleListGui implements
		ClosableSubwindows {
	protected Logger LOGGER = LangUtil.createLogger(this);

	private EditSymbolButton jButtonSymbolSelector = null;

	private final SingleRuleList<? extends Symbolizer> singleSymbolRuleList;

	/**
	 * This is the default constructor
	 * 
	 * @param singleSymbolRuleList
	 * @param openWindows
	 */
	public SingleSymbolGUI(final SingleRuleList<?> singleSymbolRuleList) {
		super(singleSymbolRuleList);
		if (singleSymbolRuleList == null)
			throw new IllegalStateException(
					"A GUI can not be created if no RuleList is provided.");
		this.singleSymbolRuleList = singleSymbolRuleList;
		initialize();
	}

	/**
	 * Adding a listener that will update the Button-Image when the rulelist has
	 * been altered *
	 */
	final RuleChangeListener listenToChangesInTheRulesToUpdateButton = new RuleChangeListener() {

		@Override
		public void changed(RuleChangedEvent e) {
			jButtonSymbolSelector.setIcon(new ImageIcon(singleSymbolRuleList
					.getImage()));
		}

	};

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		JLabel jLabelSymbol = new JLabel(
				AtlasStyler.R("SingleSymbolGUI.Symbol.Label"));
		jLabelSymbol.setToolTipText(AtlasStyler.R("SingleSymbolGUI.Symbol.TT"));

		JLabel jLabelHeading = new JLabel(
				AtlasStyler.R("SingleSymbolGUI.Heading.Label"));
		jLabelHeading.setFont(jLabelHeading.getFont().deriveFont(
				AVSwingUtil.HEADING_FONT_SIZE));

		this.setLayout(new MigLayout());
		this.add(jLabelHeading, "span 2, wrap");
		this.add(jLabelSymbol);
		this.add(getJButtonSymbol(), "wrap");

		JLabel jLabelTranslation = new JLabel(
				AtlasStyler.R("SingleSymbolGUI.Label.Label"));
		jLabelTranslation.setToolTipText(AtlasStyler
				.R("SingleSymbolGUI.Label.TT"));
		this.add(jLabelTranslation);
		this.add(getjLabelTranslationEdit(), "wrap");
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private EditSymbolButton getJButtonSymbol() {
		if (jButtonSymbolSelector == null) {
			jButtonSymbolSelector = new EditSymbolButton(singleSymbolRuleList);
			jButtonSymbolSelector.setToolTipText(AtlasStyler
					.R("SingleSymbolGUI.Symbol.TT"));
		}
		return jButtonSymbolSelector;
	}

	/**
	 * This method initializes jPanel11
	 * 
	 * @return javax.swing.JPanel
	 */
	private JButton getjLabelTranslationEdit() {
		final JButton jLabelTranslationEdit = new JButton();
		jLabelTranslationEdit.setToolTipText(AtlasStyler
				.R("SingleSymbolGUI.Label.TT"));

		/*******************************************************************
		 * The Translation JLabel can be editited
		 */
		jLabelTranslationEdit.setAction(new AbstractAction() {

			private TranslationAskJDialog ask;

			@Override
			public void actionPerformed(ActionEvent e) {

				String oldTitle = singleSymbolRuleList.getRuleTitle();

				if (AtlasStyler.getLanguageMode() == AtlasStyler.LANGUAGE_MODE.ATLAS_MULTILANGUAGE) {
					/*******************************************************
					 * AtlasStyler.LANGUAGE_MODE.ATLAS_MULTILANGUAGE
					 */
					final Translation translation = new Translation();
					translation.fromOneLine(oldTitle);

					if (ask == null) {
						TranslationEditJPanel transLabel = new TranslationEditJPanel(
								AtlasStyler.R("SingleSymbolGUI.EnterLabel"),
								translation, AtlasStyler.getLanguages());

						ask = new TranslationAskJDialog(SingleSymbolGUI.this,
								transLabel);
						ask.addPropertyChangeListener(new PropertyChangeListener() {

							@Override
							public void propertyChange(PropertyChangeEvent evt) {
								if (evt.getPropertyName()
										.equals(TranslationAskJDialog.PROPERTY_CANCEL_AND_CLOSE)) {
									ask = null;
								}
								if (evt.getPropertyName()
										.equals(TranslationAskJDialog.PROPERTY_APPLY_AND_CLOSE)) {

									singleSymbolRuleList
											.setRuleTitle(translation);
									jLabelTranslationEdit.setText(translation
											.toString());
								}
								ask = null;
							}

						});

					}
					SwingUtil.setRelativeFramePosition(ask,
							SingleSymbolGUI.this, .5, .5);
					ask.setVisible(true);

				} else {
					/*******************************************************
					 * AtlasStyler.LANGUAGE_MODE.OGC_SINGLELANGUAGE
					 */
					String newTitle = ASUtil.askForString(SingleSymbolGUI.this,
							oldTitle, null);
					if (newTitle != null) {
						singleSymbolRuleList.setRuleTitle(newTitle);
						jLabelTranslationEdit.setText(newTitle);
					}
				}
			}
		});

		Translation translation = new Translation();
		try {
			String firstTitle = singleSymbolRuleList.getRuleTitle();
			translation.fromOneLine(firstTitle);
			jLabelTranslationEdit.setText(translation.toString());
		} catch (Exception e) {
			jLabelTranslationEdit.setText("interpretation error");
		}

		return jLabelTranslationEdit;
	}

	@Override
	public void dispose() {
		// Not needed because its a weak listener list, but can't be bad:
		singleSymbolRuleList
				.removeListener(listenToChangesInTheRulesToUpdateButton);

	}

}
