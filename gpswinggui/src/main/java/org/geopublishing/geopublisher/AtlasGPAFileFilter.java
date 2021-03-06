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
package org.geopublishing.geopublisher;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.geopublishing.atlasViewer.AtlasConfig;


/**
 * A {@link FileFilter} that only show valid atlas.gpa files.
 */
public class AtlasGPAFileFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		return f.isDirectory()
				|| (f.getName().toLowerCase().endsWith("gpa") && AtlasConfig
						.isAtlasDir(f.getParentFile()));
	}

	@Override
	public String getDescription() {
		return AtlasConfigEditable.ATLAS_GPA_FILENAME;
	}
}
