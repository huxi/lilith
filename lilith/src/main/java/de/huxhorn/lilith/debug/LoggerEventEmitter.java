/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2015 Joern Huxhorn
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class LoggerEventEmitter
{
	private static final int DEFAULT_DELAY = 50;
	private int delay = DEFAULT_DELAY;
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

	public void logStuff()
	{
		execute(new LogStuffRunnable(delay, null));
	}

	public void logNDC()
	{
		execute(new LogNdcRunnable(delay));
	}

	private void execute(Runnable runnable)
	{
		executor.execute(runnable);
	}

	public void logStuffWithMdc()
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
		// can't really create recursive markers anymore because Ceki disabled them.

		return marker;
	}

	private Marker createFnordMarker()
	{
		Marker eris = MarkerFactory.getMarker("Hail Eris!!");
		Marker discordia = MarkerFactory.getMarker("All Hail Discordia!!!");
		eris.add(discordia);
		return eris;
	}

	public void logStuffWithMdcAndMarker()
	{
		execute(new LogStuffWithMdcRunnable(delay, marker));
	}

	public void logStuffWithMarker()
	{
		execute(new LogStuffRunnable(delay, marker));
	}

	@SuppressWarnings({"ThrowableInstanceNeverThrown"})
	public void logException()
	{
		Throwable ex = new RuntimeException("Test-Exception");
		execute(new LogThrowableRunnable(delay, ex));
	}

	@SuppressWarnings({"ThrowableInstanceNeverThrown"})
	public void logException2()
	{
		Exception causeCause = new RuntimeException("CauseCause-Exception", new RuntimeException("Inline CauseCauseCause-Exception"));
		Exception cause = new RuntimeException("Cause-Exception", causeCause);
		Throwable ex = new RuntimeException("Another Test-Exception", cause);
		execute(new LogThrowableRunnable(delay, ex));
	}

	@SuppressWarnings({"ThrowableInstanceNeverThrown"})
	public void logExceptionSuppressed()
	{
		Exception causeCause = new RuntimeException("Suppressed - CauseCause-Exception", new RuntimeException("Inline CauseCauseCause-Exception"));
		Exception cause = new RuntimeException("Suppressed - Cause-Exception", causeCause);
		cause.addSuppressed(new RuntimeException("Suppressed1"));
		cause.addSuppressed(new RuntimeException("Suppressed2"));
		cause.addSuppressed(new RuntimeException("Suppressed3"));
		Throwable ex = new RuntimeException("Suppressed - Root-Exception", cause);
		execute(new LogThrowableRunnable(delay, ex));
	}

	@SuppressWarnings({"ThrowableInstanceNeverThrown"})
	public void logParamException()
	{
		Throwable ex = new RuntimeException("Test-Exception");
		execute(new LogParamThrowableRunnable(delay, ex));
	}

	@SuppressWarnings({"ThrowableInstanceNeverThrown"})
	public void logParamException2()
	{
		Exception causeCause = new RuntimeException("CauseCause-Exception", new RuntimeException("Inline CauseCauseCause-Exception"));
		Exception cause = new RuntimeException("Cause-Exception", causeCause);
		Throwable ex = new RuntimeException("Another Test-Exception", cause);
		execute(new LogParamThrowableRunnable(delay, ex));
	}

	public void logAnonymous()
	{
		execute(new LogAnonymousRunnable(delay));
	}

	public void logASCII()
	{
		execute(new LogASCIIRunnable(delay));
	}


	public void logTruth()
	{
		execute(new LogTruthRunnable(delay, fnordMarker, true));
		execute(new LogTruthRunnable(delay, fnordMarker, false));
	}

	public void logDate()
	{
		execute(new LogDateRunnable(delay));
	}
}
