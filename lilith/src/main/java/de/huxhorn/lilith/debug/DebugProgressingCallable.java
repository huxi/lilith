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

package de.huxhorn.lilith.debug;

import de.huxhorn.sulky.tasks.AbstractProgressingCallable;

public class DebugProgressingCallable
	extends AbstractProgressingCallable<Long>
{
	@Override
	public Long call()
		throws Exception
	{
		Thread.sleep(2000);
		setNumberOfSteps(100);
		for(int i = 0; i <= 100; i++)
		{
			setCurrentStep(i);
			Thread.sleep(500);
		}

		return 31_337L;
	}
}
