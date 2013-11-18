/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2014 Joern Huxhorn
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
 * Copyright 2007-2014 Joern Huxhorn
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

import spock.lang.Specification
import spock.lang.Unroll

import java.util.regex.Matcher

class ThrowableInfoParserSpec extends Specification {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");


    def parseResultValues() {
        [
            // simple example
            new ThrowableInfo(
                name: "java.lang.RuntimeException",
                stackTrace: [
                    new ExtendedStackTraceElement(
                        className: "sun.reflect.NativeConstructorAccessorImpl",
                        methodName: "newInstance0",
                        lineNumber: ExtendedStackTraceElement.NATIVE_METHOD_LINE_NUMBER
                    ),
                    new ExtendedStackTraceElement(
                        className: "sun.reflect.NativeConstructorAccessorImpl",
                        methodName: "newInstance",
                        fileName: "NativeConstructorAccessorImpl.java",
                        lineNumber: 57
                    ),
                    new ExtendedStackTraceElement(
                        className: "sun.reflect.DelegatingConstructorAccessorImpl",
                        methodName: "newInstance",
                        fileName: "DelegatingConstructorAccessorImpl.java",
                        lineNumber: 45
                    ),
                ]
            ),
            // end of simple example

            // less simple example
            new ThrowableInfo(
                name: "java.lang.RuntimeException",
                message: 'Outer',
                stackTrace: [
                    new ExtendedStackTraceElement(
                        className: "de.huxhorn.lilith.sandbox.Log4jSandbox",
                        methodName: "main",
                        fileName: 'Log4jSandbox.java',
                        lineNumber: 78
                    )
                ],
                suppressed: [
                    new ThrowableInfo(
                        name: "java.lang.RuntimeException",
                        message: 'Suppressed1',
                        stackTrace: [
                            new ExtendedStackTraceElement(
                                className: "de.huxhorn.lilith.sandbox.Log4jSandbox",
                                methodName: "main",
                                fileName: 'Log4jSandbox.java',
                                lineNumber: 80
                            )
                        ]
                    ),
                    new ThrowableInfo(
                        name: "java.lang.RuntimeException",
                        message: 'Suppressed2',
                        stackTrace: [
                            new ExtendedStackTraceElement(
                                className: "de.huxhorn.lilith.sandbox.Log4jSandbox",
                                methodName: "main",
                                fileName: 'Log4jSandbox.java',
                                lineNumber: 81
                            )
                        ]
                    )
                ],
                cause: new ThrowableInfo(
                    name: "java.lang.RuntimeException",
                    message: 'Cause',
                    omittedElements: 1
                )
            ),
            // end of less simple example

            // complex example
            new ThrowableInfo(
                name: "java.lang.RuntimeException",
                message: "Hi.",
                stackTrace: [
                    new ExtendedStackTraceElement(
                        className: "de.huxhorn.lilith.sandbox.Log4jSandbox\$InnerClass",
                        methodName: "execute",
                        fileName: "Log4jSandbox.java",
                        lineNumber: 49
                    ),
                    new ExtendedStackTraceElement(
                        className: "de.huxhorn.lilith.sandbox.Log4jSandbox",
                        methodName: "main",
                        fileName: "Log4jSandbox.java",
                        lineNumber: 86
                    ),
                ],
                cause: new ThrowableInfo(
                    name: "java.lang.RuntimeException",
                    message: "Hi Cause.",
                    stackTrace: [
                        new ExtendedStackTraceElement(
                            className: "de.huxhorn.lilith.sandbox.Log4jSandbox\$InnerClass",
                            methodName: "foobar",
                            fileName: "Log4jSandbox.java",
                            lineNumber: 60
                        ),
                        new ExtendedStackTraceElement(
                            className: "de.huxhorn.lilith.sandbox.Log4jSandbox\$InnerClass",
                            methodName: "execute",
                            fileName: "Log4jSandbox.java",
                            lineNumber: 45
                        ),
                    ],
                    omittedElements: 1,
                    suppressed: [
                        new ThrowableInfo(
                            name: "java.lang.RuntimeException",
                            stackTrace: [
                                new ExtendedStackTraceElement(
                                    className: "de.huxhorn.lilith.sandbox.Log4jSandbox\$InnerClass",
                                    methodName: "foobar",
                                    fileName: "Log4jSandbox.java",
                                    lineNumber: 61
                                )
                            ],
                            omittedElements: 2
                        ),
                        new ThrowableInfo(
                            name: "java.lang.RuntimeException",
                            message: "Single line",
                            stackTrace: [
                                new ExtendedStackTraceElement(
                                    className: "de.huxhorn.lilith.sandbox.Log4jSandbox\$InnerClass",
                                    methodName: "foobar",
                                    fileName: "Log4jSandbox.java",
                                    lineNumber: 62
                                )
                            ],
                            omittedElements: 2
                        ),
                        new ThrowableInfo(
                            name: "java.lang.RuntimeException",
                            message: "With cause and suppressed",
                            stackTrace: [
                                new ExtendedStackTraceElement(
                                    className: "de.huxhorn.lilith.sandbox.Log4jSandbox\$InnerClass",
                                    methodName: "foobar",
                                    fileName: "Log4jSandbox.java",
                                    lineNumber: 63
                                )
                            ],
                            suppressed: [
                                new ThrowableInfo(
                                    name: "java.lang.RuntimeException",
                                    message: "Inner Suppressed",
                                    stackTrace: [
                                        new ExtendedStackTraceElement(
                                            className: "de.huxhorn.lilith.sandbox.Log4jSandbox\$InnerClass",
                                            methodName: "foobar",
                                            fileName: "Log4jSandbox.java",
                                            lineNumber: 64
                                        )
                                    ],
                                    omittedElements: 2
                                ),
                                new ThrowableInfo(
                                    name: "java.lang.RuntimeException",
                                    message: "Inner Suppressed with Cause",
                                    stackTrace: [
                                        new ExtendedStackTraceElement(
                                            className: "de.huxhorn.lilith.sandbox.Log4jSandbox\$InnerClass",
                                            methodName: "foobar",
                                            fileName: "Log4jSandbox.java",
                                            lineNumber: 65
                                        )
                                    ],
                                    omittedElements: 2,
                                    cause: new ThrowableInfo(
                                        name: "java.lang.RuntimeException",
                                        message: "Inner Cause",
                                        omittedElements: 3
                                    )
                                ),
                            ],
                            omittedElements: 2,
                            cause: new ThrowableInfo(
                                name: "java.lang.RuntimeException",
                                message: "Cause",
                                omittedElements: 3
                            )
                        ),
                        new ThrowableInfo(
                            name: "java.lang.RuntimeException",
                            message: "Multi\nline",
                            stackTrace: [
                                new ExtendedStackTraceElement(
                                    className: "de.huxhorn.lilith.sandbox.Log4jSandbox\$InnerClass",
                                    methodName: "foobar",
                                    fileName: "Log4jSandbox.java",
                                    lineNumber: 67
                                )
                            ],
                            omittedElements: 2
                        )
                    ]
                )
            ),
            // end of complex example

            // broken input
            new ThrowableInfo(
                    stackTrace: [
                            new ExtendedStackTraceElement(
                                    className: "sun.reflect.NativeConstructorAccessorImpl",
                                    methodName: "newInstance0",
                                    lineNumber: ExtendedStackTraceElement.NATIVE_METHOD_LINE_NUMBER
                            ),
                            new ExtendedStackTraceElement(
                                    className: "sun.reflect.NativeConstructorAccessorImpl",
                                    methodName: "newInstance",
                                    fileName: "NativeConstructorAccessorImpl.java",
                                    lineNumber: 57
                            ),
                            new ExtendedStackTraceElement(
                                    className: "sun.reflect.DelegatingConstructorAccessorImpl",
                                    methodName: "newInstance",
                                    fileName: "DelegatingConstructorAccessorImpl.java",
                                    lineNumber: 45
                            ),
                    ]
            ),
            // end of broken input
        ]
    }

    def parseInputValues() {
        [
            // simple example
            'java.lang.RuntimeException' + LINE_SEPARATOR +
            '\tat sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)' + LINE_SEPARATOR +
            '\tat sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:57)' + LINE_SEPARATOR +
            '\tat sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)' + LINE_SEPARATOR,
            // end of simple example

            // less simple example
            'java.lang.RuntimeException: Outer' + LINE_SEPARATOR +
            '\tat de.huxhorn.lilith.sandbox.Log4jSandbox.main(Log4jSandbox.java:78)' + LINE_SEPARATOR +
            '\tSuppressed: java.lang.RuntimeException: Suppressed1' + LINE_SEPARATOR +
            '\t\tat de.huxhorn.lilith.sandbox.Log4jSandbox.main(Log4jSandbox.java:80)' + LINE_SEPARATOR +
            '\tSuppressed: java.lang.RuntimeException: Suppressed2' + LINE_SEPARATOR +
            '\t\tat de.huxhorn.lilith.sandbox.Log4jSandbox.main(Log4jSandbox.java:81)' + LINE_SEPARATOR +
            'Caused by: java.lang.RuntimeException: Cause' + LINE_SEPARATOR +
            '\t... 1 more' + LINE_SEPARATOR,
            // end of less simple example

            // complex example
            'java.lang.RuntimeException: Hi.' + LINE_SEPARATOR +
            '\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.execute(Log4jSandbox.java:49)' + LINE_SEPARATOR +
            '\tat de.huxhorn.lilith.sandbox.Log4jSandbox.main(Log4jSandbox.java:86)' + LINE_SEPARATOR +
            'Caused by: java.lang.RuntimeException: Hi Cause.' + LINE_SEPARATOR +
            '\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.foobar(Log4jSandbox.java:60)' + LINE_SEPARATOR +
            '\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.execute(Log4jSandbox.java:45)' + LINE_SEPARATOR +
            '\t... 1 more' + LINE_SEPARATOR +
            '\tSuppressed: java.lang.RuntimeException' + LINE_SEPARATOR +
            '\t\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.foobar(Log4jSandbox.java:61)' + LINE_SEPARATOR +
            '\t\t... 2 more' + LINE_SEPARATOR +
            '\tSuppressed: java.lang.RuntimeException: Single line' + LINE_SEPARATOR +
            '\t\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.foobar(Log4jSandbox.java:62)' + LINE_SEPARATOR +
            '\t\t... 2 more' + LINE_SEPARATOR +
            '\tSuppressed: java.lang.RuntimeException: With cause and suppressed' + LINE_SEPARATOR +
            '\t\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.foobar(Log4jSandbox.java:63)' + LINE_SEPARATOR +
            '\t\t... 2 more' + LINE_SEPARATOR +
            '\t\tSuppressed: java.lang.RuntimeException: Inner Suppressed' + LINE_SEPARATOR +
            '\t\t\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.foobar(Log4jSandbox.java:64)' + LINE_SEPARATOR +
            '\t\t\t... 2 more' + LINE_SEPARATOR +
            '\t\tSuppressed: java.lang.RuntimeException: Inner Suppressed with Cause' + LINE_SEPARATOR +
            '\t\t\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.foobar(Log4jSandbox.java:65)' + LINE_SEPARATOR +
            '\t\t\t... 2 more' + LINE_SEPARATOR +
            '\t\tCaused by: java.lang.RuntimeException: Inner Cause' + LINE_SEPARATOR +
            '\t\t\t... 3 more' + LINE_SEPARATOR +
            '\tCaused by: java.lang.RuntimeException: Cause' + LINE_SEPARATOR +
            '\t\t... 3 more' + LINE_SEPARATOR +
            '\tSuppressed: java.lang.RuntimeException: Multi' + LINE_SEPARATOR +
            'line' + LINE_SEPARATOR +
            '\t\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.foobar(Log4jSandbox.java:67)' + LINE_SEPARATOR +
            '\t\t... 2 more' + LINE_SEPARATOR,
            // end of complex example

            // broken input
            '\tat sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)' + LINE_SEPARATOR +
            '\tat sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:57)' + LINE_SEPARATOR +
            '\tat sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)' + LINE_SEPARATOR,
            // end of broken input
        ]
    }

    @Unroll
    def "parse"() {
        when:
        def parsed = ThrowableInfoParser.parse(inputValue)

        then:
        expectedValue == parsed

        where:
        inputValue << parseInputValues()
        expectedValue << parseResultValues()

    }

    @Unroll
    def "splitLines"() {
        when:
        def split = ThrowableInfoParser.splitLines(inputValue)

        then:
        split == (expectedValue as List<String>)

        where:
        inputValue << [
                null,
                '',
                'Foo',
                'Foo\r\n\r\n\nBar\n',
                'Foo\r\n\r\r\r\r\n\nBar\n',
                'Foo\n\n\nBar\n',
                'Foo\n\n\nBar\n\n',
                '\nFoo\n\n\nBar\n\n',
                '\nFoo\n\tx\n y\nBar\n\n'
            ]
        expectedValue << [
                null,
                [''],
                ['Foo'],
                ['Foo', '', '', 'Bar'],
                ['Foo', '', '', 'Bar'],
                ['Foo', '', '', 'Bar'],
                ['Foo', '', '', 'Bar', ''],
                ['', 'Foo', '', '', 'Bar', ''],
                ['', 'Foo', '\tx', ' y', 'Bar', '']
            ]
    }

    @Unroll
    def "omittedMatcher with '#inputValue'"() {
        when:
        Matcher m = ThrowableInfoParser.omittedMatcher(inputValue);

        then:
        m.matches() == inputMatches
        if(inputMatches) {
            assert m.groupCount() == 2
            assert m.group(0) == inputValue
            assert m.group(1) == inputIndent
            assert m.group(2) == inputAmount
        }

        where:
        inputValue << [
                "\t... 17 more",
                "\t.. 17 more",
                "\t\t... 17 more",
                "... 17 more"
            ]

        inputIndent << [
                "\t",
                null,
                "\t\t",
                null
            ]

        inputAmount << [
                "17",
                null,
                "17",
                null
            ]

        inputMatches << [
                true,
                false,
                true,
                false
            ]
    }

    @Unroll
    def "atMatcher with '#inputValue'"() {
        when:
        Matcher m = ThrowableInfoParser.atMatcher(inputValue);

        then:
        m.matches() == inputMatches
        if(inputMatches) {
            assert m.groupCount() == 2
            assert m.group(0) == inputValue
            assert m.group(1) == inputIndent
            assert m.group(2) == inputRemainder
        }

        where:
        inputValue << [
                "\tat foo bar",
                "\tta foo bar",
                "\t\tat foo bar",
                "\t\tat ",
                "at foo bar"
        ]

        inputIndent << [
                "\t",
                null,
                "\t\t",
                null,
                null
            ]

        inputRemainder << [
                "foo bar",
                null,
                "foo bar",
                null,
                null
            ]

        inputMatches << [
                true,
                false,
                true,
                false,
                false
            ]
    }

    @Unroll
    def "messageMatcher with '#inputValue'"() {
        when:
        Matcher m = ThrowableInfoParser.messageMatcher(inputValue);

        then:
        m.matches() == inputMatches
        if(inputMatches) {
            assert m.groupCount() == 3
            assert m.group(0) == inputValue
            assert m.group(1) == inputIndent
            assert m.group(2) == inputPrefix
            assert m.group(3) == inputRemainder
        }

        where:
        inputValue << [
                "\tCaused by: foo",
                "foo",
                "",
                "\t\tSuppressed: bar"
        ]

        inputIndent << [
                "\t",
                "",
                "",
                "\t\t"
        ]

        inputPrefix << [
                "Caused by: ",
                null,
                null,
                "Suppressed: "
        ]

        inputRemainder << [
                "foo",
                "foo",
                "",
                "bar"
        ]

        inputMatches << [
                true,
                true,
                true,
                true
        ]
    }

    def compare(ThrowableInfo inputValue, ThrowableInfo other) {
        assert inputValue == other
        if(inputValue) {
            assert !(inputValue.is(other))

            assert inputValue.name == other.name

            assert inputValue.message == other.message

            assert inputValue.stackTrace == other.stackTrace
            if(inputValue.stackTrace) {
                assert !(inputValue.stackTrace.is(other.stackTrace))
                for(int i=0; i<inputValue.stackTrace.length; i++) {
                    assert !(inputValue.stackTrace[i].is(other.stackTrace[i]))
                }
            }

            assert inputValue.omittedElements == other.omittedElements

            assert inputValue.suppressed == other.suppressed
            if(inputValue.suppressed) {
                assert !(inputValue.suppressed.is(other.suppressed))
                for(int i=0; i<inputValue.suppressed.length; i++) {
                    assert !(inputValue.suppressed[i].is(other.suppressed[i]))
                }
            }

            assert inputValue.cause == other.cause
            if(inputValue.cause) {
                assert !(inputValue.cause.is(other.cause))
            }
        }
        return true
    }
}
