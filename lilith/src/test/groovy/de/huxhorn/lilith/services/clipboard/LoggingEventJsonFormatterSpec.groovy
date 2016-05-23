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

package de.huxhorn.lilith.services.clipboard

class LoggingEventJsonFormatterSpec extends AbstractClipboardFormatterSpec {

	@Override
	LoggingEventJsonFormatter createInstance() {
		return new LoggingEventJsonFormatter()
	}

	def Set<Integer> expectedIndices() {
		[
				6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
				31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 65, 66, 67,
				68, 69, 76, 78, 80, 82, 83, 84, 85, 86, 87, 89, 90, 91, 92, 95, 96, 97, 98, 114, 115, 116
		]
	}

	Set<Integer> excludedIndices() {
		// Jackson can't cope with recursive structures, which is ok.
		// isCompatible(Object) can't easily detect it either.
		[88, 93, 94, 99]
	}

	def List<String> expectedResults() {
		[
				'{ }',

				'{ }',

				'{\n' +
						'  "level" : "TRACE"\n' +
						'}',

				'{\n' +
						'  "level" : "DEBUG"\n' +
						'}',

				'{\n' +
						'  "level" : "INFO"\n' +
						'}',

				'{\n' +
						'  "level" : "WARN"\n' +
						'}',

				'{\n' +
						'  "level" : "ERROR"\n' +
						'}',

				'{\n' +
						'  "logger" : "com.foo.Foo"\n' +
						'}',

				'{\n' +
						'  "logger" : "com.foo.Bar"\n' +
						'}',

				'{\n' +
						'  "message" : { }\n' +
						'}',

				'{\n' +
						'  "message" : { }\n' +
						'}',

				'{\n' +
						'  "message" : {\n' +
						'    "messagePattern" : "a message."\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "message" : {\n' +
						'    "messagePattern" : "another message."\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "message" : {\n' +
						'    "messagePattern" : "a message with parameter {}.",\n' +
						'    "arguments" : [ "paramValue" ]\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "message" : {\n' +
						'    "messagePattern" : "a message with unresolved parameter {}."\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "message" : {\n' +
						'    "messagePattern" : "a message with parameter {} and unresolved parameter {}.",\n' +
						'    "arguments" : [ "paramValue" ]\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "message" : {\n' +
						'    "messagePattern" : "{}",\n' +
						'    "arguments" : [ "paramValue" ]\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "message" : {\n' +
						'    "messagePattern" : "{}"\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "mdc" : {\n' +
						'    "mdcKey" : "mdcValue"\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "throwable" : {\n' +
						'    "name" : "java.lang.RuntimeException",\n' +
						'    "omittedElements" : 0\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "throwable" : {\n' +
						'    "name" : "java.lang.RuntimeException",\n' +
						'    "omittedElements" : 0,\n' +
						'    "cause" : {\n' +
						'      "name" : "java.lang.NullPointerException",\n' +
						'      "omittedElements" : 0\n' +
						'    }\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "throwable" : {\n' +
						'    "name" : "java.lang.RuntimeException",\n' +
						'    "omittedElements" : 0,\n' +
						'    "cause" : {\n' +
						'      "name" : "java.lang.NullPointerException",\n' +
						'      "omittedElements" : 0,\n' +
						'      "cause" : {\n' +
						'        "name" : "java.lang.FooException",\n' +
						'        "omittedElements" : 0\n' +
						'      }\n' +
						'    }\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "throwable" : {\n' +
						'    "name" : "java.lang.RuntimeException",\n' +
						'    "omittedElements" : 0,\n' +
						'    "suppressed" : [ {\n' +
						'      "name" : "java.lang.NullPointerException",\n' +
						'      "omittedElements" : 0\n' +
						'    } ]\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "throwable" : {\n' +
						'    "name" : "java.lang.RuntimeException",\n' +
						'    "omittedElements" : 0,\n' +
						'    "suppressed" : [ {\n' +
						'      "name" : "java.lang.NullPointerException",\n' +
						'      "omittedElements" : 0\n' +
						'    }, {\n' +
						'      "name" : "java.lang.FooException",\n' +
						'      "omittedElements" : 0\n' +
						'    } ]\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "throwable" : {\n' +
						'    "name" : "java.lang.RuntimeException",\n' +
						'    "omittedElements" : 0,\n' +
						'    "suppressed" : [ {\n' +
						'      "name" : "java.lang.NullPointerException",\n' +
						'      "omittedElements" : 0\n' +
						'    }, {\n' +
						'      "name" : "java.lang.FooException",\n' +
						'      "omittedElements" : 0\n' +
						'    } ],\n' +
						'    "cause" : {\n' +
						'      "name" : "java.lang.BarException",\n' +
						'      "omittedElements" : 0\n' +
						'    }\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "marker" : {\n' +
						'    "name" : "Foo-Marker",\n' +
						'    "references" : {\n' +
						'      "Bar-Marker" : {\n' +
						'        "name" : "Bar-Marker"\n' +
						'      }\n' +
						'    }\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "marker" : {\n' +
						'    "name" : "Bar-Marker"\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "ndc" : [ ]\n' +
						'}',

				'{\n' +
						'  "ndc" : [ { } ]\n' +
						'}',

				'{\n' +
						'  "ndc" : [ { } ]\n' +
						'}',

				'{\n' +
						'  "ndc" : [ {\n' +
						'    "messagePattern" : "a message."\n' +
						'  } ]\n' +
						'}',

				'{\n' +
						'  "ndc" : [ {\n' +
						'    "messagePattern" : "another message."\n' +
						'  } ]\n' +
						'}',

				'{\n' +
						'  "ndc" : [ {\n' +
						'    "messagePattern" : "a message with parameter {}.",\n' +
						'    "arguments" : [ "paramValue" ]\n' +
						'  } ]\n' +
						'}',

				'{\n' +
						'  "ndc" : [ {\n' +
						'    "messagePattern" : "a message with unresolved parameter {}."\n' +
						'  } ]\n' +
						'}',

				'{\n' +
						'  "ndc" : [ {\n' +
						'    "messagePattern" : "a message with parameter {} and unresolved parameter {}.",\n' +
						'    "arguments" : [ "paramValue" ]\n' +
						'  } ]\n' +
						'}',

				'{\n' +
						'  "ndc" : [ {\n' +
						'    "messagePattern" : "{}",\n' +
						'    "arguments" : [ "paramValue" ]\n' +
						'  } ]\n' +
						'}',

				'{\n' +
						'  "ndc" : [ {\n' +
						'    "messagePattern" : "{}"\n' +
						'  } ]\n' +
						'}',

				'{\n' +
						'  "callStack" : [ ]\n' +
						'}',

				'{\n' +
						'  "callStack" : [ {\n' +
						'    "className" : "de.huxhorn.lilith.debug.DebugDialog$LogAllAction",\n' +
						'    "methodName" : "actionPerformed",\n' +
						'    "fileName" : "DebugDialog.java",\n' +
						'    "lineNumber" : 358,\n' +
						'    "codeLocation" : "de.huxhorn.lilith-8.1.0-SNAPSHOT.jar",\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.AbstractButton",\n' +
						'    "methodName" : "fireActionPerformed",\n' +
						'    "fileName" : "AbstractButton.java",\n' +
						'    "lineNumber" : 2022,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.AbstractButton$Handler",\n' +
						'    "methodName" : "actionPerformed",\n' +
						'    "fileName" : "AbstractButton.java",\n' +
						'    "lineNumber" : 2348,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.DefaultButtonModel",\n' +
						'    "methodName" : "fireActionPerformed",\n' +
						'    "fileName" : "DefaultButtonModel.java",\n' +
						'    "lineNumber" : 402,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.DefaultButtonModel",\n' +
						'    "methodName" : "setPressed",\n' +
						'    "fileName" : "DefaultButtonModel.java",\n' +
						'    "lineNumber" : 259,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  } ]\n' +
						'}',

				'{\n' +
						'  "callStack" : [ {\n' +
						'    "className" : "de.huxhorn.lilith.debug.DebugDialog$LogAllAction",\n' +
						'    "methodName" : "actionPerformed",\n' +
						'    "fileName" : "DebugDialog.java",\n' +
						'    "lineNumber" : 358,\n' +
						'    "codeLocation" : "de.huxhorn.lilith-8.1.0-SNAPSHOT.jar",\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.AbstractButton",\n' +
						'    "methodName" : "fireActionPerformed",\n' +
						'    "fileName" : "AbstractButton.java",\n' +
						'    "lineNumber" : 2022,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.AbstractButton$Handler",\n' +
						'    "methodName" : "actionPerformed",\n' +
						'    "fileName" : "AbstractButton.java",\n' +
						'    "lineNumber" : 2348,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.DefaultButtonModel",\n' +
						'    "methodName" : "fireActionPerformed",\n' +
						'    "fileName" : "DefaultButtonModel.java",\n' +
						'    "lineNumber" : 402,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.DefaultButtonModel",\n' +
						'    "methodName" : "setPressed",\n' +
						'    "fileName" : "DefaultButtonModel.java",\n' +
						'    "lineNumber" : 259,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.plaf.basic.BasicButtonListener",\n' +
						'    "methodName" : "mouseReleased",\n' +
						'    "fileName" : "BasicButtonListener.java",\n' +
						'    "lineNumber" : 252,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  } ]\n' +
						'}',

				'{\n' +
						'  "callStack" : [ {\n' +
						'    "className" : "de.huxhorn.lilith.debug.DebugDialog$LogAllAction",\n' +
						'    "methodName" : "actionPerformed",\n' +
						'    "fileName" : "DebugDialog.java",\n' +
						'    "lineNumber" : 358,\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.AbstractButton",\n' +
						'    "methodName" : "fireActionPerformed",\n' +
						'    "fileName" : "AbstractButton.java",\n' +
						'    "lineNumber" : 2022,\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.AbstractButton$Handler",\n' +
						'    "methodName" : "actionPerformed",\n' +
						'    "fileName" : "AbstractButton.java",\n' +
						'    "lineNumber" : 2348,\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.DefaultButtonModel",\n' +
						'    "methodName" : "fireActionPerformed",\n' +
						'    "fileName" : "DefaultButtonModel.java",\n' +
						'    "lineNumber" : 402,\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.DefaultButtonModel",\n' +
						'    "methodName" : "setPressed",\n' +
						'    "fileName" : "DefaultButtonModel.java",\n' +
						'    "lineNumber" : 259,\n' +
						'    "exact" : false\n' +
						'  } ]\n' +
						'}',

				'{\n' +
						'  "callStack" : [ {\n' +
						'    "className" : "de.huxhorn.lilith.debug.DebugDialog$LogAllAction",\n' +
						'    "methodName" : "actionPerformed",\n' +
						'    "fileName" : "DebugDialog.java",\n' +
						'    "lineNumber" : 358,\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.AbstractButton",\n' +
						'    "methodName" : "fireActionPerformed",\n' +
						'    "fileName" : "AbstractButton.java",\n' +
						'    "lineNumber" : 2022,\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.AbstractButton$Handler",\n' +
						'    "methodName" : "actionPerformed",\n' +
						'    "fileName" : "AbstractButton.java",\n' +
						'    "lineNumber" : 2348,\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.DefaultButtonModel",\n' +
						'    "methodName" : "fireActionPerformed",\n' +
						'    "fileName" : "DefaultButtonModel.java",\n' +
						'    "lineNumber" : 402,\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.DefaultButtonModel",\n' +
						'    "methodName" : "setPressed",\n' +
						'    "fileName" : "DefaultButtonModel.java",\n' +
						'    "lineNumber" : 259,\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.plaf.basic.BasicButtonListener",\n' +
						'    "methodName" : "mouseReleased",\n' +
						'    "fileName" : "BasicButtonListener.java",\n' +
						'    "lineNumber" : 252,\n' +
						'    "exact" : false\n' +
						'  } ]\n' +
						'}',

				'{\n' +
						'  "callStack" : [ {\n' +
						'    "className" : "javax.swing.AbstractButton",\n' +
						'    "methodName" : "fireActionPerformed",\n' +
						'    "fileName" : "AbstractButton.java",\n' +
						'    "lineNumber" : 2022,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.AbstractButton$Handler",\n' +
						'    "methodName" : "actionPerformed",\n' +
						'    "fileName" : "AbstractButton.java",\n' +
						'    "lineNumber" : 2348,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.DefaultButtonModel",\n' +
						'    "methodName" : "fireActionPerformed",\n' +
						'    "fileName" : "DefaultButtonModel.java",\n' +
						'    "lineNumber" : 402,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.DefaultButtonModel",\n' +
						'    "methodName" : "setPressed",\n' +
						'    "fileName" : "DefaultButtonModel.java",\n' +
						'    "lineNumber" : 259,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  } ]\n' +
						'}',

				'{\n' +
						'  "callStack" : [ {\n' +
						'    "className" : "javax.swing.AbstractButton",\n' +
						'    "methodName" : "fireActionPerformed",\n' +
						'    "fileName" : "AbstractButton.java",\n' +
						'    "lineNumber" : 2022,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.AbstractButton$Handler",\n' +
						'    "methodName" : "actionPerformed",\n' +
						'    "fileName" : "AbstractButton.java",\n' +
						'    "lineNumber" : 2348,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.DefaultButtonModel",\n' +
						'    "methodName" : "fireActionPerformed",\n' +
						'    "fileName" : "DefaultButtonModel.java",\n' +
						'    "lineNumber" : 402,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.DefaultButtonModel",\n' +
						'    "methodName" : "setPressed",\n' +
						'    "fileName" : "DefaultButtonModel.java",\n' +
						'    "lineNumber" : 259,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.plaf.basic.BasicButtonListener",\n' +
						'    "methodName" : "mouseReleased",\n' +
						'    "fileName" : "BasicButtonListener.java",\n' +
						'    "lineNumber" : 252,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  } ]\n' +
						'}',

				'{\n' +
						'  "callStack" : [ {\n' +
						'    "className" : "javax.swing.AbstractButton",\n' +
						'    "methodName" : "fireActionPerformed",\n' +
						'    "fileName" : "AbstractButton.java",\n' +
						'    "lineNumber" : 2022,\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.AbstractButton$Handler",\n' +
						'    "methodName" : "actionPerformed",\n' +
						'    "fileName" : "AbstractButton.java",\n' +
						'    "lineNumber" : 2348,\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.DefaultButtonModel",\n' +
						'    "methodName" : "fireActionPerformed",\n' +
						'    "fileName" : "DefaultButtonModel.java",\n' +
						'    "lineNumber" : 402,\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.DefaultButtonModel",\n' +
						'    "methodName" : "setPressed",\n' +
						'    "fileName" : "DefaultButtonModel.java",\n' +
						'    "lineNumber" : 259,\n' +
						'    "exact" : false\n' +
						'  } ]\n' +
						'}',

				'{\n' +
						'  "callStack" : [ {\n' +
						'    "className" : "javax.swing.AbstractButton",\n' +
						'    "methodName" : "fireActionPerformed",\n' +
						'    "fileName" : "AbstractButton.java",\n' +
						'    "lineNumber" : 2022,\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.AbstractButton$Handler",\n' +
						'    "methodName" : "actionPerformed",\n' +
						'    "fileName" : "AbstractButton.java",\n' +
						'    "lineNumber" : 2348,\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.DefaultButtonModel",\n' +
						'    "methodName" : "fireActionPerformed",\n' +
						'    "fileName" : "DefaultButtonModel.java",\n' +
						'    "lineNumber" : 402,\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.DefaultButtonModel",\n' +
						'    "methodName" : "setPressed",\n' +
						'    "fileName" : "DefaultButtonModel.java",\n' +
						'    "lineNumber" : 259,\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.plaf.basic.BasicButtonListener",\n' +
						'    "methodName" : "mouseReleased",\n' +
						'    "fileName" : "BasicButtonListener.java",\n' +
						'    "lineNumber" : 252,\n' +
						'    "exact" : false\n' +
						'  } ]\n' +
						'}',

				'{\n' +
						'  "callStack" : [ null, {\n' +
						'    "className" : "javax.swing.AbstractButton",\n' +
						'    "methodName" : "fireActionPerformed",\n' +
						'    "fileName" : "AbstractButton.java",\n' +
						'    "lineNumber" : 2022,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.AbstractButton$Handler",\n' +
						'    "methodName" : "actionPerformed",\n' +
						'    "fileName" : "AbstractButton.java",\n' +
						'    "lineNumber" : 2348,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.DefaultButtonModel",\n' +
						'    "methodName" : "fireActionPerformed",\n' +
						'    "fileName" : "DefaultButtonModel.java",\n' +
						'    "lineNumber" : 402,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.DefaultButtonModel",\n' +
						'    "methodName" : "setPressed",\n' +
						'    "fileName" : "DefaultButtonModel.java",\n' +
						'    "lineNumber" : 259,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  } ]\n' +
						'}',

				'{\n' +
						'  "callStack" : [ {\n' +
						'    "className" : "de.huxhorn.lilith.debug.DebugDialog$LogAllAction",\n' +
						'    "methodName" : "actionPerformed",\n' +
						'    "fileName" : "DebugDialog.java",\n' +
						'    "lineNumber" : 358,\n' +
						'    "codeLocation" : "de.huxhorn.lilith-8.1.0-SNAPSHOT.jar",\n' +
						'    "exact" : false\n' +
						'  }, null, {\n' +
						'    "className" : "javax.swing.AbstractButton$Handler",\n' +
						'    "methodName" : "actionPerformed",\n' +
						'    "fileName" : "AbstractButton.java",\n' +
						'    "lineNumber" : 2348,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.DefaultButtonModel",\n' +
						'    "methodName" : "fireActionPerformed",\n' +
						'    "fileName" : "DefaultButtonModel.java",\n' +
						'    "lineNumber" : 402,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.DefaultButtonModel",\n' +
						'    "methodName" : "setPressed",\n' +
						'    "fileName" : "DefaultButtonModel.java",\n' +
						'    "lineNumber" : 259,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  } ]\n' +
						'}',

				'{\n' +
						'  "mdc" : { }\n' +
						'}',

				'{\n' +
						'  "mdc" : {\n' +
						'    "mdcKey" : "otherMdcValue"\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "mdc" : {\n' +
						'    "mdcKey" : null\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "loggerContext" : {\n' +
						'    "name" : "loggerContextName"\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "loggerContext" : {\n' +
						'    "properties" : { }\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "loggerContext" : {\n' +
						'    "properties" : {\n' +
						'      "loggerContextKey" : "loggerContextValue"\n' +
						'    }\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "threadInfo" : { }\n' +
						'}',

				'{\n' +
						'  "threadInfo" : {\n' +
						'    "name" : "threadName"\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "threadInfo" : {\n' +
						'    "id" : 11337\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "threadInfo" : {\n' +
						'    "groupName" : "groupName"\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "threadInfo" : {\n' +
						'    "groupId" : 31337\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "ndc" : [ {\n' +
						'    "messagePattern" : "b0rked1"\n' +
						'  }, null, {\n' +
						'    "messagePattern" : "b0rked3"\n' +
						'  } ]\n' +
						'}',

				'{\n' +
						'  "throwable" : {\n' +
						'    "message" : "exception1",\n' +
						'    "omittedElements" : 0\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "throwable" : {\n' +
						'    "omittedElements" : 0,\n' +
						'    "cause" : {\n' +
						'      "message" : "exception2",\n' +
						'      "omittedElements" : 0\n' +
						'    }\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "throwable" : {\n' +
						'    "omittedElements" : 0,\n' +
						'    "suppressed" : [ {\n' +
						'      "message" : "exception3",\n' +
						'      "omittedElements" : 0\n' +
						'    } ]\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "throwable" : {\n' +
						'    "omittedElements" : 0,\n' +
						'    "suppressed" : [ {\n' +
						'      "message" : "exception4",\n' +
						'      "omittedElements" : 0\n' +
						'    }, null, {\n' +
						'      "message" : "exception5",\n' +
						'      "omittedElements" : 0\n' +
						'    } ]\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "throwable" : {\n' +
						'    "stackTrace" : [ null, {\n' +
						'      "className" : "javax.swing.AbstractButton",\n' +
						'      "methodName" : "fireActionPerformed",\n' +
						'      "fileName" : "AbstractButton.java",\n' +
						'      "lineNumber" : 2022,\n' +
						'      "version" : "1.8.0_92",\n' +
						'      "exact" : false\n' +
						'    }, {\n' +
						'      "className" : "javax.swing.AbstractButton$Handler",\n' +
						'      "methodName" : "actionPerformed",\n' +
						'      "fileName" : "AbstractButton.java",\n' +
						'      "lineNumber" : 2348,\n' +
						'      "version" : "1.8.0_92",\n' +
						'      "exact" : false\n' +
						'    }, {\n' +
						'      "className" : "javax.swing.DefaultButtonModel",\n' +
						'      "methodName" : "fireActionPerformed",\n' +
						'      "fileName" : "DefaultButtonModel.java",\n' +
						'      "lineNumber" : 402,\n' +
						'      "version" : "1.8.0_92",\n' +
						'      "exact" : false\n' +
						'    }, {\n' +
						'      "className" : "javax.swing.DefaultButtonModel",\n' +
						'      "methodName" : "setPressed",\n' +
						'      "fileName" : "DefaultButtonModel.java",\n' +
						'      "lineNumber" : 259,\n' +
						'      "version" : "1.8.0_92",\n' +
						'      "exact" : false\n' +
						'    } ],\n' +
						'    "omittedElements" : 0\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "throwable" : {\n' +
						'    "omittedElements" : 0,\n' +
						'    "cause" : {\n' +
						'      "stackTrace" : [ {\n' +
						'        "className" : "javax.swing.AbstractButton",\n' +
						'        "methodName" : "fireActionPerformed",\n' +
						'        "fileName" : "AbstractButton.java",\n' +
						'        "lineNumber" : 2022,\n' +
						'        "version" : "1.8.0_92",\n' +
						'        "exact" : false\n' +
						'      }, null, {\n' +
						'        "className" : "javax.swing.DefaultButtonModel",\n' +
						'        "methodName" : "fireActionPerformed",\n' +
						'        "fileName" : "DefaultButtonModel.java",\n' +
						'        "lineNumber" : 402,\n' +
						'        "version" : "1.8.0_92",\n' +
						'        "exact" : false\n' +
						'      }, {\n' +
						'        "className" : "javax.swing.DefaultButtonModel",\n' +
						'        "methodName" : "setPressed",\n' +
						'        "fileName" : "DefaultButtonModel.java",\n' +
						'        "lineNumber" : 259,\n' +
						'        "version" : "1.8.0_92",\n' +
						'        "exact" : false\n' +
						'      } ],\n' +
						'      "omittedElements" : 0\n' +
						'    }\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "mdc" : { }\n' +
						'}',

				'{\n' +
						'  "mdc" : {\n' +
						'    "nullMdcValueKey" : null\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "logger" : ""\n' +
						'}',
				'{\n' +
						'  "throwable" : {\n' +
						'    "name" : "",\n' +
						'    "omittedElements" : 0\n' +
						'  }\n' +
						'}',

				'{\n' +
						'  "callStack" : [ null, {\n' +
						'    "className" : "de.huxhorn.lilith.debug.DebugDialog$LogAllAction",\n' +
						'    "methodName" : "actionPerformed",\n' +
						'    "fileName" : "DebugDialog.java",\n' +
						'    "lineNumber" : 358,\n' +
						'    "codeLocation" : "de.huxhorn.lilith-8.1.0-SNAPSHOT.jar",\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.AbstractButton$Handler",\n' +
						'    "methodName" : "actionPerformed",\n' +
						'    "fileName" : "AbstractButton.java",\n' +
						'    "lineNumber" : 2348,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.DefaultButtonModel",\n' +
						'    "methodName" : "fireActionPerformed",\n' +
						'    "fileName" : "DefaultButtonModel.java",\n' +
						'    "lineNumber" : 402,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  }, {\n' +
						'    "className" : "javax.swing.DefaultButtonModel",\n' +
						'    "methodName" : "setPressed",\n' +
						'    "fileName" : "DefaultButtonModel.java",\n' +
						'    "lineNumber" : 259,\n' +
						'    "version" : "1.8.0_92",\n' +
						'    "exact" : false\n' +
						'  } ]\n' +
						'}',
		]
	}


}
