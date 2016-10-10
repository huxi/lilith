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

import de.huxhorn.lilith.conditions.ThrowableCondition

class FocusThrowablesActionSpec extends AbstractFilterActionSpec {
	@Override
	FilterAction createAction() {
		return new FocusThrowablesAction()
	}

	@Override
	Set<Integer> expectedEnabledIndices() {
		// enabled if event wrapper contains any logging event
		return [6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 65, 66, 67, 68, 69, 76, 78, 80, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 114, 115, 116, 117]
	}

	@Override
	List<String> expectedSearchStrings() {
		List<String> result = new ArrayList<>()
		expectedEnabledIndices().each {
			// returns null because that means "any throwable" in ThrowableCondition
			result.add(null)
		}
		return result
	}

	@Override
	Class expectedConditionClass() {
		return ThrowableCondition.class
	}
}
