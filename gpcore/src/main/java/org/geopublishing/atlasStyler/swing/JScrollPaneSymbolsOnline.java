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
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;
import org.geopublishing.atlasStyler.ASUtil;
import org.geopublishing.atlasStyler.AtlasStyler;
import org.geopublishing.atlasStyler.OpenMapSymbols;
import org.geopublishing.atlasStyler.SingleLineSymbolRuleList;
import org.geopublishing.atlasStyler.SinglePointSymbolRuleList;
import org.geopublishing.atlasStyler.SinglePolygonSymbolRuleList;
import org.geopublishing.atlasStyler.SingleRuleList;
import org.geopublishing.atlasViewer.swing.AtlasSwingWorker;
import org.geopublishing.atlasViewer.swing.Icons;
import org.geopublishing.atlasViewer.swing.internal.AtlasStatusDialog;
import org.geotools.data.DataUtilities;
import org.geotools.feature.GeometryAttributeType;
import org.geotools.styling.Symbolizer;
import org.opengis.feature.type.GeometryDescriptor;

import schmitzm.geotools.feature.FeatureUtil;
import schmitzm.geotools.feature.FeatureUtil.GeometryForm;
import schmitzm.swing.ExceptionDialog;
import schmitzm.swing.SwingUtil;

public class JScrollPaneSymbolsOnline extends JScrollPaneSymbols {

	private Logger LOGGER = ASUtil.createLogger(this);

	@Override
	protected String getDesc() {
		return AtlasStyler.R("SymbolSelector.Tabs.OnlineSymbols");
	}

	@Override
	protected Icon getIcon() {
		return Icons.ICON_ONLINE;
	}

	// private final GeometryDescriptor attType;

	private URL url;
	private final GeometryForm geoForm;

	/**
	 * Construct a {@link JScrollPaneSymbolsOnline}, listing all .sld symbols
	 * from http://freemapsymbols.org. filtered for a special geoemtry type
	 * (point, line, polygon)
	 * 
	 * @param attType
	 *            The {@link GeometryAttributeType} determines which folder will
	 *            be scanned for SLD fragments.
	 * 
	 * @author <a href="mailto:skpublic@wikisquare.de">Stefan Alfons Tzeggai</a>
	 */
	public JScrollPaneSymbolsOnline(GeometryDescriptor attType) {
		this(FeatureUtil.getGeometryForm(attType));
	}

	/**
	 * Construct a {@link JScrollPaneSymbolsOnline}, listing all .sld symbols
	 * from http://freemapsymbols.org. filtered for a special geoemtry type
	 * (point, line, polygon)
	 * 
	 * @param attType
	 *            The {@link GeometryAttributeType} determines which folder will
	 *            be scanned for SLD fragments.
	 * 
	 * @author <a href="mailto:skpublic@wikisquare.de">Stefan Alfons Tzeggai</a>
	 */
	public JScrollPaneSymbolsOnline(GeometryForm geoForm) {
		this.geoForm = geoForm;
		try {
			url = new URL(OpenMapSymbols.BASE_URL
					+ geoForm.toString().toLowerCase());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

		rescan(true);
	}

	/**
	 * Rescanns the online folder for symbols in background. Symbols that have
	 * been removed are not beeing removed.
	 * 
	 * @param reset
	 *            Shall the {@link JList} be cleared before rescan
	 * 
	 * @author <a href="mailto:skpublic@wikisquare.de">Stefan Alfons Tzeggai</a>
	 */
	public void rescan(boolean reset) {

		if (reset) {
			getJListSymbols().setModel(new DefaultListModel());
		}
		weakImageCache.clear();
		weakSymbolPreviewComponentsCache.clear();

		AtlasSwingWorker<Void> symbolLoader = getWorker();
		symbolLoader.executeModalNoEx();
	}

	long lastTimeJScrollPaneUpdate = System.currentTimeMillis();

	/**
	 * @return A SwingWorker that adds the Online-Symbols in a background task.
	 * 
	 * @author <a href="mailto:skpublic@wikisquare.de">Stefan Alfons Tzeggai</a>
	 */
	private AtlasSwingWorker<Void> getWorker() {
		final AtlasStatusDialog sd = new AtlasStatusDialog(
				JScrollPaneSymbolsOnline.this, OpenMapSymbols.BASE_URL,
				"Updating Online Symbol list"); // i8n TODO
		AtlasSwingWorker<Void> swingWorker = new AtlasSwingWorker<Void>(sd) {

			@Override
			protected Void doInBackground() {
				try {

					LOGGER.info("Seaching for online Symbols at " + url);

					// String a = url.toExternalForm();
					// a += "/index";
					// URL index = new URL(a);

					URL index = DataUtilities.extendURL(url, "index");

					BufferedReader in = null;
					try {
						in = new BufferedReader(new InputStreamReader(
								index.openStream()));

						// Sorting them alphabetically by using a set
						SortedSet<String> symbolURLStrings = new TreeSet<String>();
						String oneLIne;
						while ((oneLIne = in.readLine()) != null) {
							String lastPartInURI = new File(
									oneLIne.substring(1)).toURI().getRawPath();
							// Thats what happens on Windows: lastPartInURI =
							// "C:/"
							// + lastPartInURI;
							// Ich work arroundthat.. not very ellegant!
							// LOGGER.debug("lastpartinURI " + lastPartInURI);
							if (lastPartInURI.matches(".[A-Z]:.+")) {
								lastPartInURI = lastPartInURI.substring(3);
							}
							String string = url.toExternalForm()
									+ lastPartInURI;
							// LOGGER.debug("string " + string);
							symbolURLStrings.add(new URL(string).toString());
						}

						List<URL> symbolURLs = new ArrayList<URL>();
						for (String urlStr : symbolURLStrings) {
							symbolURLs.add(new URL(urlStr));
						}

						List<SingleRuleList<Symbolizer>> newElements = new ArrayList<SingleRuleList<Symbolizer>>();

						final DefaultListModel model = (DefaultListModel) getJListSymbols()
								.getModel();
						/**
						 * Add every symbol as a SymbolButton
						 */
						for (final URL url : symbolURLs) {

							/*******************************************************
							 * Checking if a Style with the same name allready
							 * exists
							 */
							// Name without .sld
							final String newNameWithOUtSLD = url.getFile()
									.substring(0, url.getFile().length() - 4);

							Enumeration<?> name2 = model.elements();
							while (name2.hasMoreElements()) {
								String styleName = ((SingleRuleList) name2
										.nextElement()).getStyleName();
								if (styleName.equals(newNameWithOUtSLD)) {
									// A Symbol with the same StyleName already
									// exits
									continue;
								}
							}

							final SingleRuleList symbolRuleList;

							switch (geoForm) {
							case POINT:
								symbolRuleList = new SinglePointSymbolRuleList(
										"");
								break;
							case LINE:
								symbolRuleList = new SingleLineSymbolRuleList(
										"");
								break;
							case ANY:
							case POLYGON:
								symbolRuleList = new SinglePolygonSymbolRuleList(
										"");
								break;

							case NONE:
							default:
								throw new IllegalStateException(
										"unrecognized type");
							}

							boolean b = symbolRuleList.loadURL(url);
							// TODO collect updates with timer
							if (b) {

								String key = JScrollPaneSymbolsOnline.this
										.getClass().getSimpleName()
										+ symbolRuleList.getStyleName()
										+ symbolRuleList.getStyleTitle()
										+ symbolRuleList.getStyleAbstract();

								JPanel fullCell = getOrCreateComponent(
										symbolRuleList, key);

								newElements.add(symbolRuleList);
								sd.setDescription(newNameWithOUtSLD);

							} else {
								// Load failed
								LOGGER.warn("Loading " + url + " failed");
							}
						}

						// Add the collected images
						for (SingleRuleList<Symbolizer> newElement : newElements) {
							model.addElement(newElement);
						}
						updateJScrollPane();

					} catch (IOException e) {
						JLabel notOnlineLabel = new JLabel(
								AtlasStyler
										.R("JScrollPaneSymbolsOnline.notOnlineErrorLabel"));
						JScrollPaneSymbolsOnline.this
								.setViewportView(notOnlineLabel);
					} catch (Exception e) {
						ExceptionDialog
								.show(SwingUtil
										.getParentWindowComponent(JScrollPaneSymbolsOnline.this),
										e);
					} finally {
						if (in != null)
							try {
								in.close();
							} catch (IOException e) {
								LOGGER.error(e);
							}
					}

					return null;
				} catch (MalformedURLException e1) {
					ExceptionDialog.show(e1);
					return null;
				}

			}

		};
		return swingWorker;
	}

	@Override
	protected String getToolTip() {
		return AtlasStyler.R("SymbolSelector.Tabs.OnlineSymbols.TT");
	}

	@Override
	protected JPopupMenu getPopupMenu() {
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();

			popupMenu.add(new JPopupMenu.Separator());
			/*******************************************************************
			 * Rescan directory
			 */
			JMenuItem rescan = new JMenuItem(
					AtlasStyler
							.R("SymbolSelector.Tabs.OnlineSymbols.Action.Rescan"));
			rescan.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					rescan(true);
				}

			});
			popupMenu.add(rescan);

		}
		return popupMenu;
	}

}
