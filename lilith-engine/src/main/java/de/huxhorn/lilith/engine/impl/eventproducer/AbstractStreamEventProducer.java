/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2015 Joern Huxhorn
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

/*
 * Copyright 2007-2015 Joern Huxhorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.huxhorn.lilith.engine.impl.eventproducer;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.sulky.buffers.AppendOperation;

import de.huxhorn.sulky.io.IOUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractStreamEventProducer<T extends Serializable>
	extends AbstractEventProducer<T>
{
	final Logger logger = LoggerFactory.getLogger(AbstractStreamEventProducer.class);

	private static final Set<String> WHITELIST;

	static
	{
		Set<String> whitelist = new HashSet<>();
		whitelist.add("[Lch.qos.logback.classic.spi.IThrowableProxy;");
		whitelist.add("[Lch.qos.logback.classic.spi.StackTraceElementProxy;");
		whitelist.add("[Ljava.lang.Object;");
		whitelist.add("[Ljava.lang.StackTraceElement;");
		whitelist.add("[Ljava.lang.String;");
		whitelist.add("[Lorg.apache.logging.log4j.Marker;");
		whitelist.add("[Lorg.apache.logging.log4j.core.impl.ExtendedStackTraceElement;");
		whitelist.add("[Lorg.apache.logging.log4j.core.impl.ThrowableProxy;");
		whitelist.add("ch.qos.logback.access.spi.AccessEvent");
		whitelist.add("ch.qos.logback.classic.spi.ClassPackagingData");
		whitelist.add("ch.qos.logback.classic.spi.LoggerContextVO");
		whitelist.add("ch.qos.logback.classic.spi.LoggingEventVO");
		whitelist.add("ch.qos.logback.classic.spi.StackTraceElementProxy");
		whitelist.add("ch.qos.logback.classic.spi.ThrowableProxyVO");
		whitelist.add("java.lang.Enum");
		whitelist.add("java.lang.StackTraceElement");
		whitelist.add("java.lang.String$CaseInsensitiveComparator");
		whitelist.add("java.util.ArrayList");
		whitelist.add("java.util.Collections$EmptyMap");
		whitelist.add("java.util.Collections$SynchronizedMap");
		whitelist.add("java.util.Collections$UnmodifiableCollection");
		whitelist.add("java.util.Collections$UnmodifiableList");
		whitelist.add("java.util.Collections$UnmodifiableMap");
		whitelist.add("java.util.HashMap");
		whitelist.add("java.util.Hashtable");
		whitelist.add("java.util.LinkedHashMap");
		whitelist.add("java.util.TreeMap");
		whitelist.add("java.util.Vector");
		whitelist.add("org.apache.log4j.spi.LocationInfo");
		whitelist.add("org.apache.log4j.spi.LoggingEvent");
		whitelist.add("org.apache.log4j.spi.ThrowableInformation");
		whitelist.add("org.apache.logging.log4j.Level");
		whitelist.add("org.apache.logging.log4j.MarkerManager$Log4jMarker");
		whitelist.add("org.apache.logging.log4j.ThreadContext$EmptyThreadContextStack");
		whitelist.add("org.apache.logging.log4j.core.impl.ExtendedClassInfo");
		whitelist.add("org.apache.logging.log4j.core.impl.ExtendedStackTraceElement");
		whitelist.add("org.apache.logging.log4j.core.impl.Log4jLogEvent$LogEventProxy");
		whitelist.add("org.apache.logging.log4j.core.impl.ThrowableProxy");
		whitelist.add("org.apache.logging.log4j.message.FormattedMessage");
		whitelist.add("org.apache.logging.log4j.message.MapMessage");
		whitelist.add("org.apache.logging.log4j.message.MessageFormatMessage");
		whitelist.add("org.apache.logging.log4j.message.ObjectArrayMessage");
		whitelist.add("org.apache.logging.log4j.message.ObjectMessage");
		whitelist.add("org.apache.logging.log4j.message.ParameterizedMessage");
		whitelist.add("org.apache.logging.log4j.message.SimpleMessage");
		whitelist.add("org.apache.logging.log4j.message.StringFormattedMessage");
		whitelist.add("org.apache.logging.log4j.message.StructuredDataId");
		whitelist.add("org.apache.logging.log4j.message.StructuredDataMessage");
		whitelist.add("org.apache.logging.log4j.message.ThreadDumpMessage$ThreadDumpMessageProxy");
		whitelist.add("org.apache.logging.log4j.spi.MutableThreadContextStack");
		whitelist.add("org.apache.logging.log4j.spi.StandardLevel");
		whitelist.add("org.slf4j.helpers.BasicMarker");

		WHITELIST = Collections.unmodifiableSet(whitelist);
	}

	private ObjectInputStream dataInput;

	public AbstractStreamEventProducer(SourceIdentifier sourceIdentifier, AppendOperation<EventWrapper<T>> eventQueue, SourceIdentifierUpdater<T> sourceIdentifierUpdater, InputStream inputStream)
		throws IOException
	{
		super(sourceIdentifier, eventQueue, sourceIdentifierUpdater);
		this.dataInput = new WhitelistObjectInputStream(new BufferedInputStream(inputStream), WHITELIST, false /*, true*/);
	}

	public void start()
	{
		Thread t = new Thread(new ReceiverRunnable(), "" + getSourceIdentifier() + "-Receiver");
		t.setDaemon(false);
		t.start();
	}

	protected abstract T postProcessEvent(Object o);

	private class ReceiverRunnable
		implements Runnable
	{
		public void run()
		{
			for(; ;)
			{
				try
				{
					Object object = dataInput.readObject();

					T event = postProcessEvent(object);

					if(object == null)
					{
						if(logger.isInfoEnabled()) logger.info("Retrieved null!");
					}
					else
					{
						addEvent(event);
					}
				}
				catch(IOException e)
				{
					if(logger.isDebugEnabled()) logger.debug("Exception ({}: '{}') while reading events. Adding eventWrapper with empty event and stopping...", e.getClass().getName(), e.getMessage(), e);
					addEvent(null);
					IOUtilities.interruptIfNecessary(e);
					break;
				}
				catch(Throwable e)
				{
					if(logger.isWarnEnabled()) logger.warn("Exception ({}: '{}') while reading events. Adding eventWrapper with empty event and stopping...", e.getClass().getName(), e.getMessage(), e);
					addEvent(null);
					IOUtilities.interruptIfNecessary(e);
					break;
				}
			}
		}
	}

	public void close()
	{
		IOUtilities.closeQuietly(dataInput);
	}
}
