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

package de.huxhorn.lilith.swing.callables;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.swing.EventWrapperViewPanel;
import de.huxhorn.lilith.swing.table.model.EventWrapperTableModel;
import de.huxhorn.sulky.conditions.Condition;
import de.huxhorn.sulky.tasks.AbstractProgressingCallable;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindNextCallable<T extends Serializable>
	extends AbstractProgressingCallable<Long>
{
	private final Logger logger = LoggerFactory.getLogger(FindNextCallable.class);

	private final EventWrapperViewPanel<T> viewPanel;
	private final EventWrapperTableModel<T> tableModel;
	private final int currentRow;
	private final Condition condition;

	public FindNextCallable(EventWrapperViewPanel<T> viewPanel, int currentRow, Condition condition)
	{
		super(200, 1000);
		this.viewPanel = viewPanel;
		this.tableModel = viewPanel.getTableModel();
		this.currentRow = currentRow;
		this.condition = condition;
	}

	public EventWrapperViewPanel<T> getViewPanel()
	{
		return viewPanel;
	}

	@Override
	public Long call()
		throws Exception
	{
		int row = currentRow;
		if(row > -1)
		{
			row++;
			if(logger.isInfoEnabled()) logger.info("Searching next starting at {}.", row);

			int maxCount = tableModel.getRowCount() - row;
			int numberOfSteps = maxCount - 1;
			if(numberOfSteps < 1)
			{
				numberOfSteps = 1;
			}
			setNumberOfSteps(numberOfSteps);
			for(int i = 0; i < maxCount; i++)
			{
				setCurrentStep(i);
				int current = i + row;
				if(logger.isDebugEnabled()) logger.debug("Processing row {}", current);
				Object obj = tableModel.getValueAt(current, 0);
				if(obj == null)
				{
					return -1L;
				}
				if(obj instanceof EventWrapper)
				{
					if(condition.isTrue(obj))
					{
						if(logger.isInfoEnabled()) logger.info("Found next at {}.", current);
						return (long) current;
					}
				}
				else
				{
					if(logger.isWarnEnabled()) logger.warn("Unexpected class! {}", obj.getClass().getName());
				}
			}
		}
		if(logger.isInfoEnabled()) logger.info("Didn't find next.");
		return -1L;
	}
}
