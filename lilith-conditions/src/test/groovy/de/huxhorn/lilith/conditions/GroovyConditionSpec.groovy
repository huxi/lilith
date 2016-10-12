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

import de.huxhorn.sulky.junit.JUnitTools
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

import static de.huxhorn.sulky.junit.JUnitTools.testClone
import static de.huxhorn.sulky.junit.JUnitTools.testSerialization
import static de.huxhorn.sulky.junit.JUnitTools.testXmlSerialization

class GroovyConditionSpec extends Specification {
	@Rule
	public TemporaryFolder folder = new TemporaryFolder()

	public File rootDirectory

	def setup() {
		rootDirectory = folder.newFolder()
	}

	@Unroll
	def "Corpus works as expected for GroovyCondition with script #script and searchString #searchString."() {
		setup:
		File groovyFormatterFile = new File(rootDirectory, script+'.groovy');
		JUnitTools.copyResourceToFile('/'+script+'.txt', groovyFormatterFile)

		def instance = new GroovyCondition(groovyFormatterFile.absolutePath, searchString)

		when:
		def result = new TreeSet(ConditionCorpus.executeConditionOnCorpus(instance))

		then:
		result == new TreeSet(expectedResult)

		where:
		script                          | searchString  | expectedResult
		'WorkingCondition'              | null          | [67, 97] as Set
		'WorkingScript'                 | null          | [67, 97] as Set
		'WorkingScriptReturnOther'      | null          | [67, 97] as Set
		'WorkingScriptThrowing'         | null          | [67, 97] as Set
		'WorkingScriptWithSearchString' | null          | [6, 7, 8, 9, 10, 11, 12, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 65, 66, 67, 68, 69, 76, 78, 80, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 115, 116, 117, 121] as Set
		'WorkingScriptWithSearchString' | 'com.foo.Foo' | [13] as Set
		'SyntaxError'                   | null          | [] as Set
		'WrongType'                     | null          | [] as Set
	}

	@Unroll
	def "serialization works with script #script and searchString #searchString."() {
		when:
		def condition = new GroovyCondition()
		condition.scriptFileName = script
		condition.searchString = searchString

		and:
		def result = testSerialization(condition)

		then:
		result.scriptFileName == script
		result.searchString == searchString
		result.scriptFileName == condition.scriptFileName
		result.searchString == condition.searchString

		where:
		script << scriptValues()
		searchString << searchStringValues()
	}

	@Unroll
	def "XML serialization works with script #script and searchString #searchString."() {
		when:
		def condition = new GroovyCondition()
		condition.scriptFileName = script
		condition.searchString = searchString

		and:
		def result = testXmlSerialization(condition)

		then:
		result.scriptFileName == script
		result.searchString == searchString
		result.scriptFileName == condition.scriptFileName
		result.searchString == condition.searchString

		where:
		script << scriptValues()
		searchString << searchStringValues()
	}

	@Unroll
	def "cloning works with script #script and searchString #searchString."() {
		when:
		def condition = new GroovyCondition()
		condition.scriptFileName = script
		condition.searchString = searchString

		and:
		def result = testClone(condition)

		then:
		result.scriptFileName == script
		result.searchString == searchString
		result.scriptFileName == condition.scriptFileName
		result.searchString == condition.searchString

		where:
		script << scriptValues()
		searchString << searchStringValues()
	}

	def "equals behaves as expected."() {
		expect:
		instance.equals(instance)
		!instance.equals(null)
		!instance.equals(new Object())
		!instance.equals(other)
		!other.equals(instance)

		where:
		instance                                  | other
		new GroovyCondition()                     | new GroovyCondition('script', 'searchString')
		new GroovyCondition('script')             | new GroovyCondition('script', 'searchString')
		new GroovyCondition(null, 'searchString') | new GroovyCondition('script', 'searchString')
	}

	def scriptValues() {
		[
				null,
				null,
				null,
				'',
				'',
				'',
				'file',
				'file',
				'file',
		]
	}

	def searchStringValues() {
		[
				null,
				'foo',
				'',
				null,
				'foo',
				'',
				null,
				'foo',
				'',
		]
	}
}
