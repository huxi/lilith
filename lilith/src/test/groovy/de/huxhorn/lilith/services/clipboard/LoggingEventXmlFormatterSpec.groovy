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

package de.huxhorn.lilith.services.clipboard

import de.huxhorn.lilith.data.EventWrapperCorpus

class LoggingEventXmlFormatterSpec extends AbstractClipboardFormatterSpec {

	@Override
	LoggingEventXmlFormatter createInstance() {
		return new LoggingEventXmlFormatter()
	}

	Set<Integer> expectedIndices() {
		EventWrapperCorpus.matchAnyLoggingEventSet()
	}

	Set<Integer> excludedIndices() {
		// schema (and therefore XML writer) require logger name
		// exclude all events without one
		def result = new HashSet(EventWrapperCorpus.matchAnyLoggingEventSet())
		result.removeAll([13, 14, 114])
		result
	}

	List<String> expectedResults() {
		[
				'<?xml version=\'1.0\' encoding=\'UTF-8\'?><LoggingEvent xmlns="http://lilith.sf.net/schema/logging/16" logger="com.foo.Foo" level="null"/>',

				'<?xml version=\'1.0\' encoding=\'UTF-8\'?><LoggingEvent xmlns="http://lilith.sf.net/schema/logging/16" logger="com.foo.Bar" level="null"/>',

				'<?xml version=\'1.0\' encoding=\'UTF-8\'?><LoggingEvent xmlns="http://lilith.sf.net/schema/logging/16" logger="" level="null"/>',
		]
	}

	def 'exploding formatter does simply return null.'() {
		setup:
		def corpus = EventWrapperCorpus.createCorpus()
		def formatter = createInstance()
		def exploding = corpus[6]

		expect:
		formatter.toString(exploding) == null
	}
}
