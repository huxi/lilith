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

import de.huxhorn.lilith.conditions.FormattedMessageEqualsCondition

class FocusFormattedMessageActionSpec extends AbstractFilterActionSpecBase {
	@Override
	FilterAction createAction() {
		return new FocusFormattedMessageAction(false)
	}

	@Override
	Set<Integer> expectedEnabledIndices() {
		return [17, 18, 19, 20, 21, 22, 23]
	}

	@Override
	List<String> expectedSearchStrings() {
		return [
				'a message.',
				'another message.',
				'a message with parameter paramValue.',
				'a message with unresolved parameter {}.',
				'a message with parameter paramValue and unresolved parameter {}.',
				'paramValue',
				'{}',
		]
	}

	@Override
	Class expectedConditionClass() {
		return FormattedMessageEqualsCondition.class
	}
}
