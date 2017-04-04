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

package de.huxhorn.lilith.engine.impl

import de.huxhorn.lilith.data.eventsource.EventWrapper
import de.huxhorn.lilith.data.eventsource.SourceIdentifier
import de.huxhorn.sulky.buffers.Buffer
import de.huxhorn.sulky.conditions.Condition
import spock.lang.Specification


class EventSourceImplSpec extends Specification {
	def 'constructor with null SourceIdentifier fails as expected.'() {
		setup:
		Buffer<EventWrapper<Integer>> buffer = Mock(Buffer)
		Condition condition = Mock(Condition)

		when:
		new EventSourceImpl<Integer>(null, buffer, condition, false)

		then:
		NullPointerException ex = thrown()
		ex.message == 'sourceIdentifier must not be null!'
	}

	def 'constructor with null buffer fails as expected.'() {
		setup:
		SourceIdentifier sourceIdentifier = new SourceIdentifier('foo')
		Condition condition = Mock(Condition)

		when:
		new EventSourceImpl<Integer>(sourceIdentifier, null, condition, false)

		then:
		NullPointerException ex = thrown()
		ex.message == 'buffer must not be null!'
	}

	def 'constructor with null condition does not fail.'() {
		setup:
		SourceIdentifier sourceIdentifier = new SourceIdentifier('foo')
		Buffer<EventWrapper<Integer>> buffer = Mock(Buffer)

		when:
		new EventSourceImpl<Integer>(sourceIdentifier, buffer, false)
		and:
		new EventSourceImpl<Integer>(sourceIdentifier, buffer, null, false)

		then:
		noExceptionThrown()
	}
}
