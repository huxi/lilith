/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2013 Joern Huxhorn
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
 * Copyright 2007-2013 Joern Huxhorn
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

    def parseInputValues() {
        [
            null,
            'foo',
            'className.methodName(Unknown Source)',
            'java.lang.Thread.sleep(Native Method)',
            'java.util.concurrent.FutureTask$Sync.innerRun(FutureTask.java:303)',
            'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) [lilith.jar:0.9.35-SNAPSHOT]',
            'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) ~[lilith.jar:0.9.35-SNAPSHOT]',
            'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) [:0.9.35-SNAPSHOT]',
            'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) ~[:0.9.35-SNAPSHOT]',
            'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) [lilith.jar:]',
            'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) ~[lilith.jar:]',
            'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) [:]',
            'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) ~[:]',
            'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) [na:na]',
            'de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) ~[na:na]'
        ]
    }

    def parseResultValues() {
        [
            null,
            null,
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
                exact: false)
        ]
    }

    @Unroll
    def 'Parsing #inputValue'() {
        when: 'parsing is working'
        ExtendedStackTraceElement parsed = ExtendedStackTraceElement.parseStackTraceElement(inputValue)

        then:
        parsed == expectedValue

        where:
        inputValue << parseInputValues()
        expectedValue << parseResultValues()
    }


    def inputValues() {
        [
            new ExtendedStackTraceElement(className: 'className'),
            new ExtendedStackTraceElement(methodName: 'methodName'),
            new ExtendedStackTraceElement(fileName: 'fileName'),
            new ExtendedStackTraceElement(lineNumber: 17),
            new ExtendedStackTraceElement(codeLocation: 'codeLocation'),
            new ExtendedStackTraceElement(version: 'version'),
            new ExtendedStackTraceElement(exact: true),
            new ExtendedStackTraceElement(
                className: 'className',
                methodName: 'methodName',
                fileName: 'fileName',
                lineNumber: 17,
                codeLocation: 'codeLocation',
                version: 'version',
                exact: true)
        ]
    }

    @Unroll
    def 'Serialization of #inputValue'() {
        when: 'serialization works'
        def other = JUnitTools.testSerialization(inputValue)

        then:
        compare(inputValue, other)
        new ExtendedStackTraceElement() != other

        where:
        inputValue << inputValues()
    }

    @Unroll
    def 'XML-Serialization of #inputValue'() {
        when: 'xml serialization works'
        def other = JUnitTools.testXmlSerialization(inputValue)

        then:
        compare(inputValue, other)
        new ExtendedStackTraceElement() != other

        where:
        inputValue << inputValues()
    }

    @Unroll
    def 'Cloning of #inputValue'() {
        when: 'cloning works'
        def other = JUnitTools.testClone(inputValue)

        then:
        compare(inputValue, other)
        new ExtendedStackTraceElement() != other

        where:
        inputValue << inputValues()
    }

    /**
     * StackTraceElement requires at least className and methodName.
     *
     * @return valid input values
     */
    def validInputValues() {
        [
            new ExtendedStackTraceElement(
                className: 'className',
                methodName: 'methodName'
            ),
            new ExtendedStackTraceElement(
                className: 'className',
                methodName: 'methodName',
                fileName: 'fileName'),
            new ExtendedStackTraceElement(
                className: 'className',
                methodName: 'methodName',
                lineNumber: 17),
            new ExtendedStackTraceElement(
                className: 'className',
                methodName: 'methodName',
                codeLocation: 'codeLocation'),
            new ExtendedStackTraceElement(
                className: 'className',
                methodName: 'methodName',
                version: 'version'),
            new ExtendedStackTraceElement(
                className: 'className',
                methodName: 'methodName',
                exact: true),
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
                lineNumber: -2,
                codeLocation: 'codeLocation',
                version: 'version',
                exact: true),
            new ExtendedStackTraceElement(
                className: 'className',
                methodName: 'methodName',
                fileName: 'fileName',
                lineNumber: -1,
                codeLocation: 'codeLocation',
                version: 'version',
                exact: true),
            new ExtendedStackTraceElement(
                className: 'className',
                methodName: 'methodName',
                fileName: 'fileName',
                lineNumber: -1,
                codeLocation: 'codeLocation',
                version: 'version',
                exact: false)
        ]
    }

    def validInputValueExtendedStrings() {
        [
            'className.methodName(Unknown Source)',
            'className.methodName(fileName)',
            'className.methodName(Unknown Source)',
            'className.methodName(Unknown Source) ~[codeLocation:]',
            'className.methodName(Unknown Source) ~[:version]',
            'className.methodName(Unknown Source)',
            'className.methodName(fileName:17) [codeLocation:version]',
            'className.methodName(Native Method) [codeLocation:version]',
            'className.methodName(fileName) [codeLocation:version]',
            'className.methodName(fileName) ~[codeLocation:version]'
        ]
    }

    @Unroll
    def 'Plain toString compatibility of #inputValue'() {
        setup:
        StackTraceElement ste = inputValue.stackTraceElement

        expect:
        ste.toString() == inputValue.toString(false)

        where:
        inputValue << validInputValues()
    }

    @Unroll
    def 'Extended toString compatibility of #inputValue'() {
        expect:
        expectedValue == inputValue.toString(true)

        where:
        inputValue << validInputValues()
        expectedValue << validInputValueExtendedStrings()
    }

    def compare(ExtendedStackTraceElement inputValue, ExtendedStackTraceElement other) {
        assert inputValue == other
        if(inputValue) {
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
