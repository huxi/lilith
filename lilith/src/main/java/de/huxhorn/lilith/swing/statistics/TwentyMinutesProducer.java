/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2009 Joern Huxhorn
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

import de.huxhorn.lilith.consumers.RrdLoggingEventConsumer;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.swing.MainFrame;

import org.rrd4j.ConsolFun;
import org.rrd4j.graph.RrdGraphConstants;
import org.rrd4j.graph.RrdGraphDef;

import java.awt.*;

public class TwentyMinutesProducer
	extends AbstractGraphImageProducer
{
	public TwentyMinutesProducer(MainFrame mainFrame)
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
			.setTimeAxis(RrdGraphConstants.SECOND, 15, RrdGraphConstants.MINUTE, 1, RrdGraphConstants.MINUTE, 5, 0, "HH:mm:ss");
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
			.datasource(RrdLoggingEventConsumer.TRACE, absoluteRrdPath, RrdLoggingEventConsumer.TRACE_DS_NAME, consolFun);
		graphDef
			.datasource(RrdLoggingEventConsumer.DEBUG, absoluteRrdPath, RrdLoggingEventConsumer.DEBUG_DS_NAME, consolFun);
		graphDef
			.datasource(RrdLoggingEventConsumer.INFO, absoluteRrdPath, RrdLoggingEventConsumer.INFO_DS_NAME, consolFun);
		graphDef
			.datasource(RrdLoggingEventConsumer.WARN, absoluteRrdPath, RrdLoggingEventConsumer.WARN_DS_NAME, consolFun);
		graphDef
			.datasource(RrdLoggingEventConsumer.ERROR, absoluteRrdPath, RrdLoggingEventConsumer.ERROR_DS_NAME, consolFun);

		graphDef.area(RrdLoggingEventConsumer.TRACE, new Color(0x00, 0x00, 0xff), RrdLoggingEventConsumer.TRACE);
		graphDef.stack(RrdLoggingEventConsumer.DEBUG, new Color(0x00, 0xff, 0x00), RrdLoggingEventConsumer.DEBUG);
		graphDef.stack(RrdLoggingEventConsumer.INFO, new Color(0xff, 0xff, 0xff), RrdLoggingEventConsumer.INFO);
		graphDef.stack(RrdLoggingEventConsumer.WARN, new Color(0xff, 0xff, 0x00), RrdLoggingEventConsumer.WARN);
		graphDef.stack(RrdLoggingEventConsumer.ERROR, new Color(0xff, 0x00, 0x00), RrdLoggingEventConsumer.ERROR);

		if(showMax)
		{
			graphDef
				.datasource(RrdLoggingEventConsumer.TOTAL, absoluteRrdPath, RrdLoggingEventConsumer.TOTAL_DS_NAME, consolFun);
			graphDef.line(RrdLoggingEventConsumer.TOTAL, Color.BLACK, RrdLoggingEventConsumer.TOTAL);
		}

		graphDef.setAntiAliasing(true);
		graphDef.setLazy(false);

		String sourceTitle = createGraphTitle(sourceIdentifier);
		long before = nowInSeconds - 1200;
		graphDef.setTimeSpan(before, nowInSeconds);
		graphDef.setTitle(sourceTitle);
		graphDef.setWidth(graphSize.width);
		graphDef.setHeight(graphSize.height);

		return graphDef;
	}
}
