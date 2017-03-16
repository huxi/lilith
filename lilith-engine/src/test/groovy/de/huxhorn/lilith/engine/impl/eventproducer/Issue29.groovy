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

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.classic.spi.LoggingEventVO
import org.slf4j.Marker
import org.slf4j.MarkerFactory
import spock.lang.Specification

class Issue29 extends Specification {

	def "issue 29"() {
		setup:
		Marker fooMarker = MarkerFactory.getDetachedMarker("foo-marker")
		Marker barMarker = MarkerFactory.getDetachedMarker("bar-marker")
		fooMarker.add(barMarker)

		Set<String> whitelist = ['org.slf4j.helpers.BasicMarker', 'java.util.concurrent.CopyOnWriteArrayList']
		def bytes = WhitelistObjectInputStreamSpec.serialize(fooMarker)


		when:
		ByteArrayInputStream is = new ByteArrayInputStream(bytes)
		WhitelistObjectInputStream instance = new WhitelistObjectInputStream(is, whitelist)
		Marker read = (Marker) instance.readObject()

		then:
		read == fooMarker
		read.contains('bar-marker')
		read.hasReferences()
	}

	def "issue 29 second test"() {
		setup:
		Marker fooMarker = MarkerFactory.getDetachedMarker("foo-marker")
		Marker barMarker = MarkerFactory.getDetachedMarker("bar-marker")
		fooMarker.add(barMarker)
		Issue29Object object = new Issue29Object()
		object.marker = fooMarker

		Set<String> whitelist = ['org.slf4j.helpers.BasicMarker', 'java.util.concurrent.CopyOnWriteArrayList', 'de.huxhorn.lilith.engine.impl.eventproducer.Issue29$Issue29Object']
		def bytes = WhitelistObjectInputStreamSpec.serialize(object)


		when:
		ByteArrayInputStream is = new ByteArrayInputStream(bytes)
		WhitelistObjectInputStream instance = new WhitelistObjectInputStream(is, whitelist)
		Marker read = ((Issue29Object) instance.readObject()).marker

		then:
		read == fooMarker
		read.contains('bar-marker')
		read.hasReferences()
	}

	static class Issue29Object implements Serializable {
		Marker marker
	}


	def "issue 29 third"() {
		setup:
		Marker fooMarker = MarkerFactory.getDetachedMarker("foo-marker")
		Marker barMarker = MarkerFactory.getDetachedMarker("bar-marker")
		fooMarker.add(barMarker)

		LoggingEventVO object = LoggingEventVO.build(new LoggingEvent(level: Level.DEBUG, marker: fooMarker))

		Set<String> whitelist = ['org.slf4j.helpers.BasicMarker', 'java.util.concurrent.CopyOnWriteArrayList', 'ch.qos.logback.classic.spi.LoggingEventVO', 'java.util.Collections$EmptyMap']
		def bytes = WhitelistObjectInputStreamSpec.serialize(object)


		when:
		ByteArrayInputStream is = new ByteArrayInputStream(bytes)
		WhitelistObjectInputStream instance = new WhitelistObjectInputStream(is, whitelist)
		Marker read = ((LoggingEventVO) instance.readObject()).marker

		then:
		read == fooMarker
		read.contains('bar-marker')
		read.hasReferences()
	}
}
