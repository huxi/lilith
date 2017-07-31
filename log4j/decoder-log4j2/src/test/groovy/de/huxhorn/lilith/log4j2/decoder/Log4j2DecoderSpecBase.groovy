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

package de.huxhorn.lilith.log4j2.decoder

import de.huxhorn.lilith.data.logging.LoggingEvent
import de.huxhorn.sulky.codec.Decoder
import spock.lang.Specification

import java.nio.charset.StandardCharsets

abstract class Log4j2DecoderSpecBase extends Specification {

	abstract String getInputString()
	abstract Decoder<LoggingEvent> getDecoder()

	def 'decoder is producing an event for valid input'() {
		given:
		def inputString = getInputString()
		def inputBytes = inputString.getBytes(StandardCharsets.UTF_8)
		def decoder = getDecoder()

		when:
		def result = decoder.decode(inputBytes)

		then:
		result != null
	}

	def 'decoder is returning null for null input'() {
		given:
		def decoder = getDecoder()

		when:
		def result = decoder.decode(null)

		then:
		result == null
	}

	def 'decoder is returning null for invalid input'() {
		given:
		def inputString = '#invalid'
		def inputBytes = inputString.getBytes(StandardCharsets.UTF_8)
		def decoder = getDecoder()

		when:
		def result = decoder.decode(inputBytes)

		then:
		result == null
	}
}
