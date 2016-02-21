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
package de.huxhorn.lilith.swing.statistics;

import de.huxhorn.lilith.eventhandlers.RrdLoggingEventHandler;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.swing.MainFrame;

import org.rrd4j.ConsolFun;
import org.rrd4j.graph.RrdGraphConstants;
import org.rrd4j.graph.RrdGraphDef;

import java.awt.*;

public class TenHoursProducer
	extends AbstractGraphImageProducer
{
	public TenHoursProducer(MainFrame mainFrame)
	{
		super(mainFrame);
	}

	public RrdGraphDef getGraphDef(long nowInSeconds, SourceIdentifier sourceIdentifier, boolean showMax)
	{
		String absoluteRrdPath = getRrdFile(sourceIdentifier).getAbsolutePath();

		RrdGraphDef graphDef = new RrdGraphDef();

		graphDef.setColor(RrdGraphConstants.COLOR_CANVAS, new Color(0xcc, 0xcc, 0xcc));
		graphDef.setNoMinorGrid(true);
		graphDef.setShowSignature(false);
		graphDef.setMinValue(0);
		graphDef.setAltAutoscaleMax(true);
		graphDef.setAltYGrid(false);
		graphDef
			.setTimeAxis(RrdGraphConstants.MINUTE, 15, RrdGraphConstants.HOUR, 1, RrdGraphConstants.HOUR, 2, 0, "HH:mm");
		graphDef.setFilename("-");
		graphDef.setImageFormat("PNG");

		ConsolFun consolFun;
		String description;
		if(showMax)
		{
			consolFun = ConsolFun.MAX;
			description = " (max.)";
		}
		else
		{
			consolFun = ConsolFun.AVERAGE;
			description = " (avg.)";
		}
		graphDef.setVerticalLabel("Events/s" + description);

		graphDef
			.datasource(RrdLoggingEventHandler.TRACE, absoluteRrdPath, RrdLoggingEventHandler.TRACE_DS_NAME, consolFun);
		graphDef
			.datasource(RrdLoggingEventHandler.DEBUG, absoluteRrdPath, RrdLoggingEventHandler.DEBUG_DS_NAME, consolFun);
		graphDef
			.datasource(RrdLoggingEventHandler.INFO, absoluteRrdPath, RrdLoggingEventHandler.INFO_DS_NAME, consolFun);
		graphDef
			.datasource(RrdLoggingEventHandler.WARN, absoluteRrdPath, RrdLoggingEventHandler.WARN_DS_NAME, consolFun);
		graphDef
			.datasource(RrdLoggingEventHandler.ERROR, absoluteRrdPath, RrdLoggingEventHandler.ERROR_DS_NAME, consolFun);

		graphDef.area(RrdLoggingEventHandler.TRACE, new Color(0x00, 0x00, 0xff), RrdLoggingEventHandler.TRACE);
		graphDef.stack(RrdLoggingEventHandler.DEBUG, new Color(0x00, 0xff, 0x00), RrdLoggingEventHandler.DEBUG);
		graphDef.stack(RrdLoggingEventHandler.INFO, new Color(0xff, 0xff, 0xff), RrdLoggingEventHandler.INFO);
		graphDef.stack(RrdLoggingEventHandler.WARN, new Color(0xff, 0xff, 0x00), RrdLoggingEventHandler.WARN);
		graphDef.stack(RrdLoggingEventHandler.ERROR, new Color(0xff, 0x00, 0x00), RrdLoggingEventHandler.ERROR);

		if(showMax)
		{
			graphDef
				.datasource(RrdLoggingEventHandler.TOTAL, absoluteRrdPath, RrdLoggingEventHandler.TOTAL_DS_NAME, consolFun);
			graphDef.line(RrdLoggingEventHandler.TOTAL, Color.BLACK, RrdLoggingEventHandler.TOTAL);
		}

		graphDef.setAntiAliasing(true);
		graphDef.setLazy(false);

		String sourceTitle = createGraphTitle(sourceIdentifier);
		long before = nowInSeconds - 10 * 60 * 60;
		graphDef.setTimeSpan(before, nowInSeconds);
		graphDef.setTitle(sourceTitle);
		graphDef.setWidth(graphSize.width);
		graphDef.setHeight(graphSize.height);
		return graphDef;
	}
}
