/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2014 Joern Huxhorn
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

package de.huxhorn.lilith.swing.uiprocessors;

import de.huxhorn.lilith.swing.ViewContainer;
import java.util.List;

public class PreviousSearchStringsContainerProcessor
	implements ViewContainerProcessor
{
	private final List<String> previousSearchStrings;

	public PreviousSearchStringsContainerProcessor(List<String> previousSearchStrings)
	{
		this.previousSearchStrings = previousSearchStrings;
	}

	@Override
	public void process(ViewContainer<?> container)
	{
		container.setPreviousSearchStrings(previousSearchStrings);
	}
}
