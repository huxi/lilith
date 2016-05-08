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
package de.huxhorn.lilith.conditions

import spock.lang.Specification
import spock.lang.Unroll

import static de.huxhorn.sulky.junit.JUnitTools.testClone
import static de.huxhorn.sulky.junit.JUnitTools.testSerialization
import static de.huxhorn.sulky.junit.JUnitTools.testXmlSerialization

class CallLocationConditionSpec extends Specification {
	@Unroll
	def "Corpus works as expected for #condition (searchString=#input)."() {
		expect:
		Corpus.executeConditionOnCorpus(condition) == expectedResult

		where:
		input                                                                                                                               | expectedResult
		null                                                                                                                                | [] as Set
		''                                                                                                                                  | Corpus.matchAllSet()
		'snafu'                                                                                                                             | [] as Set
		'actionPerformed(DebugDialog.java:358)'                                                                                             | [] as Set
		'de.huxhorn.lilith.debug.DebugDialog$LogAllAction.actionPerformed(DebugDialog.java:358) [de.huxhorn.lilith-8.1.0-SNAPSHOT.jar:na]'  | [44, 45, 46, 47] as Set
		'de.huxhorn.lilith.debug.DebugDialog$LogAllAction.actionPerformed(DebugDialog.java:358) ~[de.huxhorn.lilith-8.1.0-SNAPSHOT.jar:na]' | [44, 45, 46, 47] as Set
		'de.huxhorn.lilith.debug.DebugDialog$LogAllAction.actionPerformed(DebugDialog.java:358)'                                            | [44, 45, 46, 47] as Set
		'javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022) [na:1.8.0_92]'                                            | [48, 49, 50, 51] as Set
		'javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022) ~[na:1.8.0_92]'                                           | [48, 49, 50, 51] as Set
		'javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022)'                                                          | [48, 49, 50, 51] as Set
		'javax.swing.plaf.basic.BasicButtonListener.mouseReleased(BasicButtonListener.java:252) [na:1.8.0_92]'                              | [] as Set
		'javax.swing.plaf.basic.BasicButtonListener.mouseReleased(BasicButtonListener.java:252) ~[na:1.8.0_92]'                             | [] as Set
		'javax.swing.plaf.basic.BasicButtonListener.mouseReleased(BasicButtonListener.java:252)'                                            | [] as Set
		'java.awt.AWTEventMulticaster.mouseReleased(AWTEventMulticaster.java:289) [na:1.8.0_92]'                                            | [] as Set
		'java.awt.AWTEventMulticaster.mouseReleased(AWTEventMulticaster.java:289) ~[na:1.8.0_92]'                                           | [] as Set
		'java.awt.AWTEventMulticaster.mouseReleased(AWTEventMulticaster.java:289)'                                                          | [] as Set

		condition = new CallLocationCondition(input)
	}

	def "serialization works."() {
		when:
		def condition = new CallLocationCondition()
		condition.searchString = input

		and:
		def result = testSerialization(condition)

		then:
		result.searchString == input

		where:
		input << [null, '', 'value', 'javax.swing.plaf.basic.BasicButtonListener.mouseReleased(BasicButtonListener.java:252) ~[na:1.8.0_92]', 'javax.swing.plaf.basic.BasicButtonListener.mouseReleased(BasicButtonListener.java:252)']
	}

	def "XML serialization works."() {
		when:
		def condition = new CallLocationCondition()
		condition.searchString = input

		and:
		def result = testXmlSerialization(condition)

		then:
		result.searchString == input

		where:
		input << [null, '', 'value', 'javax.swing.plaf.basic.BasicButtonListener.mouseReleased(BasicButtonListener.java:252) ~[na:1.8.0_92]', 'javax.swing.plaf.basic.BasicButtonListener.mouseReleased(BasicButtonListener.java:252)']
	}

	def "cloning works."() {
		when:
		def condition = new CallLocationCondition()
		condition.searchString = input

		and:
		def result = testClone(condition)

		then:
		result.searchString == input

		where:
		input << [null, '', 'value', 'javax.swing.plaf.basic.BasicButtonListener.mouseReleased(BasicButtonListener.java:252) ~[na:1.8.0_92]', 'javax.swing.plaf.basic.BasicButtonListener.mouseReleased(BasicButtonListener.java:252)']
	}
}
