/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2016 Joern Huxhorn
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
package de.huxhorn.lilith.swing.actions;

import de.huxhorn.lilith.conditions.MessagePatternEqualsCondition;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.sulky.conditions.Condition;

public class FocusMessagePatternAction
		extends AbstractLoggingFilterAction
{
	private static final long serialVersionUID = -4237035769242851225L;
	private String messagePattern;

	public FocusMessagePatternAction(boolean htmlTooltip)
	{
		super("Message pattern", htmlTooltip);
	}

	protected void setMessagePattern(String messagePattern)
	{
		this.messagePattern = messagePattern;

		initializeCroppedTooltip(messagePattern);

		setEnabled(messagePattern != null);
	}

	@Override
	protected void updateState()
	{
		String messagePattern = null;
		if(loggingEvent != null)
		{
			// formattedMessage / messagePattern
			Message message = loggingEvent.getMessage();
			if(message != null)
			{
				String formattedMessage = message.getMessage();
				messagePattern = message.getMessagePattern();
				if(formattedMessage != null)
				{
					if(formattedMessage.equals(messagePattern))
					{
						messagePattern = null;
					}
				}
			}
		}
		setMessagePattern(messagePattern);
	}

	@Override
	public Condition resolveCondition()
	{
		if(messagePattern == null)
		{
			return null;
		}
		return new MessagePatternEqualsCondition(messagePattern);
	}
}
