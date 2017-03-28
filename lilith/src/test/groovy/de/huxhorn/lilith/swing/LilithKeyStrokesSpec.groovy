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

package de.huxhorn.lilith.swing

import de.huxhorn.sulky.swing.KeyStrokes
import spock.lang.Specification
import spock.lang.Unroll

class LilithKeyStrokesSpec extends Specification {
	def setupSpec() {
		System.setProperty("java.awt.headless", "true")
	}

	@Unroll
	"sanity-check keystroke for #actionName"() {
		when:
		def keyStroke = LilithKeyStrokes.getKeyStroke(actionName)

		then:
		keyStroke != null
		//println actionName + " => " + keyStroke
		actionName == LilithKeyStrokes.getActionName(keyStroke)

		where:
		actionName << new TreeSet<String>(LilithKeyStrokes.getActionNames())
	}

	@Unroll
	"sanity-check keystroke string for #actionName"() {
		when:
		def unprocessedKeyStrokeString = LilithKeyStrokes.getUnprocessedKeyStrokeString(actionName)

		then:
		unprocessedKeyStrokeString != null
		//println actionName + " => " + unprocessedKeyStrokeString
		KeyStrokes.resolveAcceleratorKeyStroke(unprocessedKeyStrokeString) != null

		where:
		actionName << new TreeSet<String>(LilithKeyStrokes.getActionNames())
	}

	def 'addKeyStroke("", ...) throws expected exception.'() {
		when:
		LilithKeyStrokes.addKeyStroke('', 'foo')

		then:
		IllegalArgumentException ex = thrown()
		ex.message == 'keyStrokeString \'\' did not resolve to a KeyStroke!'
	}

	def 'addKeyStroke(null, ...) throws expected exception.'() {
		when:
		LilithKeyStrokes.addKeyStroke(null, 'foo')

		then:
		NullPointerException ex = thrown()
		ex.message == 'keyStrokeString must not be null!'
	}

	def 'addKeyStroke(..., null) throws expected exception.'() {
		when:
		LilithKeyStrokes.addKeyStroke('foo', null)

		then:
		NullPointerException ex = thrown()
		ex.message == 'actionName must not be null!'
	}

	def 'addKeyStroke() with duplicate key stroke throws expected exception.'() {
		when:
		LilithKeyStrokes.addKeyStroke('command F', 'foo')

		then:
		IllegalStateException ex = thrown()
		ex.message == 'Duplicate action name entry for \'command F\': \'FIND\' and \'foo\''
	}

	def 'addKeyStroke() with duplicate action name throws expected exception.'() {
		when:
		LilithKeyStrokes.addKeyStroke('command F2', 'FIND')

		then:
		IllegalStateException ex = thrown()
		ex.message == 'Duplicate action name entry \'FIND\'!'
	}
}
