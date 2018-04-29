/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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

package de.huxhorn.lilith.swing.table.tooltips;

import de.huxhorn.lilith.DateTimeFormatters;
import de.huxhorn.lilith.swing.table.TooltipGenerator;
import de.huxhorn.lilith.swing.table.renderer.TimestampRenderer;
import java.time.Instant;
import javax.swing.JTable;

public class TimestampTooltipGenerator
	implements TooltipGenerator
{

	@Override
	public String createTooltipText(JTable table, int row)
	{
		String tooltip = null;
		Object value = table.getValueAt(row, 0);
		Instant instant = TimestampRenderer.resolveInstant(value);
		if(instant != null)
		{
			tooltip = DateTimeFormatters.DATETIME_IN_SYSTEM_ZONE_SPACE.format(instant);
		}
		return tooltip;
	}

}
