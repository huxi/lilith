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

import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.swing.MainFrame;

import org.rrd4j.graph.RrdGraph;
import org.rrd4j.graph.RrdGraphDef;
import org.rrd4j.graph.RrdGraphInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class AbstractGraphImageProducer
	implements GraphImageProducer
{
	private final Logger logger = LoggerFactory.getLogger(AbstractGraphImageProducer.class);

	protected MainFrame mainFrame;
	protected SimpleDateFormat dateFormat;
	protected Dimension graphSize;

	public AbstractGraphImageProducer(MainFrame mainFrame)
	{
		super();
		this.graphSize = new Dimension(600, 400);
		this.mainFrame = mainFrame;
		this.dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	}

	protected File getRrdFile(SourceIdentifier sourceIdentifier)
	{
		File statisticsParent = new File(mainFrame
			.getApplicationPreferences().getStartupApplicationPath(), "statistics");
		return new File(statisticsParent, sourceIdentifier.getIdentifier() + ".rrd");
	}

	protected String createGraphTitle(SourceIdentifier sourceIdentifier)
	{
		return mainFrame.getPrimarySourceTitle(sourceIdentifier) + " @ " + dateFormat.format(new Date());
	}

	public BufferedImage createGraphImage(long nowInSeconds, SourceIdentifier sourceIdentifier, BufferedImage result, boolean showMax)
	{
		RrdGraphDef graphDef = getGraphDef(nowInSeconds, sourceIdentifier, showMax);
		synchronized(graphDef)
		{

			RrdGraph graph;
			try
			{
				graph = new RrdGraph(graphDef);
				RrdGraphInfo graphInfo = graph.getRrdGraphInfo();
				int width = graphInfo.getWidth();
				int height = graphInfo.getHeight();
				if(result != null && (result.getWidth() != width || result.getHeight() != height))
				{
					if(logger.isInfoEnabled()) logger.info("Flushing previous image because of wrong size.");
					result.flush();
					result = null;
				}
				if(result == null)
				{
					result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
					if(logger.isInfoEnabled()) logger.info("Created new image.");
				}
				graph.render(result.getGraphics());
			}
			catch(IOException ex)
			{
				if(logger.isWarnEnabled()) logger.warn("Exception while creating graph!", ex);
				if(result != null)
				{
					result.flush();
					result = null;
				}
			}
		}
		return result;

	}

	public abstract RrdGraphDef getGraphDef(long nowInSeconds, SourceIdentifier sourceIdentifier, boolean showMax);

}
