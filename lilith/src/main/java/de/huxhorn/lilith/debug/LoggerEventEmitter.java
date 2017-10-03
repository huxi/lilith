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

package de.huxhorn.lilith.debug;

import de.huxhorn.lilith.debug.exceptions.CauseCauseCauseException;
import de.huxhorn.lilith.debug.exceptions.CauseCauseException;
import de.huxhorn.lilith.debug.exceptions.CauseException;
import de.huxhorn.lilith.debug.exceptions.SuppressedException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class LoggerEventEmitter
{
	private static final int DEFAULT_DELAY = 50;
	private int delay = DEFAULT_DELAY;
	private final Marker marker;
	private final Marker fnordMarker;
	private final ExecutorService executor;

	LoggerEventEmitter()
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

	void logStuff()
	{
		execute(new LogStuffRunnable(delay, null));
	}

	void logNDC()
	{
		execute(new LogNdcRunnable(delay));
	}

	private void execute(Runnable runnable)
	{
		executor.execute(runnable);
	}

	void logStuffWithMdc()
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

	void logStuffWithMdcAndMarker()
	{
		execute(new LogStuffWithMdcRunnable(delay, marker));
	}

	void logStuffWithMarker()
	{
		execute(new LogStuffRunnable(delay, marker));
	}

	@SuppressWarnings({"ThrowableInstanceNeverThrown"})
	void logException()
	{
		Throwable ex = new RuntimeException("Test-Exception");
		execute(new LogThrowableRunnable(delay, ex));
	}

	@SuppressWarnings({"ThrowableInstanceNeverThrown"})
	void logException2()
	{
		Exception causeCause = new CauseCauseException("CauseCause-Exception", new CauseCauseCauseException("Inline CauseCauseCause-Exception"));
		Exception cause = new CauseException("Cause-Exception", causeCause);
		Throwable ex = new RuntimeException("Root-Exception", cause);
		execute(new LogThrowableRunnable(delay, ex));
	}

	@SuppressWarnings({"ThrowableInstanceNeverThrown"})
	void logExceptionSuppressed()
	{
		Exception causeCause = new CauseCauseException("Suppressed - CauseCause-Exception", new CauseCauseCauseException("Inline CauseCauseCause-Exception"));
		Exception cause = new CauseException("Suppressed - Cause-Exception", causeCause);
		Exception supCause = new CauseException("Suppressed - Cause-Exception in Suppressed1");
		Exception sup1 = new SuppressedException("Suppressed1", supCause);
		sup1.addSuppressed(new SuppressedException("Suppressed1-1"));
		sup1.addSuppressed(new SuppressedException("Suppressed1-2"));
		cause.addSuppressed(sup1);
		cause.addSuppressed(new SuppressedException("Suppressed2"));
		cause.addSuppressed(new SuppressedException("Suppressed3"));
		Throwable ex = new RuntimeException("Suppressed - Root-Exception", cause);
		execute(new LogThrowableRunnable(delay, ex));
	}

	@SuppressWarnings({"ThrowableInstanceNeverThrown"})
	void logParamException()
	{
		Throwable ex = new RuntimeException("Test-Exception");
		execute(new LogParamThrowableRunnable(delay, ex));
	}

	@SuppressWarnings({"ThrowableInstanceNeverThrown"})
	void logParamException2()
	{
		Exception causeCause = new CauseCauseException("CauseCause-Exception", new CauseCauseCauseException("Inline CauseCauseCause-Exception"));
		Exception cause = new CauseException("Cause-Exception", causeCause);
		Throwable ex = new RuntimeException("Root-Exception", cause);
		execute(new LogParamThrowableRunnable(delay, ex));
	}

	void logAnonymous()
	{
		execute(new LogAnonymousRunnable(delay));
		logLambda();
	}

	private void logLambda()
	{
		execute(() -> LoggerFactory.getLogger(this.getClass()).info("Info from Lambda"));
	}

	void logASCII()
	{
		execute(new LogASCIIRunnable(delay));
	}


	void logTruth()
	{
		execute(new LogTruthRunnable(delay, fnordMarker, true));
		execute(new LogTruthRunnable(delay, fnordMarker, false));
	}

	void logDate()
	{
		execute(new LogDateRunnable(delay));
	}

	void logContainers()
	{
		execute(new LogContainerRunnable(delay));
	}

	void logJul()
	{
		execute(new LogJulRunnable(delay));
	}
}
