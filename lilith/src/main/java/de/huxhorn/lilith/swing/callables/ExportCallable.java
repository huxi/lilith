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

package de.huxhorn.lilith.swing.callables;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.buffers.FileBuffer;
import de.huxhorn.sulky.tasks.AbstractProgressingCallable;
import java.io.Serializable;

public class ExportCallable<T extends Serializable>
	extends AbstractProgressingCallable<Long>
{
	private final Buffer<EventWrapper<T>> input;
	private final FileBuffer<EventWrapper<T>> output;

	public ExportCallable(Buffer<EventWrapper<T>> input, FileBuffer<EventWrapper<T>> output)
	{
		this.input = input;
		this.output = output;
	}

	public Buffer<EventWrapper<T>> getInput()
	{
		return input;
	}

	public FileBuffer<EventWrapper<T>> getOutput()
	{
		return output;
	}

	@Override
	public Long call() throws Exception
	{
		long size= input.getSize();
		setNumberOfSteps(size);
		for(long i=0;i<size;i++)
		{
			setCurrentStep(i);
			output.add(input.get(i));
		}
		return size;
	}
}
