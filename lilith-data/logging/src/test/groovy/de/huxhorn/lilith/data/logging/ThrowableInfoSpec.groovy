/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2016 Joern Huxhorn
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
 * Copyright 2007-2016 Joern Huxhorn
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

class ThrowableInfoSpec extends Specification {

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
                            message: "Multi"+LINE_SEPARATOR+"line",
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
            )
            // end of complex example
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
            '\t\t... 2 more' + LINE_SEPARATOR
            // end of complex example
        ]
    }

    @Unroll
    def "validate toString"() {
        when:
        def str = inputValue.toString()

        then:
        str == expectedValue

        where:
        inputValue << parseResultValues()
        expectedValue << parseInputValues()
    }

    def inputValues() {
        [
            new ThrowableInfo(name: 'name'),
            new ThrowableInfo(message: 'message'),
            new ThrowableInfo(omittedElements: 3),
            new ThrowableInfo(stackTrace: [
                    new ExtendedStackTraceElement(className: 'className',
                        methodName: 'methodName',
                        fileName: 'fileName',
                        lineNumber: -1,
                        codeLocation: 'codeLocation',
                        version: 'version',
                        exact: false
                    ),
                    new ExtendedStackTraceElement(className: 'className',
                        methodName: 'methodName2',
                        fileName: 'fileName',
                        lineNumber: -1,
                        codeLocation: 'codeLocation',
                        version: 'version',
                        exact: false
                    )
                ]
            ),
            new ThrowableInfo(cause: new ThrowableInfo(name: 'cause')),
            new ThrowableInfo(suppressed: [
                    new ThrowableInfo(name: 'suppressed1'),
                    new ThrowableInfo(name: 'suppressed2')
                ]
            ),
            new ThrowableInfo(
                name: 'name',
                message: 'message',
                omittedElements: 3,
                stackTrace: [
                    new ExtendedStackTraceElement(className: 'className',
                        methodName: 'methodName1',
                        fileName: 'fileName',
                        lineNumber: -1,
                        codeLocation: 'codeLocation',
                        version: 'version',
                        exact: false
                    ),
                    new ExtendedStackTraceElement(className: 'className',
                        methodName: 'methodName2',
                        fileName: 'fileName',
                        lineNumber: -1,
                        codeLocation: 'codeLocation',
                        version: 'version',
                        exact: false
                    )
                ],
                cause: new ThrowableInfo(name: 'cause'),
                suppressed: [
                    new ThrowableInfo(name: 'suppressed1'),
                    new ThrowableInfo(name: 'suppressed2')
                ]
            )
        ]
    }

    @Unroll
    def 'Serialization of inputValue'() {
        when: 'serialization works'
        def other = JUnitTools.testSerialization(inputValue)

        then:
        compare(inputValue, other)
        new ThrowableInfo() != other

        where:
        inputValue << inputValues()
    }

    @Unroll
    def 'XML-Serialization of inputValue'() {
        when: 'xml serialization works'
        def other = JUnitTools.testXmlSerialization(inputValue)

        then:
        compare(inputValue, other)
        new ThrowableInfo() != other

        where:
        inputValue << inputValues()
    }

    @Unroll
    def 'Cloning of inputValue'() {
        when: 'cloning works'
        def other = JUnitTools.testClone(inputValue)

        then:
        compare(inputValue, other)
        new ThrowableInfo() != other

        where:
        inputValue << inputValues()
    }

    def 'Serialization of default constructor'() {
        when: 'serialization works'
        def inputValue = new ThrowableInfo()
        def other = JUnitTools.testSerialization(inputValue)

        then:
        compare(inputValue, other)
    }

    def 'XML-Serialization of default constructor'() {
        when: 'xml serialization works'
        def inputValue = new ThrowableInfo()
        def other = JUnitTools.testXmlSerialization(inputValue)

        then:
        compare(inputValue, other)
    }

    def 'Cloning of default constructor'() {
        when: 'cloning works'
        def inputValue = new ThrowableInfo()
        def other = JUnitTools.testClone(inputValue)

        then:
        compare(inputValue, other)
    }

	@Unroll
	def 'Serialization of broken instances.'() {
		when: 'serialization works'
		def other = JUnitTools.testSerialization(inputValue)

		then:
		compare(inputValue, other)

		where:
		inputValue << brokenInstances()
	}

	@Unroll
	def 'XML-Serialization of broken instances.'() {
		when: 'xml serialization works'
		def other = JUnitTools.testXmlSerialization(inputValue)

		then:
		compare(inputValue, other)

		where:
		inputValue << brokenInstances()
	}

	@Unroll
	def 'Cloning of broken instances.'() {
		when: 'cloning works'
		def other = JUnitTools.testClone(inputValue)

		then:
		compare(inputValue, other)

		where:
		inputValue << brokenInstances()
	}

	@Unroll
	def 'toString() of broken instances.'() {
		expect:
		inputValue.toString() != null

		where:
		inputValue << brokenInstances()
	}

	def brokenInstances() {
		def recursiveCause = new ThrowableInfo()
		recursiveCause.cause = recursiveCause

		def recursiveSuppressed = new ThrowableInfo()
		recursiveSuppressed.suppressed = [recursiveSuppressed]

		def nullSuppressed1 = new ThrowableInfo(suppressed: [null])
		def nullSuppressed2 = new ThrowableInfo(suppressed: [new ThrowableInfo(), null, new ThrowableInfo()])

		def nullStack1 = new ThrowableInfo(stackTrace: [null])
		def nullStack2 = new ThrowableInfo(stackTrace: [new ExtendedStackTraceElement(), null, new ExtendedStackTraceElement()])

		[recursiveCause, recursiveSuppressed, nullSuppressed1, nullSuppressed2, nullStack1, nullStack2]
	}

	def compare(ThrowableInfo inputValue, ThrowableInfo other) {
		assert inputValue == other
		if (inputValue) {
			assert !(inputValue.is(other))

			assert inputValue.name == other.name
			assert inputValue.message == other.message

			if (inputValue.stackTrace) {
				assert other.stackTrace
				assert inputValue.stackTrace.length == other.stackTrace.length
				assert !(inputValue.stackTrace.is(other.stackTrace))

				for (int i = 0; i < inputValue.stackTrace.length; i++) {
					def inputStackTraceElement = inputValue.stackTrace[i]
					def otherStackTraceElement = other.stackTrace[i]
					assert inputStackTraceElement == otherStackTraceElement
					if (inputStackTraceElement) {
						assert !(inputStackTraceElement.is(otherStackTraceElement))
					}
				}
			} else {
				assert !other.stackTrace
			}

			assert inputValue.omittedElements == other.omittedElements

			if (inputValue.suppressed) {
				assert other.suppressed
				assert inputValue.suppressed.length == other.suppressed.length
				assert !(inputValue.suppressed.is(other.suppressed))

				for (int i = 0; i < inputValue.suppressed.length; i++) {
					def inputSuppressed = inputValue.suppressed[i]
					def otherSuppressed = other.suppressed[i]
					assert inputSuppressed == otherSuppressed
					if (inputSuppressed) {
						assert !(inputSuppressed.is(otherSuppressed))
					}
				}
			} else {
				assert !other.suppressed
			}

			assert inputValue.cause == other.cause
			if (inputValue.cause) {
				assert !(inputValue.cause.is(other.cause))
			}
		}
		return true
	}

	@Unroll
	def "#instance does not equal #other."() {
		expect:
		!instance.equals(other)
		!other.equals(instance)

		where:
		instance                              | other
		new ThrowableInfo()                   | new ThrowableInfo(name: 'b')
		new ThrowableInfo(name: 'a')          | new ThrowableInfo(name: 'b')
		new ThrowableInfo()                   | new ThrowableInfo(message: 'b')
		new ThrowableInfo(message: 'a')       | new ThrowableInfo(message: 'b')
		new ThrowableInfo()                   | new ThrowableInfo(cause: new ThrowableInfo())
		new ThrowableInfo()                   | new ThrowableInfo(suppressed: [])
		new ThrowableInfo(suppressed: [])     | new ThrowableInfo(suppressed: [null])
		new ThrowableInfo(suppressed: [null]) | new ThrowableInfo(suppressed: [new ThrowableInfo()])
	}

	def "equals behaves as expected."() {
		setup:
		def instance = new ThrowableInfo()
		def other = new ThrowableInfo(name: 'foo')

		expect:
		instance.equals(instance)
		!instance.equals(null)
		!instance.equals(new Object())
		!instance.equals(other)
		!other.equals(instance)
	}

	def "equals behaves as expected with internal identity equality."() {
		setup:
		def cause = new ThrowableInfo()
		def instance = new ThrowableInfo(cause: cause)
		def other = new ThrowableInfo(cause: cause)

		expect:
		instance.equals(instance)
		instance.equals(other)
		other.equals(instance)
	}

	def "toString() special cases"() {
		expect:
		new ThrowableInfo(name: 'java.lang.RuntimeException').toString() == 'java.lang.RuntimeException\n'
		new ThrowableInfo(name: 'java.lang.RuntimeException', message: 'java.lang.RuntimeException').toString() == 'java.lang.RuntimeException\n'
		new ThrowableInfo(name: 'java.lang.RuntimeException', message: 'foo').toString() == 'java.lang.RuntimeException: foo\n'
	}
}
