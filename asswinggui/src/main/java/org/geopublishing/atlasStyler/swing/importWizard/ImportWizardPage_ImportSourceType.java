package org.geopublishing.atlasStyler.swing.importWizard;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import net.miginfocom.swing.MigLayout;

import org.geopublishing.atlasStyler.ASProps;
import org.geopublishing.atlasStyler.ASProps.Keys;
import org.geopublishing.atlasStyler.AsSwingUtil;
import org.geopublishing.atlasStyler.swing.importWizard.ImportWizard.SOURCETYPE;
import org.netbeans.spi.wizard.WizardPage;

public class ImportWizardPage_ImportSourceType extends WizardPage {
	/*
	 * The short description label that appears on the left side of the wizard
	 */
	JLabel explanationJLabel = new JLabel(
			AsSwingUtil.R("ImportWizard.ImportSourceType.Explanation"));

	private final String validationImportSourceTypeFailedMsg = AsSwingUtil
			.R("ImportWizard.ImportSourceType.ValidationError");

	private JLabel explanationFileJLabel = new JLabel(
			AsSwingUtil.R("ImportWizard.ImportSourceType.Explanation.File"));

	private JRadioButton fileJRadioButton;

	private JLabel explanationWfsJLabel = new JLabel(
			AsSwingUtil.R("ImportWizard.ImportSourceType.Explanation.WFS"));
	private JRadioButton wfsJRadioButton;

	private JLabel explanationPostGISJLabel = new JLabel(
			AsSwingUtil.R("ImportWizard.ImportSourceType.Explanation.PostGis"));
	private JRadioButton pgJRadioButton;

	private ButtonGroup buttonGroup = new ButtonGroup();

	public static String getDescription() {
		return AsSwingUtil.R("ImportWizard.ImportSourceType");
	}

	public ImportWizardPage_ImportSourceType() {
		initGui();
	}

	@Override
	protected String validateContents(final Component component,
			final Object event) {
		if (buttonGroup.getSelection() == null)
			return validationImportSourceTypeFailedMsg;

		return null;
	}

	private void initGui() {
		setLayout(new MigLayout("wrap 1, w :460:, h :270:"));
		add(explanationJLabel);
		add(getFileJRadioButton(), "gapy unrelated");
		add(explanationFileJLabel);
		add(getWfsJRadioButton(), "gapy unrelated");
		add(explanationWfsJLabel);

		add(getPostGisJRadioButton(), "gapy unrelated");
		add(explanationPostGISJLabel);

	}

	private JRadioButton getFileJRadioButton() {
		if (fileJRadioButton == null) {
			fileJRadioButton = new JRadioButton(
					AsSwingUtil.R("ImportWizard.ImportSourceType.File"));
			buttonGroup.add(fileJRadioButton);

			fileJRadioButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					if (fileJRadioButton.isSelected()) {

						putWizardData(ImportWizard.IMPORT_SOURCE_TYPE,
								ImportWizard.SOURCETYPE.file);

						// Store this selection
						ASProps.set(Keys.lastImportWizardType,
								ImportWizard.SOURCETYPE.file.toString());
					}
				}

			});

			// Select this if it is the last used import option, select it
			if (ASProps.get(Keys.lastImportWizardType) != null) {
				String lastImport = ASProps.get(Keys.lastImportWizardType);
				boolean b = lastImport != null
						&& ImportWizard.SOURCETYPE.valueOf(lastImport) == SOURCETYPE.file;
				fileJRadioButton.setSelected(b);
				if (b)
					putWizardData(ImportWizard.IMPORT_SOURCE_TYPE,
							ImportWizard.SOURCETYPE.file);

			}
		}
		return fileJRadioButton;
	}

	private JRadioButton getWfsJRadioButton() {
		if (wfsJRadioButton == null) {
			wfsJRadioButton = new JRadioButton(
					AsSwingUtil.R("ImportWizard.ImportSourceType.Wfs"));
			buttonGroup.add(wfsJRadioButton);

			wfsJRadioButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					if (wfsJRadioButton.isSelected()) {

						putWizardData(ImportWizard.IMPORT_SOURCE_TYPE,
								ImportWizard.SOURCETYPE.wfs);

						// Store this selection
						ASProps.set(Keys.lastImportWizardType,
								ImportWizard.SOURCETYPE.wfs.toString());
					}
				}

			});

			// Select this if it is the last used import option, select it
			if (ASProps.get(Keys.lastImportWizardType) != null) {
				String lastImport = ASProps.get(Keys.lastImportWizardType);
				boolean b = lastImport != null
						&& ImportWizard.SOURCETYPE.valueOf(lastImport) == SOURCETYPE.wfs;
				wfsJRadioButton.setSelected(b);
				if (b)
					putWizardData(ImportWizard.IMPORT_SOURCE_TYPE,
							ImportWizard.SOURCETYPE.wfs);

			}
		}
		return wfsJRadioButton;
	}

	private JRadioButton getPostGisJRadioButton() {
		if (pgJRadioButton == null) {
			pgJRadioButton = new JRadioButton(
					AsSwingUtil.R("AtlasStyler.SelectPostgisLayerDialog.title"));
			buttonGroup.add(pgJRadioButton);

			pgJRadioButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					if (pgJRadioButton.isSelected()) {

						putWizardData(ImportWizard.IMPORT_SOURCE_TYPE,
								ImportWizard.SOURCETYPE.postgis);

						// Store this selection
						ASProps.set(Keys.lastImportWizardType,
								ImportWizard.SOURCETYPE.postgis.toString());
					}
				}

			});

			// Select this if it is the last used import option, select it
			if (ASProps.get(Keys.lastImportWizardType) != null) {
				String lastImport = ASProps.get(Keys.lastImportWizardType);
				boolean b = lastImport != null
						&& ImportWizard.SOURCETYPE.valueOf(lastImport) == SOURCETYPE.postgis;
				pgJRadioButton.setSelected(b);
				if (b)
					putWizardData(ImportWizard.IMPORT_SOURCE_TYPE,
							ImportWizard.SOURCETYPE.postgis);

			}
		}
		return pgJRadioButton;
	}

}
