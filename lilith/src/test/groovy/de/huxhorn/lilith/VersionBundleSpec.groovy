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

package de.huxhorn.lilith

import spock.lang.Specification
import spock.lang.Unroll

class VersionBundleSpec extends Specification {
	@Unroll
	def 'getter work as expected for #versionBundle'() {
		expect:
		versionBundle.version == version
		versionBundle.timestamp == timestamp


		where:
		versionBundle                 | version | timestamp
		new VersionBundle('foo')      | 'foo'   | -1
		new VersionBundle('foo', -1)  | 'foo'   | -1
		new VersionBundle('foo', -17) | 'foo'   | -1
		new VersionBundle('bar', 17)  | 'bar'   | 17
	}

	@Unroll
	def 'toString works as expected for #versionBundle'() {
		expect:
		versionBundle.toString() == expectedResult


		where:
		versionBundle                 | expectedResult
		new VersionBundle('foo')      | 'foo#-1'
		new VersionBundle('foo', -1)  | 'foo#-1'
		new VersionBundle('foo', -17) | 'foo#-1'
		new VersionBundle('bar', 17)  | 'bar#17'
	}

	@Unroll
	def 'fromString(#input) returns expected result #expectedResult'() {
		expect:
		VersionBundle.fromString(input) == expectedResult

		where:
		input      | expectedResult
		'foo'      | new VersionBundle('foo')
		' foo '    | new VersionBundle('foo')
		' foo#17'  | new VersionBundle('foo', 17)
		' foo#17 ' | new VersionBundle('foo', 17)
		'foo#-1'   | new VersionBundle('foo', -1)
		'foo#-17'  | new VersionBundle('foo', -1)
		'foo#bar'  | new VersionBundle('foo', -1)
		null       | null
	}

	@SuppressWarnings("ChangeToOperator")
	def 'compareTo is only working on timestamp.'() {
		given:
		def instance1 = new VersionBundle('foo', -1)
		def instance2 = new VersionBundle('foo', 10)
		def instance3 = new VersionBundle('foo', 17)
		def instance4 = new VersionBundle('bar', 17)

		expect:
		instance1.compareTo(instance1) == 0
		instance1.compareTo(instance2) < 0
		instance1.compareTo(instance3) < 0
		instance1.compareTo(instance4) < 0

		instance2.compareTo(instance1) > 0
		instance2.compareTo(instance2) == 0
		instance2.compareTo(instance3) < 0
		instance2.compareTo(instance4) < 0

		instance3.compareTo(instance1) > 0
		instance3.compareTo(instance2) > 0
		instance3.compareTo(instance3) == 0
		instance3.compareTo(instance4) == 0

		instance4.compareTo(instance1) > 0
		instance4.compareTo(instance2) > 0
		instance4.compareTo(instance3) == 0
		instance4.compareTo(instance4) == 0

		and:
		instance1.compareTo(null) > 0
	}

	def 'new VersionBundle(null) explodes as expected'() {
		when:
		new VersionBundle(null)

		then:
		NullPointerException ex = thrown()
		ex.message == 'version must not be null!'
	}

	def 'new VersionBundle(null, 17) explodes as expected'() {
		when:
		new VersionBundle(null, 17)

		then:
		NullPointerException ex = thrown()
		ex.message == 'version must not be null!'
	}

	@SuppressWarnings("ChangeToOperator")
	def 'equals() and hashCode checks'() {
		given:
		def instance1 = new VersionBundle('foo', 10)
		def instance2 = new VersionBundle('foo', 10)
		def instance3 = new VersionBundle('foo', 17)
		def instance4 = new VersionBundle('bar', 17)
		def instance5 = new VersionBundle('bar', 10)

		expect:
		!instance1.equals(null)
		//noinspection GrEqualsBetweenInconvertibleTypes
		!instance1.equals(1)
		instance1.equals(instance1)
		instance1.equals(instance2)
		!instance1.equals(instance3)
		!instance1.equals(instance4)
		!instance1.equals(instance5)

		and:
		instance1.hashCode() == instance1.hashCode()
		instance1.hashCode() == instance2.hashCode()
	}
}
