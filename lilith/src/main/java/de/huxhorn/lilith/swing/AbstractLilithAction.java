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

package de.huxhorn.lilith.swing;

import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.Action;

public abstract class AbstractLilithAction
	extends AbstractAction
{
	private static final long serialVersionUID = 4621724216557651699L;
	private final LilithActionId id;
	private final boolean toolbar;

	public AbstractLilithAction(LilithActionId id)
	{
		this(id, false);
	}

	public AbstractLilithAction(LilithActionId id, boolean toolbar)
	{
		this.id = Objects.requireNonNull(id, "id must not be null!");
		this.toolbar = toolbar;
		initProperties();
	}

	private void initProperties()
	{
		putValue(Action.ACCELERATOR_KEY, LilithKeyStrokes.getKeyStroke(id));
		String text = id.getText();
		String description=id.getDescription();
		if(text != null)
		{
			if(text.equals(description))
			{
				description = null;
			}
		}
		putValue(Action.SHORT_DESCRIPTION, description);
		if(toolbar)
		{
			putValue(Action.SMALL_ICON, Icons.resolveToolbarIcon(id));
		}
		else
		{
			putValue(Action.NAME, text);
			putValue(Action.MNEMONIC_KEY, id.getMnemonic());
			putValue(Action.SMALL_ICON, Icons.resolveMenuIcon(id));
		}
	}

	public LilithActionId getId()
	{
		return id;
	}

	public boolean isToolbar()
	{
		return toolbar;
	}
}
