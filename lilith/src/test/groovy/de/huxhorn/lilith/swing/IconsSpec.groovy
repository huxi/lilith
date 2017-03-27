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

import spock.lang.Specification
import spock.lang.Unroll

class IconsSpec extends Specification {

	def setupSpec() {
		System.setProperty("java.awt.headless", "true")
	}

	@Unroll
	'toolbar action #actionId has a toolbar icon.'() {
		expect:
		Icons.resolveToolbarIcon(actionId) != null
		where:
		actionId << [
				LilithActionId.TAIL,
				// TODO: LilithActionId.PAUSE,
				LilithActionId.CLEAR,
				LilithActionId.DISCONNECT,

				LilithActionId.FIND,
				LilithActionId.FIND_PREVIOUS,
				LilithActionId.FIND_NEXT,

				// TODO: LilithActionId.ATTACH,

				LilithActionId.PREFERENCES,

				LilithActionId.LOVE,
		]
	}
}
