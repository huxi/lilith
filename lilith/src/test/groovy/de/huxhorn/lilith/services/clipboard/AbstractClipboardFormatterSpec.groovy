/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2016 Joern Huxhorn
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

import spock.lang.Unroll

abstract class AbstractClipboardFormatterSpec extends AbstractBasicFormatterSpec {
	abstract ClipboardFormatter createInstance()

	def "Formatter has a name."() {
		when:
		def instance = createInstance()

		then:
		instance.name != null
	}

	def "Formatter has a description."() {
		when:
		def instance = createInstance()

		then:
		instance.description != null
	}

	@Unroll
	def "Formatter is#not native."() {
		when:
		def instance = createInstance()

		then:
		instance.native == expectedNative

		where:
		expectedNative = expectedNative()
		not = expectedNative? '' : ' not'
	}

	@Unroll
	def "Formatter #has an accelerator."() {
		when:
		def instance = createInstance()

		then:
		if(expectedAcceleratorAvailability) {
			assert instance.accelerator != null
		} else {
			assert instance.accelerator == null
		}

		where:
		expectedAcceleratorAvailability = expectedAcceleratorAvailability()
		has = expectedAcceleratorAvailability? 'has' : 'does not have'
	}

	boolean expectedNative() {
		true
	}

	boolean expectedAcceleratorAvailability() {
		false
	}
}
