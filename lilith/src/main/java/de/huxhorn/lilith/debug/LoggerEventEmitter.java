/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
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

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;


public class LoggerEventEmitter
{
	private static final int DEFAULT_DELAY = 50;
	private int delay=DEFAULT_DELAY;
	private Marker marker;
	private Marker fnordMarker;
	private ExecutorService executor;

	public LoggerEventEmitter()
	{
		marker = createMarker();
		fnordMarker = createFnordMarker();
		executor = Executors.newCachedThreadPool();
	}

	public int getDelay()
	{
		return delay;
	}

	public void setDelay(int delay)
	{
		this.delay = delay;
	}

	public void logStuff() throws InterruptedException
	{
		execute(new LogStuffRunnable(delay, null));
	}

	private void execute(Runnable runnable)
	{
		executor.execute(runnable);
	}

	public void logStuffWithMdc() throws InterruptedException
	{
		execute(new LogStuffWithMdcRunnable(delay, null));
	}

	private Marker createMarker()
	{

		Marker marker = MarkerFactory.getMarker("FooBar");
		Marker marker1 = MarkerFactory.getMarker("Foo");
		Marker marker2 = MarkerFactory.getMarker("Bar");
		Marker marker3 = MarkerFactory.getMarker("Recursive");

		marker.add(marker1);
		marker.add(marker2);
		marker.add(marker3);
		marker3.add(marker);
		return marker;
	}

	private Marker createFnordMarker()
	{
		Marker eris = MarkerFactory.getMarker("Hail Eris!!");
		Marker discordia = MarkerFactory.getMarker("All Hail Discordia!!!");
		eris.add(discordia);
		return eris;
	}

	public void logStuffWithMdcAndMarker() throws InterruptedException
	{
		execute(new LogStuffWithMdcRunnable(delay, marker));
	}

	public void logStuffWithMarker() throws InterruptedException
	{
		execute(new LogStuffRunnable(delay, marker));
	}

	public void logException() throws InterruptedException
	{
		//noinspection ThrowableInstanceNeverThrown
		Throwable ex=new RuntimeException("Test-Exception");
		execute(new LogThrowableRunnable(delay, ex));
	}

	public void logParamException() throws InterruptedException
	{
		//noinspection ThrowableInstanceNeverThrown
		Throwable ex=new RuntimeException("Test-Exception");
		execute(new LogParamThrowableRunnable(delay, ex));
	}

	public void logAnonymous()
	{
		execute(new LogAnonymousRunnable(delay));
	}

	public void logException2() throws InterruptedException
	{
		//noinspection ThrowableInstanceNeverThrown
		Throwable ex=new RuntimeException("Another Test-Exception", new RuntimeException("Test-Exception"));
		execute(new LogThrowableRunnable(delay, ex));
	}

	public void logSkull()
	{
		execute(new LogSkullRunnable(delay));
	}


	public void logTruth()
	{
		execute(new LogTruthRunnable(delay, fnordMarker));
	}
}
