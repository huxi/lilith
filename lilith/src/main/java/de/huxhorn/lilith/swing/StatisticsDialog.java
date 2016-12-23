/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2015 Joern Huxhorn
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

import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.swing.statistics.StatisticsPanel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.JDialog;

public class StatisticsDialog
	extends JDialog
{
	private static final long serialVersionUID = -6745606522649465902L;

	private StatisticsPanel statisticsPanel;
	private MainFrame mainFrame;

	public StatisticsDialog(MainFrame owner)
	{
		super(owner);
		this.mainFrame = owner;
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(false);
		setResizable(false);
		if(Icons.STATISTICS_MENU_ICON != null)
		{
			setIconImage(Icons.STATISTICS_MENU_ICON.getImage());
		}

		statisticsPanel = new StatisticsPanel(owner);
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
				Object newValue = evt.getNewValue();
				if(newValue instanceof SourceIdentifier)
				{
					SourceIdentifier sourceIdentifier = (SourceIdentifier) newValue;

					ApplicationPreferences applicationPreferences = mainFrame.getApplicationPreferences();
					Map<String, String> sourceNames = null;
					boolean showingPrimaryIdentifier = false;
					if(applicationPreferences != null)
					{
						sourceNames = applicationPreferences.getSourceNames();
						showingPrimaryIdentifier = applicationPreferences.isShowingPrimaryIdentifier();
					}
					String title = ViewActions.getPrimarySourceTitle(sourceIdentifier.getIdentifier(), sourceNames, showingPrimaryIdentifier);
					setTitle("Statistics for '" + title + "'...");
				}
			}
		}
	}
}
