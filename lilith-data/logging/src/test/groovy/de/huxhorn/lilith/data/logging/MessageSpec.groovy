/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2016 Joern Huxhorn
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
 * Copyright 2007-2016 Joern Huxhorn
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

package de.huxhorn.lilith.data.logging

import spock.lang.Specification
import spock.lang.Unroll

import static de.huxhorn.sulky.junit.JUnitTools.testClone
import static de.huxhorn.sulky.junit.JUnitTools.testSerialization
import static de.huxhorn.sulky.junit.JUnitTools.testXmlSerialization

class MessageSpec extends Specification {

	private static final Message INSTANCE = new Message()

	def 'default constructor works as expected.'() {
		when:
		Message instance = new Message()

		then:
		instance.messagePattern == null
		instance.arguments == null
		instance.message == null
	}

	def 'single-parameter constructor works as expected.'() {
		when:
		Message instance = new Message('message')

		then:
		instance.messagePattern == 'message'
		instance.arguments == null
		instance.message == 'message'
	}

	def 'two-parameter constructor works as expected.'() {
		when:
		Message instance = new Message('message {} {}', ['one', 'two'] as String[])

		then:
		instance.messagePattern == 'message {} {}'
		instance.arguments == ['one', 'two'] as String[]
		instance.message == 'message one two'
		// walking lazy init path 2
		instance.message == 'message one two'
	}

	@Unroll
	def 'Serialization works for #message.'() {
		when:
		def other = testSerialization(message)

		then:
		other == message
		other.hashCode() == message.hashCode()
		!(message.is(other))
		other.messagePattern == message.messagePattern
		other.message == message.message
		other.arguments == message.arguments
		if(other.arguments) {
			assert !(other.arguments.is(message.arguments))
		}

		where:
		message << someInstances()
	}

	@Unroll
	def 'XML-Serialization works for #message.'() {
		when:
		def other = testXmlSerialization(message)

		then:
		other == message
		other.hashCode() == message.hashCode()
		!(message.is(other))
		other.messagePattern == message.messagePattern
		other.message == message.message
		other.arguments == message.arguments
		if(other.arguments) {
			assert !(other.arguments.is(message.arguments))
		}

		where:
		message << someInstances()
	}

	@Unroll
	def 'Cloning works for #message.'() {
		when:
		def other = testClone(message)

		then:
		other == message
		other.hashCode() == message.hashCode()
		!(message.is(other))
		other.messagePattern == message.messagePattern
		other.message == message.message
		other.arguments == message.arguments
		if(other.arguments) {
			assert !(other.arguments.is(message.arguments))
		}

		where:
		message << someInstances()
	}

	@Unroll
	def 'equals and hashCode works as expected.'() {
		expect:
		instance.equals(other) == expectedEqual
		if(expectedEqual) {
			assert instance.hashCode() == other.hashCode()
		}

		where:
		instance                           | other                              | expectedEqual
		INSTANCE                           | INSTANCE                           | true
		INSTANCE                           | 'foo'                              | false
		INSTANCE                           | null                               | false
		new Message()                      | new Message()                      | true
		new Message()                      | new Message('message')             | false
		new Message('message')             | new Message('message')             | true
		new Message('message')             | new Message()                      | false
		new Message('message')             | new Message('message2')            | false
		new Message('', ['a'] as String[]) | new Message('', ['a'] as String[]) | true
		new Message('', ['a'] as String[]) | new Message('', ['b'] as String[]) | false
	}

	Message[] someInstances() {
		[
		        new Message(),
				new Message('message'),
				new Message('messagePattern', ['one', 'two'] as String[]),
				new Message('messagePattern {} {}', ['one', 'two'] as String[]),
				new Message('messagePattern', ['one', null, 'three'] as String[]),
		]
	}
}
