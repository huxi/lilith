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

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.swing.TextPreprocessor;
import de.huxhorn.lilith.swing.ViewContainer;
import de.huxhorn.sulky.conditions.Condition;

import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.event.ActionEvent;

public abstract class AbstractFilterAction
	extends AbstractAction
	implements FilterAction
{
	private static final long serialVersionUID = -8702163293653882073L;

	protected transient ViewContainer viewContainer;
	private final boolean htmlTooltip;


	protected AbstractFilterAction(String name, boolean htmlTooltip)
	{
		super(name);
		this.htmlTooltip = htmlTooltip;
	}

	@Override
	public final void setViewContainer(ViewContainer viewContainer) {
		if(this.viewContainer != viewContainer)
		{
			this.viewContainer = viewContainer;
			viewContainerUpdated();
		}
	}

	private void viewContainerUpdated()
	{
		EventWrapper eventWrapper = null;
		if(viewContainer != null)
		{
			eventWrapper = viewContainer.getSelectedEvent();
		}
		setEventWrapper(eventWrapper);
	}

	@Override
	public ViewContainer getViewContainer()
	{
		return viewContainer;
	}

	@Override
	public final void actionPerformed(ActionEvent e)
	{
		if(this.viewContainer == null)
		{
			return;
		}
		Condition condition = resolveCondition();
		if(condition == null)
		{
			return;
		}
		viewContainer.applyCondition(condition, e);
	}

	/**
	 * To be called after setEventWrapper.
	 */
	protected abstract void updateState();

	protected void initializeCroppedTooltip(String tooltip)
	{
		initializeCroppedTooltip(tooltip, this, htmlTooltip);
	}

	protected void initializeConditionTooltip(Condition condition)
	{
		initializeConditionTooltip(condition, this, htmlTooltip);
	}

	public abstract void setEventWrapper(EventWrapper eventWrapper);

	public abstract Condition resolveCondition();

	public static  void initializeConditionTooltip(Condition condition, Action action, boolean htmlTooltip)
	{
		initializeCroppedTooltip(TextPreprocessor.formatCondition(condition), action, htmlTooltip);
	}

	public static  void initializeCroppedTooltip(String tooltip, Action action, boolean htmlTooltip)
	{
		tooltip = TextPreprocessor.cropTextBlock(tooltip);
		if(htmlTooltip)
		{
			tooltip = TextPreprocessor.preformattedTooltip(tooltip);
		}
		action.putValue(Action.SHORT_DESCRIPTION, tooltip);
	}
}
