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

	@Unroll
	"sanity-check keystroke for #actionName"() {
		when:
		def keyStroke = LilithKeyStrokes.getKeyStroke(actionName)

		then:
		keyStroke != null
		println keyStroke
		actionName == LilithKeyStrokes.getActionName(keyStroke)

		where:
		actionName << new TreeSet<String>(LilithKeyStrokes.getActionNames())
	}

	@Unroll
	"sanity-check keystroke string for #actionName"() {
		when:
		def keyStrokeString = LilithKeyStrokes.getKeyStrokeString(actionName)

		then:
		keyStrokeString != null
		println keyStrokeString
		KeyStrokes.resolveAcceleratorKeyStroke(keyStrokeString) != null

		where:
		actionName << new TreeSet<String>(LilithKeyStrokes.getActionNames())
	}
}
