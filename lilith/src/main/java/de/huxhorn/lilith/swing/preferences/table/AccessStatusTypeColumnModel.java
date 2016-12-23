/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.huxhorn.lilith.swing.preferences.table;

import de.huxhorn.lilith.data.access.HttpStatus;
import de.huxhorn.lilith.swing.table.ColorScheme;
import java.util.Map;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

public class AccessStatusTypeColumnModel
	extends DefaultTableColumnModel
{
	public static final int DEFAULT_COLUMN_INDEX_INDEX = 0;
	public static final int DEFAULT_COLUMN_INDEX_PREVIEW = 1;

	private static final String DEFAULT_COLUMN_NAME_INDEX = "#";
	private static final String DEFAULT_COLUMN_NAME_PREVIEW = "Preview";

	private AccessStatusTypePreviewRenderer previewRenderer;

	public AccessStatusTypeColumnModel()
	{
		super();

		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_INDEX);
			col.setCellRenderer(new IndexRenderer());
			addColumn(col);
		}

		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_PREVIEW);
			previewRenderer = new AccessStatusTypePreviewRenderer();
			col.setCellRenderer(previewRenderer);
			addColumn(col);
		}
	}

	public Map<HttpStatus.Type, ColorScheme> getSchemes()
	{
		return previewRenderer.getSchemes();
	}

	public void setSchemes(Map<HttpStatus.Type, ColorScheme> schemes)
	{
		previewRenderer.setSchemes(schemes);
	}
}
