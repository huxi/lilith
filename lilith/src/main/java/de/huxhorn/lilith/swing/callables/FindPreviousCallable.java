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
package de.huxhorn.lilith.swing.callables;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.swing.table.model.EventWrapperTableModel;
import de.huxhorn.sulky.conditions.Condition;
import de.huxhorn.sulky.tasks.AbstractProgressingCallable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class FindPreviousCallable<T extends Serializable>
	extends AbstractProgressingCallable<Long>
{
	private final Logger logger = LoggerFactory.getLogger(FindPreviousCallable.class);

	private int currentRow;
	private Condition condition;
	private EventWrapperTableModel<T> tableModel;

	public FindPreviousCallable(EventWrapperTableModel<T> tableModel, int currentRow, Condition condition)
	{
		super(200, 1000);
		this.tableModel = tableModel;
		this.currentRow = currentRow;
		this.condition = condition;
	}

	public Long call()
		throws Exception
	{
		int row = currentRow;
		if(row > -1)
		{
			row--;
			if(logger.isInfoEnabled()) logger.info("Searching previous starting at {}.", row);
			tableModel.getRowCount();
			int maxCount = row;
			int numberOfSteps = maxCount - 1;
			if(numberOfSteps < 1)
			{
				numberOfSteps = 1;
			}
			setNumberOfSteps(numberOfSteps);
			for(int i = 0; i < maxCount; i++)
			{
				setCurrentStep(i);
				int current = row - i;
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
						if(logger.isInfoEnabled()) logger.info("Found previous at {}.", current);
						return (long) current;
					}
				}
				else
				{
					if(logger.isWarnEnabled()) logger.warn("Unexpected class! {}", obj.getClass().getName());
				}
			}
		}
		if(logger.isInfoEnabled()) logger.info("Didn't find previous.");
		return -1L;
	}
}
