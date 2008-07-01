/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
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
package de.huxhorn.lilith.swing;

import javax.swing.*;

import de.huxhorn.lilith.swing.statistics.*;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.awt.Image;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatisticsDialog
	extends JDialog
{
	private final Logger logger = LoggerFactory.getLogger(StatisticsDialog.class);

	private StatisticsPanel statisticsPanel;
	private MainFrame mainFrame;

	public StatisticsDialog(MainFrame owner)
	{
		super(owner);
		this.mainFrame=owner;
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(false);
		setResizable(false);
		{
			Throwable error=null;
			try
			{
				Method setIconMethod = StatisticsDialog.class.getMethod("setIconImage", Image.class);

				URL url=EventWrapperViewPanel.class.getResource("/tango/16x16/apps/utilities-system-monitor.png");
				if(url!=null)
				{
					ImageIcon icon = new ImageIcon(url);
					setIconMethod.invoke(this, icon.getImage());
				}
			}
			catch (NoSuchMethodException e)
			{
				if(logger.isInfoEnabled()) logger.info("No setIconImage-method found...");
			}
			catch (IllegalAccessException e)
			{
				error=e;
			}
			catch (InvocationTargetException e)
			{
				error=e;
			}
			if(error!=null)
			{
				if(logger.isWarnEnabled()) logger.warn("Exception while executing setIconImage-method!", error);
			}

		}

		statisticsPanel=new StatisticsPanel(owner);
		statisticsPanel.addPropertyChangeListener(new SourceChangeListener());
		setContentPane(statisticsPanel);
	}

	public void setSourceIdentifier(SourceIdentifier sourceIdentifier)
	{
		statisticsPanel.setSourceIdentifier(sourceIdentifier);
	}

	public SourceIdentifier getSourceIdentifier()
	{
		return statisticsPanel.getSourceIdentifier();
	}

	private class SourceChangeListener
		implements PropertyChangeListener
	{

		/**
		 * This method gets called when a bound property is changed.
		 *
		 * @param evt A PropertyChangeEvent object describing the event source
		 *            and the property that has changed.
		 */

		public void propertyChange(PropertyChangeEvent evt)
		{
			if(StatisticsPanel.SOURCE_IDENTIFIER_PROPERTY.equals(evt.getPropertyName()))
			{
				Object newValue=evt.getNewValue();
				if(newValue instanceof SourceIdentifier)
				{
					SourceIdentifier sourceIdentifier=(SourceIdentifier) newValue;
					setTitle("Statistics for '"+mainFrame.getPrimarySourceTitle(sourceIdentifier)+"'...");
				}
			}
		}
	}
}
