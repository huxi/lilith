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

import spock.lang.Specification
import spock.lang.Unroll

class ClipboardFormatterDataSpec extends Specification {

	@Unroll
	def "equals and hashCode work as expected for #oneInstance and #otherInstance."() {

		expect:
		if (expectedEqual) {
			assert oneInstance == otherInstance
			assert otherInstance == oneInstance
			assert oneInstance.hashCode() == otherInstance.hashCode()
		} else {
			assert oneInstance != otherInstance
			assert otherInstance != oneInstance
		}

		oneInstance.equals(oneInstance)
		!oneInstance.equals(null)
		//noinspection GrEqualsBetweenInconvertibleTypes
		!oneInstance.equals(new Integer(17))

		where:
		oneInstance                                                              | otherInstance                                                            | expectedEqual
		new ClipboardFormatterData(new FooFormatter())                           | new ClipboardFormatterData(new FooFormatter())                           | true

		new ClipboardFormatterData(new FooFormatter(name: 'name'))               | new ClipboardFormatterData(new FooFormatter(name: 'name'))               | true
		new ClipboardFormatterData(new FooFormatter(name: 'name'))               | new ClipboardFormatterData(new FooFormatter())                           | false

		new ClipboardFormatterData(new FooFormatter(description: 'description')) | new ClipboardFormatterData(new FooFormatter(description: 'description')) | true
		new ClipboardFormatterData(new FooFormatter(description: 'description')) | new ClipboardFormatterData(new FooFormatter())                           | false

		new ClipboardFormatterData(new FooFormatter(accelerator: 'accelerator')) | new ClipboardFormatterData(new FooFormatter(accelerator: 'accelerator')) | true
		new ClipboardFormatterData(new FooFormatter(accelerator: 'accelerator')) | new ClipboardFormatterData(new FooFormatter())                           | false
	}

	def "constructor throws expected exception."() {
		when:
		new ClipboardFormatterData(null)

		then:
		NullPointerException ex = thrown()
		ex.message == 'clipboardFormatter must not be null!'
	}

	def "getters"() {
		when:
		ClipboardFormatterData instance = new ClipboardFormatterData(new FooFormatter(name: 'fooName', description: 'fooDescription', accelerator: 'fooAccelerator'))

		then:
		instance.name == 'fooName'
		instance.description == 'fooDescription'
		instance.accelerator == 'fooAccelerator'
	}

	static class FooFormatter implements ClipboardFormatter {

		String name
		String description
		String accelerator

		@Override
		boolean isCompatible(Object object) {
			return false
		}

		@Override
		String toString(Object object) {
			return null
		}
	}
}
