/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.huxhorn.lilith.logback.appender;

import ch.qos.logback.classic.spi.LoggingEvent;
import de.huxhorn.sulky.generics.io.SerializableSerializer;
import de.huxhorn.sulky.generics.io.Serializer;
import de.huxhorn.lilith.data.logging.logback.LogbackLoggingAdapter;

public class ClassicMultiplexSocketAppender
	extends MultiplexSocketAppenderBase<LoggingEvent>
{
	/**
	 * The default port number of compressed new-style remote logging server (10000).
	 */
	public static final int COMRESSED_DEFAULT_PORT = 10000;

	/**
	 * The default port number of uncompressed new-style remote logging server (10001).
	 */
	public static final int UNCOMRESSED_DEFAULT_PORT = 10001;

	private boolean includeCallerData;
	private boolean compressing;
	private boolean usingDefaultPort;
	private Serializer<de.huxhorn.lilith.data.logging.LoggingEvent> lilithSerializer;

	public ClassicMultiplexSocketAppender()
	{
		this(true);
	}

	public ClassicMultiplexSocketAppender(boolean compressing)
	{
		super();
		usingDefaultPort=true;
		setCompressing(compressing);
		includeCallerData=false;
	}

	@Override
	public void setPort(int port)
	{
		super.setPort(port);
		usingDefaultPort=false;
	}

	/**
	 * GZIPs the event if set to true.
	 *
	 * Automatically chooses the correct default port if it was not previously set manually.
	 *
	 * @param compressing if events will be gzipped or not.
	 */
	public void setCompressing(boolean compressing)
	{
		this.compressing = compressing;
		if(usingDefaultPort)
		{
			if(compressing)
			{
				setPort(COMRESSED_DEFAULT_PORT);
			}
			else
			{
				setPort(UNCOMRESSED_DEFAULT_PORT);
			}
			usingDefaultPort=true;
		}
		// setSerializer(new SerializableSerializer<LoggingEvent>(compressing));
		lilithSerializer =new SerializableSerializer<de.huxhorn.lilith.data.logging.LoggingEvent>(compressing);
		setSerializer(new TransformingSerializer());
	}

	public boolean isCompressing()
	{
		return compressing;
	}

	public boolean isIncludeCallerData()
	{
		return includeCallerData;
	}

	public void setIncludeCallerData(boolean includeCallerData)
	{
		this.includeCallerData = includeCallerData;
	}

	protected void preProcess(LoggingEvent event)
	{
		if(event!=null)
		{
			if (includeCallerData)
			{
			  event.getCallerData();
			}
		}
//		// HACK: http://bugzilla.qos.ch/show_bug.cgi?id=100
//		try
//		{
//			Field field=LoggingEvent.class.getDeclaredField("argumentArray");
//			field.setAccessible(true);
//			Object aa=field.get(event);
//			if(aa instanceof Object[])
//			{
//				Object[] argumentArray=(Object[]) aa;
//
//				for(int i=0;i<argumentArray.length;i++)
//				{
//					Object current=argumentArray[i];
//					if(current!=null && !(current instanceof String))
//					{
//						argumentArray[i]=""+current;
//					}
//				}
//			}
//		}
//		catch (NoSuchFieldException e)
//		{
//			//e.printStackTrace();
//		}
//		catch (IllegalAccessException e)
//		{
//			//e.printStackTrace();
//		}
	}

	private class TransformingSerializer
		implements Serializer<LoggingEvent>
	{
		LogbackLoggingAdapter adapter=new LogbackLoggingAdapter();

		public byte[] serialize(LoggingEvent logbackEvent)
		{
			de.huxhorn.lilith.data.logging.LoggingEvent lilithEvent = adapter.convert(logbackEvent);
			lilithEvent.setApplicationIdentifier(getApplicationIdentifier());
			return lilithSerializer.serialize(lilithEvent);
		}
	}
}
