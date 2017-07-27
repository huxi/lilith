/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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
 * Copyright 2007-2017 Joern Huxhorn
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

package de.huxhorn.lilith.engine.impl.eventproducer

import ch.qos.logback.classic.spi.LoggingEventVO
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import spock.lang.Specification
import spock.lang.Unroll

class SerializableWhitelistSpec extends Specification{
	private static final Logger LOGGER = LoggerFactory.getLogger(SerializableEventProducer.class);
	@Unroll
	"deserialization works for #valueClass: #value"() {
		when:
		writeAndRead(value)

		then:
		noExceptionThrown()

		where:
		value << validValues()
		valueClass = value.class.name
	}

	List<Serializable> validValues() {
		List<Serializable> result = []

		result += "Foo"

		ch.qos.logback.classic.spi.LoggingEvent logbackLoggingEvent = new ch.qos.logback.classic.spi.LoggingEvent(
				"foo.bar",
				(ch.qos.logback.classic.Logger)LOGGER,
				ch.qos.logback.classic.Level.DEBUG,
				"message {}",
				new Throwable(),
				[new File(".")] as Object[]
		)
		MDC.put("MDK-Key", "MDC-Value")
		logbackLoggingEvent.prepareForDeferredProcessing()
		result += LoggingEventVO.build(logbackLoggingEvent)

		return result
	}

	@Unroll
	"deserialization fails for #valueClass: #value"() {
		when:
		writeAndRead(value)

		then:
		ClassNotFoundException ex = thrown()
		ex.message.contains('Unauthorized deserialization attempt!')

		where:
		value << invalidValues()
		valueClass = value.class.name
	}

	List<Serializable> invalidValues() {
		List<Serializable> result = []

		result += new File('.')

		return result
	}

	static void writeAndRead(Serializable serializable) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream()
		ObjectOutputStream oos = new ObjectOutputStream(bos)
		oos.writeObject(serializable)
		oos.close()
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray())
		WhitelistObjectInputStream ois = new WhitelistObjectInputStream(bis, SerializableWhitelist.WHITELIST)
		ois.readObject()
	}
}
