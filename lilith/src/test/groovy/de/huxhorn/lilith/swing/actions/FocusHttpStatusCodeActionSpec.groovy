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

import de.huxhorn.lilith.conditions.HttpStatusCodeCondition

class FocusHttpStatusCodeActionSpec extends AbstractFilterActionSpecBase {
	@Override
	FilterAction createAction() {
		return new FocusHttpStatusCodeAction()
	}

	@Override
	Set<Integer> expectedEnabledIndices() {
		return [52, 53, 54, 55, 56, 57, 58, 120]
	}

	@Override
	List<String> expectedSearchStrings() {
		return ['100', '200', '202', '301', '404', '451', '500', '488']
	}

	@Override
	Class expectedConditionClass() {
		return HttpStatusCodeCondition.class
	}
}
