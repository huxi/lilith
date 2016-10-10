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

import de.huxhorn.lilith.conditions.HttpStatusTypeCondition
import de.huxhorn.lilith.data.access.HttpStatus

class FocusHttpStatusTypeActionSpec extends AbstractFilterActionSpec {
	@Override
	FilterAction createAction() {
		return new FocusHttpStatusTypeAction(HttpStatus.Type.CLIENT_ERROR)
	}

	@Override
	Set<Integer> expectedEnabledIndices() {
		// enabled if event wrapper contains any access event
		return [5, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 70, 71, 72, 73, 74, 75, 77, 79, 81, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 118, 119, 120]
	}

	@Override
	List<String> expectedSearchStrings() {
		List<String> result = new ArrayList<>()
		expectedEnabledIndices().each {
			// returns always the status type used during construction
			result.add('CLIENT_ERROR')
		}
		return result
	}

	@Override
	Class expectedConditionClass() {
		return HttpStatusTypeCondition.class
	}
}
