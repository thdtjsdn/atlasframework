/*******************************************************************************
 * Copyright (c) 2010 Stefan A. Tzeggai.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Stefan A. Tzeggai - initial API and implementation
 ******************************************************************************/
package org.geopublishing.geopublisher.gui.map;

import java.awt.event.ActionEvent;
import java.util.LinkedList;

import javax.swing.AbstractAction;

import org.geopublishing.atlasViewer.AtlasRefInterface;
import org.geopublishing.atlasViewer.dp.Group;
import org.geopublishing.atlasViewer.exceptions.AtlasException;
import org.geopublishing.atlasViewer.map.Map;
import org.geopublishing.atlasViewer.map.MapPool;
import org.geopublishing.atlasViewer.swing.Icons;
import org.geopublishing.geopublisher.swing.GeopublisherGUI;

import de.schmitzm.swing.ExceptionDialog;
import de.schmitzm.swing.SwingUtil;

public class MapPoolDeleteAction extends AbstractAction {

	private final MapPoolJTable mapPoolJTable;
	private MapPool mapPool;

	public MapPoolDeleteAction(MapPoolJTable mapPoolJTable) {
		super(GeopublisherGUI.R("MapPool.Action.DeleteMap"),
				Icons.ICON_REMOVE);

		this.mapPoolJTable = mapPoolJTable;
		this.mapPool = mapPoolJTable.getMapPool();
	}

	public void actionPerformed(ActionEvent e) {
		int idx = mapPoolJTable.getSelectedRow();

		final Map map2delete = mapPool.get(mapPoolJTable
				.convertRowIndexToModel(idx));

		if (!SwingUtil.askYesNo(mapPoolJTable, GeopublisherGUI.R(
				"MapPool.Action.DeleteMap.Question", map2delete.getTitle()))) {
			return;
		}

		// Delete references to this map in the Groups
		Group.findReferencesTo(mapPoolJTable.getAce().getRootGroup(),
				map2delete, new LinkedList<AtlasRefInterface<?>>(), true);

		Map removed = mapPool.remove(map2delete.getId());
		if (removed == null) {
			ExceptionDialog.show(mapPoolJTable, new AtlasException(
					"The map we tried to delete is not in the MapPool?!"));
		}
	}

}
