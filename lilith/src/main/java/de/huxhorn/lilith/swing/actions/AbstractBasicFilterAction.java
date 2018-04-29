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

import de.huxhorn.lilith.swing.ViewContainer;
import de.huxhorn.sulky.conditions.Condition;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public abstract class AbstractBasicFilterAction
	extends AbstractAction
	implements BasicFilterAction
{
	private static final long serialVersionUID = 7262183597944496045L;

	protected transient ViewContainer viewContainer;
	protected final boolean htmlTooltip;

	protected AbstractBasicFilterAction(String name, boolean htmlTooltip)
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

	@Override
	public final ViewContainer getViewContainer()
	{
		return viewContainer;
	}

	protected void viewContainerUpdated()
	{
		setEnabled(viewContainer != null);
	}

	@Override
	public abstract Condition resolveCondition(ActionEvent e);

	@Override
	public final void actionPerformed(ActionEvent e)
	{
		if(this.viewContainer == null)
		{
			return;
		}
		Condition condition = resolveCondition(e);
		if(condition == null)
		{
			return;
		}
		viewContainer.applyCondition(condition, e);
	}

	protected boolean isAlternativeBehaviorRequested(ActionEvent e)
	{
		return e != null && (e.getModifiers() & ActionEvent.ALT_MASK) != 0;
	}

	protected void initializeCroppedTooltip(String tooltip)
	{
		ActionTooltips.initializeCroppedTooltip(tooltip, this, htmlTooltip);
	}

	protected void initializeConditionTooltip(Condition condition)
	{
		ActionTooltips.initializeConditionTooltip(condition, this, htmlTooltip);
	}

}
