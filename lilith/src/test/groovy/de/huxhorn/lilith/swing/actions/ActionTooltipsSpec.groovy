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

package de.huxhorn.lilith.swing.actions

import spock.lang.Specification
import spock.lang.Unroll

import javax.swing.Action

class ActionTooltipsSpec extends Specification {
	@Unroll
	def 'initializeCroppedTooltip(#input, Action, #html) initializes tooltip of Action with expected value.'() {
		setup:
		Action action = Mock(Action)

		when:
		ActionTooltips.initializeCroppedTooltip(input, action, html)

		then:
		1 * action.putValue(Action.SHORT_DESCRIPTION, expectedTooltip)

		where:
		input | expectedTooltip                        | html
		null  | null                                   | true
		null  | null                                   | false
		''    | '<html><tt><pre></pre></tt></html>'    | true
		''    | ''                                     | false
		'foo' | '<html><tt><pre>foo</pre></tt></html>' | true
		'foo' | 'foo'                                  | false
	}
}
