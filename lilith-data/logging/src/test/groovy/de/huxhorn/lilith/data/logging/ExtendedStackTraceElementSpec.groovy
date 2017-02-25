/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Copyright 2007-2017 Joern Huxhorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.huxhorn.lilith.data.logging

import de.huxhorn.sulky.junit.JUnitTools
import spock.lang.Specification
import spock.lang.Unroll

class ExtendedStackTraceElementSpec extends Specification {

	private static final boolean IS_AT_LEAST_JAVA_9 = Boolean.valueOf(System.getProperty("java.version"))

	def parseInputValues() {
		[
				// invalid
				null,
				'foo',
				'methodName(Unknown Source)',
				'java.util.concurrent.FutureTask$Sync.innerRun(FutureTask.java:303',
				'java.util.concurrent.FutureTask$Sync.innerRun(:)',
				'java.util.concurrent.FutureTask$Sync.innerRun(FutureTask.java:foo)',

				// invalid extended info
				'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) [lilith.jar:0.9.35-SNAPSHOT',
				'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) ~[lilith.jar:0.9.35-SNAPSHOT',
				'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) ',
				'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079)  ',
				'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079)   ',
				'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) lilith.jar:0.9.35-SNAPSHOT]',
				'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) [lilith.jar]',

				// valid
				'.(Unknown Source)', // valid enough to prevent explosion in StackTraceElement c'tor
				'className.methodName(Unknown Source)',
				'java.lang.Thread.sleep(Native Method)',
				'java.util.concurrent.FutureTask$Sync.innerRun(FutureTask.java:303)',
				'java.util.concurrent.FutureTask$Sync.innerRun(FutureTask.java)',
				'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) [lilith.jar:0.9.35-SNAPSHOT]',
				'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) ~[lilith.jar:0.9.35-SNAPSHOT]',
				'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) [:0.9.35-SNAPSHOT]',
				'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) ~[:0.9.35-SNAPSHOT]',
				'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) [lilith.jar:]',
				'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) ~[lilith.jar:]',
				'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) [:]',
				'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) ~[:]',
				'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) [na:na]',
				'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) ~[na:na]',

				// from Java 9 StackTraceElement.toString() javadoc
				'com.foo.loader/foo@9.0/com.foo.Main.run(Main.java:101)',
				'com.foo.loader/foo@9.0/com.foo.Main.run(Main.java)',
				'com.foo.loader/foo@9.0/com.foo.Main.run(Unknown Source)',
				'com.foo.loader/foo@9.0/com.foo.Main.run(Native Method)',
				'com.foo.loader//com.foo.bar.App.run(App.java:12)',
				'acme@2.1/org.acme.Lib.test(Lib.java:80)',
				'MyClass.mash(MyClass.java:9)',
		]
	}

	def parseResultValues() {
		[
				// invalid
				null,
				null,
				null,
				null,
				null,
				null,

				// invalid extended info
				new ExtendedStackTraceElement(
						className: 'de.huxhorn.lilith.swing.MainFrame',
						methodName: 'setAccessEventSourceManager',
						fileName: 'MainFrame.java',
						lineNumber: 1079),

				new ExtendedStackTraceElement(
						className: 'de.huxhorn.lilith.swing.MainFrame',
						methodName: 'setAccessEventSourceManager',
						fileName: 'MainFrame.java',
						lineNumber: 1079),

				new ExtendedStackTraceElement(
						className: 'de.huxhorn.lilith.swing.MainFrame',
						methodName: 'setAccessEventSourceManager',
						fileName: 'MainFrame.java',
						lineNumber: 1079),

				new ExtendedStackTraceElement(
						className: 'de.huxhorn.lilith.swing.MainFrame',
						methodName: 'setAccessEventSourceManager',
						fileName: 'MainFrame.java',
						lineNumber: 1079),

				new ExtendedStackTraceElement(
						className: 'de.huxhorn.lilith.swing.MainFrame',
						methodName: 'setAccessEventSourceManager',
						fileName: 'MainFrame.java',
						lineNumber: 1079),

				new ExtendedStackTraceElement(
						className: 'de.huxhorn.lilith.swing.MainFrame',
						methodName: 'setAccessEventSourceManager',
						fileName: 'MainFrame.java',
						lineNumber: 1079),

				new ExtendedStackTraceElement(
						className: 'de.huxhorn.lilith.swing.MainFrame',
						methodName: 'setAccessEventSourceManager',
						fileName: 'MainFrame.java',
						lineNumber: 1079),

				// valid
				new ExtendedStackTraceElement(
						className: '',
						methodName: '',
						lineNumber: ExtendedStackTraceElement.UNKNOWN_SOURCE_LINE_NUMBER),

				new ExtendedStackTraceElement(
						className: 'className',
						methodName: 'methodName',
						lineNumber: ExtendedStackTraceElement.UNKNOWN_SOURCE_LINE_NUMBER),

				new ExtendedStackTraceElement(
						className: 'java.lang.Thread',
						methodName: 'sleep',
						lineNumber: ExtendedStackTraceElement.NATIVE_METHOD_LINE_NUMBER),

				new ExtendedStackTraceElement(
						className: 'java.util.concurrent.FutureTask$Sync',
						methodName: 'innerRun',
						fileName: 'FutureTask.java',
						lineNumber: 303),

				new ExtendedStackTraceElement(
						className: 'java.util.concurrent.FutureTask$Sync',
						methodName: 'innerRun',
						fileName: 'FutureTask.java',
						lineNumber: ExtendedStackTraceElement.UNKNOWN_SOURCE_LINE_NUMBER),

				new ExtendedStackTraceElement(
						className: 'de.huxhorn.lilith.swing.MainFrame',
						methodName: 'setAccessEventSourceManager',
						fileName: 'MainFrame.java',
						lineNumber: 1079,
						codeLocation: 'lilith.jar',
						version: '0.9.35-SNAPSHOT',
						exact: true),

				new ExtendedStackTraceElement(
						className: 'de.huxhorn.lilith.swing.MainFrame',
						methodName: 'setAccessEventSourceManager',
						fileName: 'MainFrame.java',
						lineNumber: 1079,
						codeLocation: 'lilith.jar',
						version: '0.9.35-SNAPSHOT',
						exact: false),

				new ExtendedStackTraceElement(
						className: 'de.huxhorn.lilith.swing.MainFrame',
						methodName: 'setAccessEventSourceManager',
						fileName: 'MainFrame.java',
						lineNumber: 1079,
						version: '0.9.35-SNAPSHOT',
						exact: true),

				new ExtendedStackTraceElement(
						className: 'de.huxhorn.lilith.swing.MainFrame',
						methodName: 'setAccessEventSourceManager',
						fileName: 'MainFrame.java',
						lineNumber: 1079,
						version: '0.9.35-SNAPSHOT',
						exact: false),

				new ExtendedStackTraceElement(
						className: 'de.huxhorn.lilith.swing.MainFrame',
						methodName: 'setAccessEventSourceManager',
						fileName: 'MainFrame.java',
						lineNumber: 1079,
						codeLocation: 'lilith.jar',
						exact: true),

				new ExtendedStackTraceElement(
						className: 'de.huxhorn.lilith.swing.MainFrame',
						methodName: 'setAccessEventSourceManager',
						fileName: 'MainFrame.java',
						lineNumber: 1079,
						codeLocation: 'lilith.jar',
						exact: false),

				new ExtendedStackTraceElement(
						className: 'de.huxhorn.lilith.swing.MainFrame',
						methodName: 'setAccessEventSourceManager',
						fileName: 'MainFrame.java',
						lineNumber: 1079,
						exact: true),

				new ExtendedStackTraceElement(
						className: 'de.huxhorn.lilith.swing.MainFrame',
						methodName: 'setAccessEventSourceManager',
						fileName: 'MainFrame.java',
						lineNumber: 1079,
						exact: false),

				new ExtendedStackTraceElement(
						className: 'de.huxhorn.lilith.swing.MainFrame',
						methodName: 'setAccessEventSourceManager',
						fileName: 'MainFrame.java',
						lineNumber: 1079,
						exact: true),

				new ExtendedStackTraceElement(
						className: 'de.huxhorn.lilith.swing.MainFrame',
						methodName: 'setAccessEventSourceManager',
						fileName: 'MainFrame.java',
						lineNumber: 1079,
						exact: false),

				// from Java 9 StackTraceElement.toString() javadoc

				// com.foo.loader/foo@9.0/com.foo.Main.run(Main.java:101)
				new ExtendedStackTraceElement(
						className: 'com.foo.Main',
						methodName: 'run',
						fileName: 'Main.java',
						lineNumber: 101,
						exact: false,
						classLoaderName: 'com.foo.loader',
						moduleName: 'foo',
						moduleVersion: '9.0'),

				// com.foo.loader/foo@9.0/com.foo.Main.run(Main.java)
				new ExtendedStackTraceElement(
						className: 'com.foo.Main',
						methodName: 'run',
						fileName: 'Main.java',
						lineNumber: ExtendedStackTraceElement.UNKNOWN_SOURCE_LINE_NUMBER,
						exact: false,
						classLoaderName: 'com.foo.loader',
						moduleName: 'foo',
						moduleVersion: '9.0'),

				// com.foo.loader/foo@9.0/com.foo.Main.run(Unknown Source)
				new ExtendedStackTraceElement(
						className: 'com.foo.Main',
						methodName: 'run',
						lineNumber: ExtendedStackTraceElement.UNKNOWN_SOURCE_LINE_NUMBER,
						exact: false,
						classLoaderName: 'com.foo.loader',
						moduleName: 'foo',
						moduleVersion: '9.0'),

				// com.foo.loader/foo@9.0/com.foo.Main.run(Native Method)
				new ExtendedStackTraceElement(
						className: 'com.foo.Main',
						methodName: 'run',
						lineNumber: ExtendedStackTraceElement.NATIVE_METHOD_LINE_NUMBER,
						exact: false,
						classLoaderName: 'com.foo.loader',
						moduleName: 'foo',
						moduleVersion: '9.0'),

				// com.foo.loader//com.foo.bar.App.run(App.java:12)
				new ExtendedStackTraceElement(
						className: 'com.foo.bar.App',
						methodName: 'run',
						fileName: 'App.java',
						lineNumber: 12,
						exact: false,
						classLoaderName: 'com.foo.loader'),

				// acme@2.1/org.acme.Lib.test(Lib.java:80)
				new ExtendedStackTraceElement(
						className: 'org.acme.Lib',
						methodName: 'test',
						fileName: 'Lib.java',
						lineNumber: 80,
						exact: false,
						moduleName: 'acme',
						moduleVersion: '2.1'),

				// MyClass.mash(MyClass.java:9)
				new ExtendedStackTraceElement(
						className: 'MyClass',
						methodName: 'mash',
						fileName: 'MyClass.java',
						lineNumber: 9,
						exact: false),
		]
	}

	@Unroll
	'Parsing #inputValue'() {
		when: 'parsing is working'
		ExtendedStackTraceElement parsed = ExtendedStackTraceElement.parseStackTraceElement(inputValue)

		then:
		parsed == expectedValue

		where:
		inputValue << parseInputValues()
		expectedValue << parseResultValues()
	}

	def parseSpecialInputValues() {
		[
				'//com.foo.bar.App.run(App.java:12)',
				'/@/com.foo.bar.App.run(App.java:12)',
		]
	}

	def parseSpecialResultValues() {
		[
				// //com.foo.bar.App.run(App.java:12)
				new ExtendedStackTraceElement(
						className: 'com.foo.bar.App',
						methodName: 'run',
						fileName: 'App.java',
						lineNumber: 12,
						exact: false),

				// /@/com.foo.bar.App.run(App.java:12)
				new ExtendedStackTraceElement(
						className: 'com.foo.bar.App',
						methodName: 'run',
						fileName: 'App.java',
						lineNumber: 12,
						exact: false),
		]
	}

	@Unroll
	'Parsing special #inputValue'() {
		when: 'parsing is working'
		ExtendedStackTraceElement parsed = ExtendedStackTraceElement.parseStackTraceElement(inputValue)

		then:
		parsed == expectedValue

		where:
		inputValue << parseSpecialInputValues()
		expectedValue << parseSpecialResultValues()
	}

	def inputValues() {
		[
				new ExtendedStackTraceElement(className: 'className'),
				new ExtendedStackTraceElement(methodName: 'methodName'),
				new ExtendedStackTraceElement(fileName: 'fileName'),
				new ExtendedStackTraceElement(lineNumber: 17),
				new ExtendedStackTraceElement(lineNumber: ExtendedStackTraceElement.NATIVE_METHOD_LINE_NUMBER),
				new ExtendedStackTraceElement(codeLocation: 'codeLocation'),
				new ExtendedStackTraceElement(version: 'version'),
				new ExtendedStackTraceElement(exact: true),
				new ExtendedStackTraceElement(classLoaderName: 'classLoaderName'),
				new ExtendedStackTraceElement(moduleName: 'moduleName'),
				new ExtendedStackTraceElement(moduleVersion: 'moduleVersion'),
				new ExtendedStackTraceElement(
						className: 'className',
						methodName: 'methodName',
						fileName: 'fileName',
						lineNumber: 17,
						codeLocation: 'codeLocation',
						version: 'version',
						exact: true),
				new ExtendedStackTraceElement(
						className: 'className',
						methodName: 'methodName',
						fileName: 'fileName',
						lineNumber: ExtendedStackTraceElement.NATIVE_METHOD_LINE_NUMBER,
						codeLocation: 'codeLocation',
						version: 'version',
						exact: true),
				new ExtendedStackTraceElement(
						className: 'className',
						methodName: 'methodName',
						fileName: 'fileName',
						lineNumber: 17,
						classLoaderName: 'ClassLoaderName',
						moduleName: 'ModuleName',
						moduleVersion: 'ModuleVersion',
						),
		]
	}

	@Unroll
	'Serialization of #extendedString'() {
		when: 'serialization works'
		def other = JUnitTools.testSerialization(inputValue)

		then:
		compare(inputValue, other)
		new ExtendedStackTraceElement() != other

		where:
		inputValue << inputValues()
		extendedString = inputValue.toString(true)
	}

	@Unroll
	'XML-Serialization of #extendedString'() {
		when: 'xml serialization works'
		def other = JUnitTools.testXmlSerialization(inputValue)

		then:
		compare(inputValue, other)
		new ExtendedStackTraceElement() != other

		where:
		inputValue << inputValues()
		extendedString = inputValue.toString(true)
	}

	@Unroll
	'Cloning of #extendedString'() {
		when: 'cloning works'
		def other = JUnitTools.testClone(inputValue)

		then:
		compare(inputValue, other)
		new ExtendedStackTraceElement() != other

		where:
		inputValue << inputValues()
		extendedString = inputValue.toString(true)
	}

	def 'Serialization of default constructor'() {
		when: 'serialization works'
		def inputValue = new ExtendedStackTraceElement()
		def other = JUnitTools.testSerialization(inputValue)

		then:
		compare(inputValue, other)
	}

	def 'XML-Serialization of default constructor'() {
		when: 'xml serialization works'
		def inputValue = new ExtendedStackTraceElement()
		def other = JUnitTools.testXmlSerialization(inputValue)

		then:
		compare(inputValue, other)
	}

	def 'Cloning of default constructor'() {
		when: 'cloning works'
		def inputValue = new ExtendedStackTraceElement()
		def other = JUnitTools.testClone(inputValue)

		then:
		compare(inputValue, other)
	}

	/**
	 * StackTraceElement requires at least className and methodName.
	 *
	 * @return valid input values
	 */
	List<ExtendedStackTraceElement> validInputValues(boolean onlyCompatible) {
		def result = []
		result += new ExtendedStackTraceElement(
						className: 'className',
						methodName: 'methodName'
				)

		result += new ExtendedStackTraceElement(
						className: 'className',
						methodName: 'methodName',
						fileName: 'fileName')

		result += new ExtendedStackTraceElement(
						className: 'className',
						methodName: 'methodName',
						lineNumber: 17)

		result += new ExtendedStackTraceElement(
						className: 'className',
						methodName: 'methodName',
						codeLocation: 'codeLocation')

		result += new ExtendedStackTraceElement(
						className: 'className',
						methodName: 'methodName',
						version: 'version')

		result += new ExtendedStackTraceElement(
						className: 'className',
						methodName: 'methodName',
						exact: true)

		result += new ExtendedStackTraceElement(
						className: 'className',
						methodName: 'methodName',
						fileName: 'fileName',
						lineNumber: 17,
						codeLocation: 'codeLocation',
						version: 'version',
						exact: true)

		result += new ExtendedStackTraceElement(
						className: 'className',
						methodName: 'methodName',
						fileName: 'fileName',
						lineNumber: -2,
						codeLocation: 'codeLocation',
						version: 'version',
						exact: true)

		result += new ExtendedStackTraceElement(
						className: 'className',
						methodName: 'methodName',
						fileName: 'fileName',
						lineNumber: -1,
						codeLocation: 'codeLocation',
						version: 'version',
						exact: true)

		result += new ExtendedStackTraceElement(
						className: 'className',
						methodName: 'methodName',
						fileName: 'fileName',
						lineNumber: -1,
						codeLocation: 'codeLocation',
						version: 'version',
						exact: false)

		if(!onlyCompatible || IS_AT_LEAST_JAVA_9) {
			// from Java 9 StackTraceElement.toString() javadoc

			// com.foo.loader/foo@9.0/com.foo.Main.run(Main.java:101)
			result += new ExtendedStackTraceElement(
					className: 'com.foo.Main',
					methodName: 'run',
					fileName: 'Main.java',
					lineNumber: 101,
					exact: false,
					classLoaderName: 'com.foo.loader',
					moduleName: 'foo',
					moduleVersion: '9.0')

			// com.foo.loader/foo@9.0/com.foo.Main.run(Main.java)
			result += new ExtendedStackTraceElement(
					className: 'com.foo.Main',
					methodName: 'run',
					fileName: 'Main.java',
					lineNumber: ExtendedStackTraceElement.UNKNOWN_SOURCE_LINE_NUMBER,
					exact: false,
					classLoaderName: 'com.foo.loader',
					moduleName: 'foo',
					moduleVersion: '9.0')

			// com.foo.loader/foo@9.0/com.foo.Main.run(Unknown Source)
			result += new ExtendedStackTraceElement(
					className: 'com.foo.Main',
					methodName: 'run',
					lineNumber: ExtendedStackTraceElement.UNKNOWN_SOURCE_LINE_NUMBER,
					exact: false,
					classLoaderName: 'com.foo.loader',
					moduleName: 'foo',
					moduleVersion: '9.0')

			// com.foo.loader/foo@9.0/com.foo.Main.run(Native Method)
			result += new ExtendedStackTraceElement(
					className: 'com.foo.Main',
					methodName: 'run',
					lineNumber: ExtendedStackTraceElement.NATIVE_METHOD_LINE_NUMBER,
					exact: false,
					classLoaderName: 'com.foo.loader',
					moduleName: 'foo',
					moduleVersion: '9.0')

			// com.foo.loader//com.foo.bar.App.run(App.java:12)
			result += new ExtendedStackTraceElement(
					className: 'com.foo.bar.App',
					methodName: 'run',
					fileName: 'App.java',
					lineNumber: 12,
					exact: false,
					classLoaderName: 'com.foo.loader')

			// acme@2.1/org.acme.Lib.test(Lib.java:80)
			result += new ExtendedStackTraceElement(
					className: 'org.acme.Lib',
					methodName: 'test',
					fileName: 'Lib.java',
					lineNumber: 80,
					exact: false,
					moduleName: 'acme',
					moduleVersion: '2.1')

			// MyClass.mash(MyClass.java:9)
			result += new ExtendedStackTraceElement(
					className: 'MyClass',
					methodName: 'mash',
					fileName: 'MyClass.java',
					lineNumber: 9,
					exact: false)

			// org.acme.Lib.test(Lib.java:80)
			result += new ExtendedStackTraceElement(
					className: 'org.acme.Lib',
					methodName: 'test',
					fileName: 'Lib.java',
					lineNumber: 80,
					exact: false,
					classLoaderName: '',
					moduleName: '',
					moduleVersion: '')

			// foo/org.acme.Lib.test(Lib.java:80)
			result += new ExtendedStackTraceElement(
					className: 'org.acme.Lib',
					methodName: 'test',
					fileName: 'Lib.java',
					lineNumber: 80,
					exact: false,
					classLoaderName: '',
					moduleName: 'foo',
					moduleVersion: '')
		}
		return result
	}

	def validInputValueToStringExtendedStrings() {
		[
				'className.methodName(Unknown Source)',
				'className.methodName(fileName)',
				'className.methodName(Unknown Source)',
				'className.methodName(Unknown Source) ~[codeLocation:na]',
				'className.methodName(Unknown Source) ~[na:version]',
				'className.methodName(Unknown Source)',
				'className.methodName(fileName:17) [codeLocation:version]',
				'className.methodName(Native Method) [codeLocation:version]',
				'className.methodName(fileName) [codeLocation:version]',
				'className.methodName(fileName) ~[codeLocation:version]',
				// from Java 9 StackTraceElement.toString() javadoc
				'com.foo.loader/foo@9.0/com.foo.Main.run(Main.java:101)',
				'com.foo.loader/foo@9.0/com.foo.Main.run(Main.java)',
				'com.foo.loader/foo@9.0/com.foo.Main.run(Unknown Source)',
				'com.foo.loader/foo@9.0/com.foo.Main.run(Native Method)',
				'com.foo.loader//com.foo.bar.App.run(App.java:12)',
				'acme@2.1/org.acme.Lib.test(Lib.java:80)',
				'MyClass.mash(MyClass.java:9)',
				'org.acme.Lib.test(Lib.java:80)',
				'foo/org.acme.Lib.test(Lib.java:80)',
		]
	}

	def validInputValueGetExtendedStringStrings() {
		[
				null,
				null,
				null,
				'~[codeLocation:na]',
				'~[na:version]',
				null,
				'[codeLocation:version]',
				'[codeLocation:version]',
				'[codeLocation:version]',
				'~[codeLocation:version]',
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
		]
	}

	@Unroll
	'Plain toString compatibility of #extendedString'() {
		setup:
		StackTraceElement ste = inputValue.stackTraceElement

		expect:
		ste.toString() == inputValue.toString(false)

		where:
		inputValue << validInputValues(true)
		extendedString = inputValue.toString(true)
	}

	@Unroll
	'Extended toString compatibility of #extendedString'() {
		expect:
		expectedValue == inputValue.toString(true)

		where:
		inputValue << validInputValues(false)
		expectedValue << validInputValueToStringExtendedStrings()
		extendedString = inputValue.toString(true)
	}

	@Unroll
	'getExtendedString compatibility of #extendedString'() {
		expect:
		expectedValue == inputValue.getExtendedString()

		where:
		inputValue << validInputValues(false)
		expectedValue << validInputValueGetExtendedStringStrings()
		extendedString = inputValue.toString(true)
	}

	@SuppressWarnings("ChangeToOperator")
	@Unroll
	"equals behaves as expected for #extendedString."() {
		setup:
		def instance = new ExtendedStackTraceElement()

		expect:
		instance.equals(instance)
		!instance.equals(null)
		!instance.equals(new Object())
		!instance.equals(inputValue)
		!inputValue.equals(instance)

		where:
		inputValue << inputValues()
		extendedString = inputValue.toString(true)
	}

	def "new ExtendedStackTraceElement(null) throws expected exception."() {
		when:
		new ExtendedStackTraceElement(null)

		then:
		NullPointerException ex = thrown()
		ex.message == 'stackTraceElement must not be null!'
	}

	@Unroll
	"new ExtendedStackTraceElement(#input) works as expected."() {
		when:
		def instance = new ExtendedStackTraceElement(input)

		then:
		instance.stackTraceElement == input
		!instance.stackTraceElement.is(input)

		where:
		input << [
		        new StackTraceElement('declaringClass', 'methodName', 'fileName', 17),
				new StackTraceElement('declaringClass', 'methodName', null, ExtendedStackTraceElement.UNKNOWN_SOURCE_LINE_NUMBER),
				new StackTraceElement('declaringClass', 'methodName', null, ExtendedStackTraceElement.NATIVE_METHOD_LINE_NUMBER),
				new StackTraceElement('declaringClass', 'methodName', null, 17),
				new StackTraceElement('', '', null, ExtendedStackTraceElement.UNKNOWN_SOURCE_LINE_NUMBER),
				new StackTraceElement('', '', null, ExtendedStackTraceElement.NATIVE_METHOD_LINE_NUMBER),
				new StackTraceElement('', '', null, 17),
				new StackTraceElement('', '', null, -3),
		]
	}

	def "Instances with missing className or methodName won't return a StackTraceElement."() {
		expect:
		input.stackTraceElement == null

		where:
		input << [
				new ExtendedStackTraceElement(
						className: null,
						methodName: null
				),
				new ExtendedStackTraceElement(
						className: null,
						methodName: 'methodName'
				),
				new ExtendedStackTraceElement(
						className: 'className',
						methodName: null
				),
		]
	}

	def compare(ExtendedStackTraceElement inputValue, ExtendedStackTraceElement other) {
		assert inputValue == other
		if (inputValue) {
			assert !(inputValue.is(other))

			assert inputValue.className == other.className
			assert inputValue.methodName == other.methodName
			assert inputValue.fileName == other.fileName
			assert inputValue.lineNumber == other.lineNumber
			assert inputValue.codeLocation == other.codeLocation
			assert inputValue.version == other.version
			assert inputValue.exact == other.exact
		}
		return true
	}
}
