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

import de.huxhorn.lilith.conditions.ThreadNameCondition
import de.huxhorn.lilith.swing.preferences.SavedCondition

class FocusSavedConditionActionSpec extends AbstractBasicFilterActionSpecBase {
	@Override
	BasicFilterAction createAction() {
		SavedCondition savedCondition = new SavedCondition(new ThreadNameCondition())
		savedCondition.setName('savedCondition')
		return new FocusSavedConditionAction(savedCondition, false)
	}

	@Override
	Class expectedConditionClass() {
		return ThreadNameCondition.class
	}

	def 'Broken SavedCondition causes expected exception'() {
		when:
		SavedCondition savedCondition = new SavedCondition()
		savedCondition.setName('savedCondition')
		//noinspection GroovyResultOfObjectAllocationIgnored
		new FocusSavedConditionAction(savedCondition, false)

		then:
		IllegalArgumentException ex = thrown()
		ex.message == 'Condition of '+savedCondition+' is null!'
	}
}
