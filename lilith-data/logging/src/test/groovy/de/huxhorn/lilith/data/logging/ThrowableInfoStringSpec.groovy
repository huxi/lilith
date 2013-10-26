package de.huxhorn.lilith.data.logging

import spock.lang.Specification

class ThrowableInfoStringSpec extends Specification {
    def complexString() {
        setup:
        ThrowableInfo info = new ThrowableInfo(
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

        )
        String expected = "java.lang.RuntimeException: Hi.\n" +
                "\tat de.huxhorn.lilith.sandbox.Log4jSandbox\$InnerClass.execute(Log4jSandbox.java:49)\n" +
                "\tat de.huxhorn.lilith.sandbox.Log4jSandbox.main(Log4jSandbox.java:86)\n" +
                "Caused by: java.lang.RuntimeException: Hi Cause.\n" +
                "\tat de.huxhorn.lilith.sandbox.Log4jSandbox\$InnerClass.foobar(Log4jSandbox.java:60)\n" +
                "\tat de.huxhorn.lilith.sandbox.Log4jSandbox\$InnerClass.execute(Log4jSandbox.java:45)\n" +
                "\t... 1 more\n" +
                "\tSuppressed: java.lang.RuntimeException\n" +
                "\t\tat de.huxhorn.lilith.sandbox.Log4jSandbox\$InnerClass.foobar(Log4jSandbox.java:61)\n" +
                "\t\t... 2 more\n" +
                "\tSuppressed: java.lang.RuntimeException: Single line\n" +
                "\t\tat de.huxhorn.lilith.sandbox.Log4jSandbox\$InnerClass.foobar(Log4jSandbox.java:62)\n" +
                "\t\t... 2 more\n" +
                "\tSuppressed: java.lang.RuntimeException: With cause and suppressed\n" +
                "\t\tat de.huxhorn.lilith.sandbox.Log4jSandbox\$InnerClass.foobar(Log4jSandbox.java:63)\n" +
                "\t\t... 2 more\n" +
                "\t\tSuppressed: java.lang.RuntimeException: Inner Suppressed\n" +
                "\t\t\tat de.huxhorn.lilith.sandbox.Log4jSandbox\$InnerClass.foobar(Log4jSandbox.java:64)\n" +
                "\t\t\t... 2 more\n" +
                "\t\tSuppressed: java.lang.RuntimeException: Inner Suppressed with Cause\n" +
                "\t\t\tat de.huxhorn.lilith.sandbox.Log4jSandbox\$InnerClass.foobar(Log4jSandbox.java:65)\n" +
                "\t\t\t... 2 more\n" +
                "\t\tCaused by: java.lang.RuntimeException: Inner Cause\n" +
                "\t\t\t... 3 more\n" +
                "\tCaused by: java.lang.RuntimeException: Cause\n" +
                "\t\t... 3 more\n" +
                "\tSuppressed: java.lang.RuntimeException: Multi\n" +
                "line\n" +
                "\t\tat de.huxhorn.lilith.sandbox.Log4jSandbox\$InnerClass.foobar(Log4jSandbox.java:67)\n" +
                "\t\t... 2 more\n"

        expect:
        expected == ThrowableInfo.asString(info, true)
    }


}
