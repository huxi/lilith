/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2010 Joern Huxhorn
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
package de.huxhorn.lilith.eventhandlers;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.engine.EventHandler;

import org.rrd4j.ConsolFun;
import org.rrd4j.DsType;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDbPool;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;
import org.rrd4j.core.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RrdLoggingEventHandler
	implements EventHandler<LoggingEvent>

{
	private final Logger logger = LoggerFactory.getLogger(RrdLoggingEventHandler.class);
	public static final String TRACE = "" + LoggingEvent.Level.TRACE;
	public static final String DEBUG = "" + LoggingEvent.Level.DEBUG;
	public static final String INFO = "" + LoggingEvent.Level.INFO;
	public static final String WARN = "" + LoggingEvent.Level.WARN;
	public static final String ERROR = "" + LoggingEvent.Level.ERROR;

	public static final String TOTAL = "TOTAL";

	public static final String TRACE_DS_NAME = TRACE + "!";
	public static final String DEBUG_DS_NAME = DEBUG + "!";
	public static final String INFO_DS_NAME = INFO + "!";
	public static final String WARN_DS_NAME = WARN + "!";
	public static final String ERROR_DS_NAME = ERROR + "!";

	public static final String TOTAL_DS_NAME = TOTAL + "!";

	private RrdDbPool pool = RrdDbPool.getInstance();

	private long lastUpdateTime;
	private File basePath;
	private boolean enabled;
	private Map<String, EventCounter> eventCounters;

	public RrdLoggingEventHandler()
	{
		this.enabled=true;
	}

	public File getBasePath()
	{
		return basePath;
	}

	public void setBasePath(File basePath)
	{
		this.basePath = basePath;
	}

	public void handle(List<EventWrapper<LoggingEvent>> events)
	{
		if(!enabled)
		{
			return;
		}
		EventCounter globalCounter;
		if(eventCounters == null)
		{
			eventCounters = new HashMap<String, EventCounter>();
			globalCounter = new EventCounter();
			eventCounters.put("global", globalCounter);
		}
		else
		{
			globalCounter=eventCounters.get("global");
		}
		for(EventWrapper<LoggingEvent> wrapper : events)
		{
			LoggingEvent event = wrapper.getEvent();
			if(event != null)
			{
				SourceIdentifier si = wrapper.getSourceIdentifier();
				String primary = si.getIdentifier();
				EventCounter eventCounter;
				if(eventCounters.containsKey(primary))
				{
					eventCounter = eventCounters.get(primary);
				}
				else
				{
					eventCounter = new EventCounter();
					eventCounters.put(primary, eventCounter);
				}
				eventCounter.counters.get(TOTAL_DS_NAME).increase();
				globalCounter.counters.get(TOTAL_DS_NAME).increase();
				LoggingEvent.Level level = event.getLevel();
				if(LoggingEvent.Level.TRACE.equals(level))
				{
					eventCounter.counters.get(TRACE_DS_NAME).increase();
					globalCounter.counters.get(TRACE_DS_NAME).increase();
				}
				else if(LoggingEvent.Level.DEBUG.equals(level))
				{
					eventCounter.counters.get(DEBUG_DS_NAME).increase();
					globalCounter.counters.get(DEBUG_DS_NAME).increase();
				}
				else if(LoggingEvent.Level.INFO.equals(level))
				{
					eventCounter.counters.get(INFO_DS_NAME).increase();
					globalCounter.counters.get(INFO_DS_NAME).increase();
				}
				else if(LoggingEvent.Level.WARN.equals(level))
				{
					eventCounter.counters.get(WARN_DS_NAME).increase();
					globalCounter.counters.get(WARN_DS_NAME).increase();
				}
				else if(LoggingEvent.Level.ERROR.equals(level))
				{
					eventCounter.counters.get(ERROR_DS_NAME).increase();
					globalCounter.counters.get(ERROR_DS_NAME).increase();
				}
			}
		} // for
		// collected events in eventCounters...
		updateRrdFile();
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	private void updateRrdFile()
	{
		if(eventCounters != null && eventCounters.size() > 0)
		{
			long timestamp = Util.getTime();
			//long creationTS=timestamp-1000;
			if(System.currentTimeMillis()-lastUpdateTime > 1000)
			{
				for(Map.Entry<String, EventCounter> current : eventCounters.entrySet())
				{
					String primary = current.getKey();
					EventCounter eventCounter = current.getValue();
					File rrdFile = new File(basePath, primary + ".rrd");
					if(!rrdFile.isFile())
					{
						try
						{
							createRrdFile(rrdFile, timestamp - 1);
						}
						catch(IOException ex)
						{
							if(logger.isWarnEnabled())
							{
								logger.warn("Exception while creating rrd-db '" + rrdFile.getAbsolutePath() + "'!", ex);
							}
						}
					}

					RrdDb rrd = null;
					try
					{
						//rrd = new RrdDb(rrdFile.getAbsolutePath());
						rrd = pool.requestRrdDb(rrdFile.getAbsolutePath());
						Sample sample = rrd.createSample(timestamp);
						for(Map.Entry<String, Counter> curCount : eventCounter.counters.entrySet())
						{
							String name = curCount.getKey();
							Counter counter = curCount.getValue();
							int counterValue = counter.getValue();
							sample.setValue(name, counterValue);
							if(logger.isDebugEnabled())
							{
								logger
									.debug("Setting rrd-db '{}' with counterValue[{}] {}.", new Object[]{rrdFile.getAbsolutePath(), name, counterValue});
							}
						}
						sample.update();
					}
					catch(IOException ex)
					{
						if(logger.isWarnEnabled())
						{
							logger.warn("Exception while updating rrd-db '" + rrdFile.getAbsolutePath() + "'!", ex);
						}
					}
					finally
					{
						if(rrd != null)
						{
							try
							{
								pool.release(rrd);
								//rrd.close();
							}
							catch(IOException ex)
							{
								if(logger.isWarnEnabled())
								{
									logger
										.warn("Exception while releasing rrd-db '" + rrdFile.getAbsolutePath() + "'!", ex);
								}
							}
						}
					}
				} // for
				lastUpdateTime = System.currentTimeMillis();
				eventCounters=null;
			}
		}
	}

	private void createRrdFile(File rrdFile, long timestamp)
		throws IOException
	{
		final int stepSize = 2; // every 2 seconds
		final int heartbeat = stepSize * 2;
		File parent = rrdFile.getParentFile();
		parent.mkdirs();

		RrdDef rrdDef = new RrdDef(rrdFile.getAbsolutePath());
		rrdDef.setStartTime(timestamp);
		rrdDef.setStep(stepSize);
		rrdDef.addDatasource(TRACE_DS_NAME, DsType.ABSOLUTE, heartbeat, Double.NaN, Double.NaN);
		rrdDef.addDatasource(DEBUG_DS_NAME, DsType.ABSOLUTE, heartbeat, Double.NaN, Double.NaN);
		rrdDef.addDatasource(INFO_DS_NAME, DsType.ABSOLUTE, heartbeat, Double.NaN, Double.NaN);
		rrdDef.addDatasource(WARN_DS_NAME, DsType.ABSOLUTE, heartbeat, Double.NaN, Double.NaN);
		rrdDef.addDatasource(ERROR_DS_NAME, DsType.ABSOLUTE, heartbeat, Double.NaN, Double.NaN);
		rrdDef.addDatasource(TOTAL_DS_NAME, DsType.ABSOLUTE, heartbeat, Double.NaN, Double.NaN);
		// all archives have an additional 50 values for safety
		rrdDef.addArchive(ConsolFun.AVERAGE, 0.5, 1, 650); // 600*2 second samples => 20 mins
		rrdDef.addArchive(ConsolFun.AVERAGE, 0.7, 6, 650); // 6*600*2 samples => 2 hours
		rrdDef.addArchive(ConsolFun.AVERAGE, 0.8, 30, 650); // 5*6*600*2 samples => 10 hours
		rrdDef.addArchive(ConsolFun.AVERAGE, 0.999, 72, 650); // 72*600*2 samples => 1 days
		rrdDef.addArchive(ConsolFun.AVERAGE, 0.999, 30 * 72, 650); // 30*72*600*2 samples => 30 days
		rrdDef.addArchive(ConsolFun.AVERAGE, 0.999, 90 * 72, 650); // 30*72*600*2 samples => 90 days
		rrdDef.addArchive(ConsolFun.AVERAGE, 0.999, 26352, 650); // (72*366)*600*2 samples => 1 year
		rrdDef.addArchive(ConsolFun.MAX, 0.5, 1, 650); // 600*2 second samples => 20 mins
		rrdDef.addArchive(ConsolFun.MAX, 0.7, 6, 650); // 6*600*2 samples => 2 hours
		rrdDef.addArchive(ConsolFun.MAX, 0.8, 30, 650); // 5*6*600*2 samples => 10 hours
		rrdDef.addArchive(ConsolFun.MAX, 0.999, 72, 650); // 72*600*2 samples => 1 days
		rrdDef.addArchive(ConsolFun.MAX, 0.999, 30 * 72, 650); // 30*72*600*2 samples => 30 days
		rrdDef.addArchive(ConsolFun.MAX, 0.999, 90 * 72, 650); // 30*72*600*2 samples => 90 days
		rrdDef.addArchive(ConsolFun.MAX, 0.999, 26352, 650); // (72*366)*600*2 samples => 1 year
		RrdDb rrdDb = new RrdDb(rrdDef);
		rrdDb.close();
		if(logger.isInfoEnabled()) logger.info("Created rrd-db '" + rrdFile.getAbsolutePath() + "'.");
	}

	private static class EventCounter
	{
		public final Map<String, Counter> counters;

		public EventCounter()
		{
			counters = new HashMap<String, Counter>(6);
			counters.put(TRACE_DS_NAME, new Counter());
			counters.put(DEBUG_DS_NAME, new Counter());
			counters.put(INFO_DS_NAME, new Counter());
			counters.put(WARN_DS_NAME, new Counter());
			counters.put(ERROR_DS_NAME, new Counter());
			counters.put(TOTAL_DS_NAME, new Counter());
		}
	}

	private static class Counter
	{
		private int value = 0;

		public void increase()
		{
			value++;
		}

		public int getValue()
		{
			return value;
		}

	}
}
