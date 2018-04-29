/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2018 Joern Huxhorn
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

import de.huxhorn.lilith.swing.actions.BasicFilterAction;
import de.huxhorn.lilith.swing.actions.FocusHttpRequestUriAction;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

class FocusHttpRequestUriMenu
	extends AbstractAccessFilterMenu
{
	private static final long serialVersionUID = -6901950716124475185L;

	FocusHttpRequestUriMenu()
	{
		super("Request URI");

		setViewContainer(null);
	}

	@Override
	protected void updateState()
	{
		removeAll();
		String uri = null;
		if (accessEvent != null) {
			uri = accessEvent.getRequestURI();
		}

		if(uri == null)
		{
			setEnabled(false);
			return;
		}

		boolean added = false;
		for (String current : prepareUris(uri))
		{
			BasicFilterAction filterAction = createAction(current);
			filterAction.setViewContainer(viewContainer);
			add(filterAction);
			added = true;
		}
		setEnabled(added);
	}

	protected BasicFilterAction createAction(String uri)
	{
		return new FocusHttpRequestUriAction(uri);
	}

	static List<String> prepareUris(String uri)
	{
		List<String> tokens = new ArrayList<>();
		StringTokenizer tok = new StringTokenizer(uri, "/", false);
		while(tok.hasMoreTokens())
		{
			String current=tok.nextToken();
			tokens.add(current);
		}

		List<String> result=new ArrayList<>(tokens.size());
		for(int i=tokens.size();i>0;i--)
		{
			StringBuilder builder=new StringBuilder(); // NOPMD - AvoidInstantiatingObjectsInLoops
			for(int j=0;j<i;j++)
			{
				builder.append('/');
				builder.append(tokens.get(j));
			}
			result.add(builder.toString());
		}
		return result;
	}

}
