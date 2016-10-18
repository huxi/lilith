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

import de.huxhorn.lilith.data.eventsource.EventWrapper
import de.huxhorn.lilith.swing.ViewContainer
import de.huxhorn.sulky.conditions.Condition
import de.huxhorn.sulky.conditions.Not
import spock.lang.Specification

import java.awt.event.ActionEvent
import java.beans.PropertyChangeListener

class NegateFilterActionSpec extends Specification {
	def 'default constructor works.'() {
		when:
		NegateFilterAction action = new NegateFilterAction()

		then:
		action.wrapped == null
	}

	def 'constructor with FilterAction works.'() {
		setup:
		FilterAction filterAction = Mock(FilterAction)

		when:
		NegateFilterAction action = new NegateFilterAction(filterAction)

		then:
		action.wrapped == filterAction
	}

	def 'setting FilterAction works.'() {
		setup:
		FilterAction filterAction = Mock(FilterAction)
		NegateFilterAction action = new NegateFilterAction()

		when:
		action.setWrapped(filterAction)

		then:
		action.wrapped == filterAction
	}

	def 'resolveCondition() without wrapped FilterAction returns null.'() {
		setup:
		NegateFilterAction action = new NegateFilterAction()

		expect:
		action.resolveCondition(null) == null
	}

	def 'resolveCondition() with wrapped FilterAction returning null returns null.'() {
		setup:
		FilterAction filterAction = Mock(FilterAction)
		NegateFilterAction action = new NegateFilterAction(filterAction)

		when:
		def condition = action.resolveCondition(null)

		then:
		condition == null

		and:
		1 * filterAction.resolveCondition(null) >> null
		0 * filterAction._
	}

	def 'resolveCondition() with wrapped FilterAction returning Condition returns Not(Condition).'() {
		setup:
		FilterAction filterAction = Mock(FilterAction)
		Condition mockedCondition = Mock(Condition)
		NegateFilterAction action = new NegateFilterAction(filterAction)

		when:
		def condition = action.resolveCondition(null)

		then:
		condition != null
		condition instanceof Not
		condition.condition == mockedCondition

		and:
		1 * filterAction.resolveCondition(null) >> mockedCondition
		0 * filterAction._
	}

	def 'getValue propagates as expected.'() {
		setup:
		FilterAction filterAction = Mock(FilterAction)
		NegateFilterAction action = new NegateFilterAction()

		when:
		def value1 = action.getValue('foo')

		and:
		action.wrapped = filterAction
		def value2 = action.getValue('foo')

		and:
		action.wrapped = null
		def value3 = action.getValue('foo')

		then:
		1 * filterAction.getValue('foo') >> 'bar'
		0 * filterAction._

		and:
		value1 == null
		value2 == 'bar'
		value3 == null
	}

	def 'putValue propagates as expected.'() {
		setup:
		FilterAction filterAction = Mock(FilterAction)
		NegateFilterAction action = new NegateFilterAction()

		when:
		action.putValue('foo', 'bar')

		and:
		action.wrapped = filterAction
		action.putValue('foo', 'bar')

		and:
		action.wrapped = null
		action.putValue('foo', 'bar')

		then:
		1 * filterAction.putValue('foo', 'bar')
		0 * filterAction._
	}

	def 'isEnabled propagates as expected.'() {
		setup:
		FilterAction filterAction = Mock(FilterAction)
		NegateFilterAction action = new NegateFilterAction()

		when:
		def value1 = action.isEnabled()

		and:
		action.wrapped = filterAction
		def value2 = action.isEnabled()
		def value3 = action.isEnabled()

		and:
		action.wrapped = null
		def value4 = action.isEnabled()

		then:
		1 * filterAction.isEnabled() >> true
		1 * filterAction.isEnabled() >> false
		0 * filterAction._

		and:
		!value1
		value2
		!value3
		!value4
	}

	def 'setEnabled propagates as expected.'() {
		setup:
		FilterAction filterAction = Mock(FilterAction)
		NegateFilterAction action = new NegateFilterAction()

		when:
		action.setEnabled(true)

		and:
		action.wrapped = filterAction
		action.setEnabled(true)

		and:
		action.wrapped = null
		action.setEnabled(true)

		then:
		1 * filterAction.setEnabled(true)
		0 * filterAction._
	}

	def 'addPropertyChangeListener propagates as expected.'() {
		setup:
		PropertyChangeListener mockedPropertyChangeListener = Mock(PropertyChangeListener)
		FilterAction filterAction = Mock(FilterAction)
		NegateFilterAction action = new NegateFilterAction()

		when:
		action.addPropertyChangeListener(mockedPropertyChangeListener)

		and:
		action.wrapped = filterAction
		action.addPropertyChangeListener(mockedPropertyChangeListener)

		and:
		action.wrapped = null
		action.addPropertyChangeListener(mockedPropertyChangeListener)

		then:
		1 * filterAction.addPropertyChangeListener(mockedPropertyChangeListener)
		0 * filterAction._
	}

	def 'removePropertyChangeListener propagates as expected.'() {
		setup:
		PropertyChangeListener mockedPropertyChangeListener = Mock(PropertyChangeListener)
		FilterAction filterAction = Mock(FilterAction)
		NegateFilterAction action = new NegateFilterAction()

		when:
		action.removePropertyChangeListener(mockedPropertyChangeListener)

		and:
		action.wrapped = filterAction
		action.removePropertyChangeListener(mockedPropertyChangeListener)

		and:
		action.wrapped = null
		action.removePropertyChangeListener(mockedPropertyChangeListener)

		then:
		1 * filterAction.removePropertyChangeListener(mockedPropertyChangeListener)
		0 * filterAction._
	}

	def 'setEventWrapper propagates as expected in case of FilterAction.'() {
		setup:
		EventWrapper mockedEventWrapper = Mock(EventWrapper)
		FilterAction filterAction = Mock(FilterAction)
		NegateFilterAction action = new NegateFilterAction()

		when:
		action.setEventWrapper(mockedEventWrapper)

		and:
		action.wrapped = filterAction
		action.setEventWrapper(mockedEventWrapper)

		and:
		action.wrapped = null
		action.setEventWrapper(mockedEventWrapper)

		then:
		1 * filterAction.setEventWrapper(mockedEventWrapper)
		0 * filterAction._
	}

	def 'setEventWrapper propagates as expected in case of BasicFilterAction.'() {
		setup:
		EventWrapper mockedEventWrapper = Mock(EventWrapper)
		BasicFilterAction filterAction = Mock(BasicFilterAction)
		NegateFilterAction action = new NegateFilterAction()

		when:
		action.setEventWrapper(mockedEventWrapper)

		and:
		action.wrapped = filterAction
		action.setEventWrapper(mockedEventWrapper)

		and:
		action.wrapped = null
		action.setEventWrapper(mockedEventWrapper)

		then:
		0 * filterAction._

		and:
		noExceptionThrown()
	}

	def 'setViewContainer propagates as expected.'() {
		setup:
		ViewContainer mockedViewContainer = Mock(ViewContainer)
		FilterAction filterAction = Mock(FilterAction)
		NegateFilterAction action = new NegateFilterAction()

		when:
		action.setViewContainer(mockedViewContainer)

		and:
		action.wrapped = filterAction
		action.setViewContainer(mockedViewContainer)

		and:
		action.wrapped = null
		action.setViewContainer(mockedViewContainer)

		then:
		1 * filterAction.setViewContainer(mockedViewContainer)
		0 * filterAction._
	}

	def 'getViewContainer propagates as expected.'() {
		setup:
		FilterAction filterAction = Mock(FilterAction)
		NegateFilterAction action = new NegateFilterAction()

		when:
		action.getViewContainer()

		and:
		action.wrapped = filterAction
		action.getViewContainer()

		and:
		action.wrapped = null
		action.getViewContainer()

		then:
		1 * filterAction.getViewContainer()
		0 * filterAction._
	}

	def 'actionPerformed(ActionEvent) propagates as expected - everything fine.'() {
		setup:
		FilterAction filterActionMock = Mock(FilterAction)
		ViewContainer viewContainerMock = Mock(ViewContainer)
		Condition conditionMock = Mock(Condition)
		NegateFilterAction action = new NegateFilterAction(filterActionMock)
		ActionEvent actionEvent = new ActionEvent("Foo", 0, null)


		when:
		action.actionPerformed(actionEvent)

		then:
		1 * filterActionMock.getViewContainer() >> viewContainerMock
		1 * filterActionMock.resolveCondition(actionEvent) >> conditionMock
		1 * viewContainerMock.applyCondition(new Not(conditionMock), actionEvent)
		0 * filterActionMock._
		0 * viewContainerMock._
		0 * conditionMock._
	}

	def 'actionPerformed(ActionEvent) propagates as expected - missing wrapped or viewContainer.'() {
		setup:
		FilterAction filterActionMock = Mock(FilterAction)
		NegateFilterAction action = new NegateFilterAction()
		ActionEvent actionEvent = new ActionEvent("Foo", 0, null)


		when:
		action.actionPerformed(actionEvent)

		and:
		action.wrapped = filterActionMock
		action.actionPerformed(actionEvent)

		and:
		action.wrapped = null
		action.actionPerformed(actionEvent)

		then:
		1 * filterActionMock.getViewContainer() >> null
		0 * filterActionMock._
	}

	def 'actionPerformed(ActionEvent) propagates as expected - missing condition.'() {
		setup:
		FilterAction filterActionMock = Mock(FilterAction)
		ViewContainer viewContainerMock = Mock(ViewContainer)
		NegateFilterAction action = new NegateFilterAction(filterActionMock)
		ActionEvent actionEvent = new ActionEvent("Foo", 0, null)


		when:
		action.actionPerformed(actionEvent)

		then:
		1 * filterActionMock.getViewContainer() >> viewContainerMock
		1 * filterActionMock.resolveCondition(actionEvent) >> null
		0 * filterActionMock._
		0 * viewContainerMock._
	}
}
