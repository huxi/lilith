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

package de.huxhorn.lilith.swing.menu;

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.swing.ApplicationPreferences;
import de.huxhorn.lilith.swing.actions.AbstractAccessFilterAction;
import de.huxhorn.lilith.swing.actions.AbstractLoggingFilterAction;
import de.huxhorn.lilith.swing.actions.EventWrapperRelated;
import de.huxhorn.lilith.swing.actions.FilterAction;
import de.huxhorn.lilith.swing.actions.ViewContainerRelated;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JMenuItem;

class AbstractFilterMainMenu
	extends AbstractFilterMenu
	implements ConditionNamesAware
{
	private static final long serialVersionUID = -33150466502573127L;

	private final List<EventWrapperRelated> eventWrapperRelatedList=new ArrayList<>();
	private final List<ViewContainerRelated> viewContainerRelatedList=new ArrayList<>();
	private final List<ConditionNamesAware> conditionNamesAwareList=new ArrayList<>();
	private final List<Component> loggingComponents=new ArrayList<>();
	private final List<Component> accessComponents=new ArrayList<>();

	protected final boolean htmlTooltip;
	protected final ApplicationPreferences applicationPreferences;

	AbstractFilterMainMenu(Action action, ApplicationPreferences applicationPreferences, boolean htmlTooltip)
	{
		super(action);
		this.htmlTooltip = htmlTooltip;
		this.applicationPreferences = applicationPreferences;
		JLabel hintLabel = new JLabel("Hold " + KeyEvent.getModifiersExText(KeyEvent.SHIFT_DOWN_MASK) + " to create new view.");
		registerLoggingComponent(hintLabel);
		registerLoggingComponent(null);
		registerAccessComponent(hintLabel);
		registerAccessComponent(null);
	}

	void registerFilterAction(FilterAction filterAction)
	{
		eventWrapperRelatedList.add(filterAction);
		viewContainerRelatedList.add(filterAction);
	}

	void registerAbstractFilterMenu(AbstractFilterMenu filterMenu)
	{
		eventWrapperRelatedList.add(filterMenu);
		viewContainerRelatedList.add(filterMenu);
		if(filterMenu instanceof ConditionNamesAware)
		{
			conditionNamesAwareList.add((ConditionNamesAware)filterMenu);
		}
	}

	final void registerLoggingComponent(Component component)
	{
		loggingComponents.add(component);
	}

	final void registerAccessComponent(Component component)
	{
		accessComponents.add(component);
	}

	@Override
	protected void viewContainerUpdated()
	{
		for (ViewContainerRelated current : viewContainerRelatedList)
		{
			current.setViewContainer(viewContainer);
		}
		super.viewContainerUpdated();
	}

	@Override
	public void setEventWrapper(EventWrapper eventWrapper)
	{
		removeAll();

		for (EventWrapperRelated current : eventWrapperRelatedList)
		{
			current.setEventWrapper(eventWrapper);
		}

		LoggingEvent loggingEvent = AbstractLoggingFilterAction.resolveLoggingEvent(eventWrapper);
		if(loggingEvent != null)
		{
			registerComponents(loggingComponents);
			setEnabled(true);
			return;
		}

		AccessEvent accessEvent = AbstractAccessFilterAction.resolveAccessEvent(eventWrapper);
		if(accessEvent != null)
		{
			registerComponents(accessComponents);
			setEnabled(true);
			return;
		}
		setEnabled(false);
	}

	private void registerComponents(List<Component> components)
	{
		for (Component current : components)
		{
			if(current == null)
			{
				addSeparator();
			}
			else if(current instanceof JMenuItem)
			{
				add((JMenuItem)current);
			}
			else
			{
				add(current);
			}
		}
	}

	@Override
	public void setConditionNames(List<String> conditionNames)
	{
		for (ConditionNamesAware current : conditionNamesAwareList)
		{
			current.setConditionNames(conditionNames);
		}
	}
}
