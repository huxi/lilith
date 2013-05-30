/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2013 Joern Huxhorn
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

import de.huxhorn.lilith.conditions.FormattedMessageEqualsCondition;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.swing.TextPreprocessor;
import de.huxhorn.sulky.conditions.Condition;

import javax.swing.*;

public class FocusFormattedMessageAction
		extends AbstractLoggingFilterAction
{
	private static final long serialVersionUID = -1245643497938628684L;
	private String formattedMessage;

	public FocusFormattedMessageAction()
	{
		super("Formatted message");
	}

	protected void setFormattedMessage(String formattedMessage)
	{
		this.formattedMessage = formattedMessage;

		putValue(Action.SHORT_DESCRIPTION, TextPreprocessor.preformattedTooltip(TextPreprocessor.cropTextBlock(formattedMessage)));

		setEnabled(formattedMessage != null);
	}

	@Override
	protected void updateState()
	{
		if(viewContainer == null)
		{
			setFormattedMessage(null);
			return;
		}

		String formattedMessage = null;
		if(loggingEvent != null)
		{
			// formattedMessage / messagePattern
			Message message = loggingEvent.getMessage();
			if(message != null)
			{
				formattedMessage = message.getMessage();
			}
		}
		setFormattedMessage(formattedMessage);
	}

	@Override
	public Condition resolveCondition()
	{
		if(formattedMessage == null)
		{
			return null;
		}
		return new FormattedMessageEqualsCondition(formattedMessage);
	}
}
