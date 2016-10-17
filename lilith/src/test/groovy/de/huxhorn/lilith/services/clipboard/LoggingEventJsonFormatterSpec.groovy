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

import de.huxhorn.lilith.data.EventWrapperCorpus

class LoggingEventJsonFormatterSpec extends AbstractClipboardFormatterSpec {

	private static final String NEWLINE = System.properties['line.separator']

	@Override
	LoggingEventJsonFormatter createInstance() {
		return new LoggingEventJsonFormatter()
	}

	def Set<Integer> expectedIndices() {
		EventWrapperCorpus.matchAnyLoggingEventSet()
	}

	Set<Integer> excludedIndices() {
		[
				// Jackson can't cope with recursive structures, which is ok.
				// isCompatible(Object) can't easily detect it either.
				88, 93, 94,
				// https://github.com/FasterXML/jackson-databind/issues/1411
				99, 121
		]
	}

	def List<String> expectedResults() {
		[
				'{ }',

				'{ }',

				'{' + NEWLINE +
						'  "level" : "TRACE"' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "level" : "DEBUG"' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "level" : "INFO"' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "level" : "WARN"' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "level" : "ERROR"' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "logger" : "com.foo.Foo"' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "logger" : "com.foo.Bar"' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "message" : { }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "message" : { }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "message" : {' + NEWLINE +
						'    "messagePattern" : "a message."' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "message" : {' + NEWLINE +
						'    "messagePattern" : "another message."' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "message" : {' + NEWLINE +
						'    "messagePattern" : "a message with parameter {}.",' + NEWLINE +
						'    "arguments" : [ "paramValue" ]' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "message" : {' + NEWLINE +
						'    "messagePattern" : "a message with unresolved parameter {}."' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "message" : {' + NEWLINE +
						'    "messagePattern" : "a message with parameter {} and unresolved parameter {}.",' + NEWLINE +
						'    "arguments" : [ "paramValue" ]' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "message" : {' + NEWLINE +
						'    "messagePattern" : "{}",' + NEWLINE +
						'    "arguments" : [ "paramValue" ]' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "message" : {' + NEWLINE +
						'    "messagePattern" : "{}"' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "mdc" : {' + NEWLINE +
						'    "mdcKey" : "mdcValue"' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "throwable" : {' + NEWLINE +
						'    "name" : "java.lang.RuntimeException",' + NEWLINE +
						'    "omittedElements" : 0' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "throwable" : {' + NEWLINE +
						'    "name" : "java.lang.RuntimeException",' + NEWLINE +
						'    "omittedElements" : 0,' + NEWLINE +
						'    "cause" : {' + NEWLINE +
						'      "name" : "java.lang.NullPointerException",' + NEWLINE +
						'      "omittedElements" : 0' + NEWLINE +
						'    }' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "throwable" : {' + NEWLINE +
						'    "name" : "java.lang.RuntimeException",' + NEWLINE +
						'    "omittedElements" : 0,' + NEWLINE +
						'    "cause" : {' + NEWLINE +
						'      "name" : "java.lang.NullPointerException",' + NEWLINE +
						'      "omittedElements" : 0,' + NEWLINE +
						'      "cause" : {' + NEWLINE +
						'        "name" : "java.lang.FooException",' + NEWLINE +
						'        "omittedElements" : 0' + NEWLINE +
						'      }' + NEWLINE +
						'    }' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "throwable" : {' + NEWLINE +
						'    "name" : "java.lang.RuntimeException",' + NEWLINE +
						'    "omittedElements" : 0,' + NEWLINE +
						'    "suppressed" : [ {' + NEWLINE +
						'      "name" : "java.lang.NullPointerException",' + NEWLINE +
						'      "omittedElements" : 0' + NEWLINE +
						'    } ]' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "throwable" : {' + NEWLINE +
						'    "name" : "java.lang.RuntimeException",' + NEWLINE +
						'    "omittedElements" : 0,' + NEWLINE +
						'    "suppressed" : [ {' + NEWLINE +
						'      "name" : "java.lang.NullPointerException",' + NEWLINE +
						'      "omittedElements" : 0' + NEWLINE +
						'    }, {' + NEWLINE +
						'      "name" : "java.lang.FooException",' + NEWLINE +
						'      "omittedElements" : 0' + NEWLINE +
						'    } ]' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "throwable" : {' + NEWLINE +
						'    "name" : "java.lang.RuntimeException",' + NEWLINE +
						'    "omittedElements" : 0,' + NEWLINE +
						'    "suppressed" : [ {' + NEWLINE +
						'      "name" : "java.lang.NullPointerException",' + NEWLINE +
						'      "omittedElements" : 0' + NEWLINE +
						'    }, {' + NEWLINE +
						'      "name" : "java.lang.FooException",' + NEWLINE +
						'      "omittedElements" : 0' + NEWLINE +
						'    } ],' + NEWLINE +
						'    "cause" : {' + NEWLINE +
						'      "name" : "java.lang.BarException",' + NEWLINE +
						'      "omittedElements" : 0' + NEWLINE +
						'    }' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "marker" : {' + NEWLINE +
						'    "name" : "Foo-Marker",' + NEWLINE +
						'    "references" : {' + NEWLINE +
						'      "Bar-Marker" : {' + NEWLINE +
						'        "name" : "Bar-Marker"' + NEWLINE +
						'      }' + NEWLINE +
						'    }' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "marker" : {' + NEWLINE +
						'    "name" : "Bar-Marker"' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "ndc" : [ ]' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "ndc" : [ { } ]' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "ndc" : [ { } ]' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "ndc" : [ {' + NEWLINE +
						'    "messagePattern" : "a message."' + NEWLINE +
						'  } ]' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "ndc" : [ {' + NEWLINE +
						'    "messagePattern" : "another message."' + NEWLINE +
						'  } ]' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "ndc" : [ {' + NEWLINE +
						'    "messagePattern" : "a message with parameter {}.",' + NEWLINE +
						'    "arguments" : [ "paramValue" ]' + NEWLINE +
						'  } ]' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "ndc" : [ {' + NEWLINE +
						'    "messagePattern" : "a message with unresolved parameter {}."' + NEWLINE +
						'  } ]' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "ndc" : [ {' + NEWLINE +
						'    "messagePattern" : "a message with parameter {} and unresolved parameter {}.",' + NEWLINE +
						'    "arguments" : [ "paramValue" ]' + NEWLINE +
						'  } ]' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "ndc" : [ {' + NEWLINE +
						'    "messagePattern" : "{}",' + NEWLINE +
						'    "arguments" : [ "paramValue" ]' + NEWLINE +
						'  } ]' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "ndc" : [ {' + NEWLINE +
						'    "messagePattern" : "{}"' + NEWLINE +
						'  } ]' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "callStack" : [ ]' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "callStack" : [ {' + NEWLINE +
						'    "className" : "de.huxhorn.lilith.debug.DebugDialog$LogAllAction",' + NEWLINE +
						'    "methodName" : "actionPerformed",' + NEWLINE +
						'    "fileName" : "DebugDialog.java",' + NEWLINE +
						'    "lineNumber" : 358,' + NEWLINE +
						'    "codeLocation" : "de.huxhorn.lilith-8.1.0-SNAPSHOT.jar",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.AbstractButton",' + NEWLINE +
						'    "methodName" : "fireActionPerformed",' + NEWLINE +
						'    "fileName" : "AbstractButton.java",' + NEWLINE +
						'    "lineNumber" : 2022,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.AbstractButton$Handler",' + NEWLINE +
						'    "methodName" : "actionPerformed",' + NEWLINE +
						'    "fileName" : "AbstractButton.java",' + NEWLINE +
						'    "lineNumber" : 2348,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'    "methodName" : "fireActionPerformed",' + NEWLINE +
						'    "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'    "lineNumber" : 402,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'    "methodName" : "setPressed",' + NEWLINE +
						'    "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'    "lineNumber" : 259,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  } ]' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "callStack" : [ {' + NEWLINE +
						'    "className" : "de.huxhorn.lilith.debug.DebugDialog$LogAllAction",' + NEWLINE +
						'    "methodName" : "actionPerformed",' + NEWLINE +
						'    "fileName" : "DebugDialog.java",' + NEWLINE +
						'    "lineNumber" : 358,' + NEWLINE +
						'    "codeLocation" : "de.huxhorn.lilith-8.1.0-SNAPSHOT.jar",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.AbstractButton",' + NEWLINE +
						'    "methodName" : "fireActionPerformed",' + NEWLINE +
						'    "fileName" : "AbstractButton.java",' + NEWLINE +
						'    "lineNumber" : 2022,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.AbstractButton$Handler",' + NEWLINE +
						'    "methodName" : "actionPerformed",' + NEWLINE +
						'    "fileName" : "AbstractButton.java",' + NEWLINE +
						'    "lineNumber" : 2348,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'    "methodName" : "fireActionPerformed",' + NEWLINE +
						'    "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'    "lineNumber" : 402,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'    "methodName" : "setPressed",' + NEWLINE +
						'    "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'    "lineNumber" : 259,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.plaf.basic.BasicButtonListener",' + NEWLINE +
						'    "methodName" : "mouseReleased",' + NEWLINE +
						'    "fileName" : "BasicButtonListener.java",' + NEWLINE +
						'    "lineNumber" : 252,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  } ]' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "callStack" : [ {' + NEWLINE +
						'    "className" : "de.huxhorn.lilith.debug.DebugDialog$LogAllAction",' + NEWLINE +
						'    "methodName" : "actionPerformed",' + NEWLINE +
						'    "fileName" : "DebugDialog.java",' + NEWLINE +
						'    "lineNumber" : 358,' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.AbstractButton",' + NEWLINE +
						'    "methodName" : "fireActionPerformed",' + NEWLINE +
						'    "fileName" : "AbstractButton.java",' + NEWLINE +
						'    "lineNumber" : 2022,' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.AbstractButton$Handler",' + NEWLINE +
						'    "methodName" : "actionPerformed",' + NEWLINE +
						'    "fileName" : "AbstractButton.java",' + NEWLINE +
						'    "lineNumber" : 2348,' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'    "methodName" : "fireActionPerformed",' + NEWLINE +
						'    "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'    "lineNumber" : 402,' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'    "methodName" : "setPressed",' + NEWLINE +
						'    "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'    "lineNumber" : 259,' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  } ]' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "callStack" : [ {' + NEWLINE +
						'    "className" : "de.huxhorn.lilith.debug.DebugDialog$LogAllAction",' + NEWLINE +
						'    "methodName" : "actionPerformed",' + NEWLINE +
						'    "fileName" : "DebugDialog.java",' + NEWLINE +
						'    "lineNumber" : 358,' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.AbstractButton",' + NEWLINE +
						'    "methodName" : "fireActionPerformed",' + NEWLINE +
						'    "fileName" : "AbstractButton.java",' + NEWLINE +
						'    "lineNumber" : 2022,' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.AbstractButton$Handler",' + NEWLINE +
						'    "methodName" : "actionPerformed",' + NEWLINE +
						'    "fileName" : "AbstractButton.java",' + NEWLINE +
						'    "lineNumber" : 2348,' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'    "methodName" : "fireActionPerformed",' + NEWLINE +
						'    "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'    "lineNumber" : 402,' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'    "methodName" : "setPressed",' + NEWLINE +
						'    "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'    "lineNumber" : 259,' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.plaf.basic.BasicButtonListener",' + NEWLINE +
						'    "methodName" : "mouseReleased",' + NEWLINE +
						'    "fileName" : "BasicButtonListener.java",' + NEWLINE +
						'    "lineNumber" : 252,' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  } ]' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "callStack" : [ {' + NEWLINE +
						'    "className" : "javax.swing.AbstractButton",' + NEWLINE +
						'    "methodName" : "fireActionPerformed",' + NEWLINE +
						'    "fileName" : "AbstractButton.java",' + NEWLINE +
						'    "lineNumber" : 2022,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.AbstractButton$Handler",' + NEWLINE +
						'    "methodName" : "actionPerformed",' + NEWLINE +
						'    "fileName" : "AbstractButton.java",' + NEWLINE +
						'    "lineNumber" : 2348,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'    "methodName" : "fireActionPerformed",' + NEWLINE +
						'    "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'    "lineNumber" : 402,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'    "methodName" : "setPressed",' + NEWLINE +
						'    "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'    "lineNumber" : 259,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  } ]' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "callStack" : [ {' + NEWLINE +
						'    "className" : "javax.swing.AbstractButton",' + NEWLINE +
						'    "methodName" : "fireActionPerformed",' + NEWLINE +
						'    "fileName" : "AbstractButton.java",' + NEWLINE +
						'    "lineNumber" : 2022,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.AbstractButton$Handler",' + NEWLINE +
						'    "methodName" : "actionPerformed",' + NEWLINE +
						'    "fileName" : "AbstractButton.java",' + NEWLINE +
						'    "lineNumber" : 2348,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'    "methodName" : "fireActionPerformed",' + NEWLINE +
						'    "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'    "lineNumber" : 402,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'    "methodName" : "setPressed",' + NEWLINE +
						'    "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'    "lineNumber" : 259,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.plaf.basic.BasicButtonListener",' + NEWLINE +
						'    "methodName" : "mouseReleased",' + NEWLINE +
						'    "fileName" : "BasicButtonListener.java",' + NEWLINE +
						'    "lineNumber" : 252,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  } ]' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "callStack" : [ {' + NEWLINE +
						'    "className" : "javax.swing.AbstractButton",' + NEWLINE +
						'    "methodName" : "fireActionPerformed",' + NEWLINE +
						'    "fileName" : "AbstractButton.java",' + NEWLINE +
						'    "lineNumber" : 2022,' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.AbstractButton$Handler",' + NEWLINE +
						'    "methodName" : "actionPerformed",' + NEWLINE +
						'    "fileName" : "AbstractButton.java",' + NEWLINE +
						'    "lineNumber" : 2348,' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'    "methodName" : "fireActionPerformed",' + NEWLINE +
						'    "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'    "lineNumber" : 402,' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'    "methodName" : "setPressed",' + NEWLINE +
						'    "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'    "lineNumber" : 259,' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  } ]' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "callStack" : [ {' + NEWLINE +
						'    "className" : "javax.swing.AbstractButton",' + NEWLINE +
						'    "methodName" : "fireActionPerformed",' + NEWLINE +
						'    "fileName" : "AbstractButton.java",' + NEWLINE +
						'    "lineNumber" : 2022,' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.AbstractButton$Handler",' + NEWLINE +
						'    "methodName" : "actionPerformed",' + NEWLINE +
						'    "fileName" : "AbstractButton.java",' + NEWLINE +
						'    "lineNumber" : 2348,' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'    "methodName" : "fireActionPerformed",' + NEWLINE +
						'    "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'    "lineNumber" : 402,' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'    "methodName" : "setPressed",' + NEWLINE +
						'    "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'    "lineNumber" : 259,' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.plaf.basic.BasicButtonListener",' + NEWLINE +
						'    "methodName" : "mouseReleased",' + NEWLINE +
						'    "fileName" : "BasicButtonListener.java",' + NEWLINE +
						'    "lineNumber" : 252,' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  } ]' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "callStack" : [ null, {' + NEWLINE +
						'    "className" : "javax.swing.AbstractButton",' + NEWLINE +
						'    "methodName" : "fireActionPerformed",' + NEWLINE +
						'    "fileName" : "AbstractButton.java",' + NEWLINE +
						'    "lineNumber" : 2022,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.AbstractButton$Handler",' + NEWLINE +
						'    "methodName" : "actionPerformed",' + NEWLINE +
						'    "fileName" : "AbstractButton.java",' + NEWLINE +
						'    "lineNumber" : 2348,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'    "methodName" : "fireActionPerformed",' + NEWLINE +
						'    "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'    "lineNumber" : 402,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'    "methodName" : "setPressed",' + NEWLINE +
						'    "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'    "lineNumber" : 259,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  } ]' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "callStack" : [ {' + NEWLINE +
						'    "className" : "de.huxhorn.lilith.debug.DebugDialog$LogAllAction",' + NEWLINE +
						'    "methodName" : "actionPerformed",' + NEWLINE +
						'    "fileName" : "DebugDialog.java",' + NEWLINE +
						'    "lineNumber" : 358,' + NEWLINE +
						'    "codeLocation" : "de.huxhorn.lilith-8.1.0-SNAPSHOT.jar",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, null, {' + NEWLINE +
						'    "className" : "javax.swing.AbstractButton$Handler",' + NEWLINE +
						'    "methodName" : "actionPerformed",' + NEWLINE +
						'    "fileName" : "AbstractButton.java",' + NEWLINE +
						'    "lineNumber" : 2348,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'    "methodName" : "fireActionPerformed",' + NEWLINE +
						'    "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'    "lineNumber" : 402,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'    "methodName" : "setPressed",' + NEWLINE +
						'    "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'    "lineNumber" : 259,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  } ]' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "mdc" : { }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "mdc" : {' + NEWLINE +
						'    "mdcKey" : "otherMdcValue"' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "mdc" : {' + NEWLINE +
						'    "mdcKey" : null' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "loggerContext" : {' + NEWLINE +
						'    "name" : "loggerContextName"' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "loggerContext" : {' + NEWLINE +
						'    "properties" : { }' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "loggerContext" : {' + NEWLINE +
						'    "properties" : {' + NEWLINE +
						'      "loggerContextKey" : "loggerContextValue"' + NEWLINE +
						'    }' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "threadInfo" : { }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "threadInfo" : {' + NEWLINE +
						'    "name" : "threadName"' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "threadInfo" : {' + NEWLINE +
						'    "id" : 11337' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "threadInfo" : {' + NEWLINE +
						'    "groupName" : "groupName"' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "threadInfo" : {' + NEWLINE +
						'    "groupId" : 31337' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "ndc" : [ {' + NEWLINE +
						'    "messagePattern" : "b0rked1"' + NEWLINE +
						'  }, null, {' + NEWLINE +
						'    "messagePattern" : "b0rked3"' + NEWLINE +
						'  } ]' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "throwable" : {' + NEWLINE +
						'    "message" : "exception1",' + NEWLINE +
						'    "omittedElements" : 0' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "throwable" : {' + NEWLINE +
						'    "omittedElements" : 0,' + NEWLINE +
						'    "cause" : {' + NEWLINE +
						'      "message" : "exception2",' + NEWLINE +
						'      "omittedElements" : 0' + NEWLINE +
						'    }' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "throwable" : {' + NEWLINE +
						'    "omittedElements" : 0,' + NEWLINE +
						'    "suppressed" : [ {' + NEWLINE +
						'      "message" : "exception3",' + NEWLINE +
						'      "omittedElements" : 0' + NEWLINE +
						'    } ]' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "throwable" : {' + NEWLINE +
						'    "omittedElements" : 0,' + NEWLINE +
						'    "suppressed" : [ {' + NEWLINE +
						'      "message" : "exception4",' + NEWLINE +
						'      "omittedElements" : 0' + NEWLINE +
						'    }, null, {' + NEWLINE +
						'      "message" : "exception5",' + NEWLINE +
						'      "omittedElements" : 0' + NEWLINE +
						'    } ]' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "throwable" : {' + NEWLINE +
						'    "stackTrace" : [ null, {' + NEWLINE +
						'      "className" : "javax.swing.AbstractButton",' + NEWLINE +
						'      "methodName" : "fireActionPerformed",' + NEWLINE +
						'      "fileName" : "AbstractButton.java",' + NEWLINE +
						'      "lineNumber" : 2022,' + NEWLINE +
						'      "version" : "1.8.0_92",' + NEWLINE +
						'      "exact" : false' + NEWLINE +
						'    }, {' + NEWLINE +
						'      "className" : "javax.swing.AbstractButton$Handler",' + NEWLINE +
						'      "methodName" : "actionPerformed",' + NEWLINE +
						'      "fileName" : "AbstractButton.java",' + NEWLINE +
						'      "lineNumber" : 2348,' + NEWLINE +
						'      "version" : "1.8.0_92",' + NEWLINE +
						'      "exact" : false' + NEWLINE +
						'    }, {' + NEWLINE +
						'      "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'      "methodName" : "fireActionPerformed",' + NEWLINE +
						'      "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'      "lineNumber" : 402,' + NEWLINE +
						'      "version" : "1.8.0_92",' + NEWLINE +
						'      "exact" : false' + NEWLINE +
						'    }, {' + NEWLINE +
						'      "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'      "methodName" : "setPressed",' + NEWLINE +
						'      "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'      "lineNumber" : 259,' + NEWLINE +
						'      "version" : "1.8.0_92",' + NEWLINE +
						'      "exact" : false' + NEWLINE +
						'    } ],' + NEWLINE +
						'    "omittedElements" : 0' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "throwable" : {' + NEWLINE +
						'    "omittedElements" : 0,' + NEWLINE +
						'    "cause" : {' + NEWLINE +
						'      "stackTrace" : [ {' + NEWLINE +
						'        "className" : "javax.swing.AbstractButton",' + NEWLINE +
						'        "methodName" : "fireActionPerformed",' + NEWLINE +
						'        "fileName" : "AbstractButton.java",' + NEWLINE +
						'        "lineNumber" : 2022,' + NEWLINE +
						'        "version" : "1.8.0_92",' + NEWLINE +
						'        "exact" : false' + NEWLINE +
						'      }, null, {' + NEWLINE +
						'        "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'        "methodName" : "fireActionPerformed",' + NEWLINE +
						'        "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'        "lineNumber" : 402,' + NEWLINE +
						'        "version" : "1.8.0_92",' + NEWLINE +
						'        "exact" : false' + NEWLINE +
						'      }, {' + NEWLINE +
						'        "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'        "methodName" : "setPressed",' + NEWLINE +
						'        "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'        "lineNumber" : 259,' + NEWLINE +
						'        "version" : "1.8.0_92",' + NEWLINE +
						'        "exact" : false' + NEWLINE +
						'      } ],' + NEWLINE +
						'      "omittedElements" : 0' + NEWLINE +
						'    }' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "mdc" : { }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "mdc" : {' + NEWLINE +
						'    "nullMdcValueKey" : null' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "logger" : ""' + NEWLINE +
						'}',
				'{' + NEWLINE +
						'  "throwable" : {' + NEWLINE +
						'    "name" : "",' + NEWLINE +
						'    "omittedElements" : 0' + NEWLINE +
						'  }' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "callStack" : [ null, {' + NEWLINE +
						'    "className" : "de.huxhorn.lilith.debug.DebugDialog$LogAllAction",' + NEWLINE +
						'    "methodName" : "actionPerformed",' + NEWLINE +
						'    "fileName" : "DebugDialog.java",' + NEWLINE +
						'    "lineNumber" : 358,' + NEWLINE +
						'    "codeLocation" : "de.huxhorn.lilith-8.1.0-SNAPSHOT.jar",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.AbstractButton$Handler",' + NEWLINE +
						'    "methodName" : "actionPerformed",' + NEWLINE +
						'    "fileName" : "AbstractButton.java",' + NEWLINE +
						'    "lineNumber" : 2348,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'    "methodName" : "fireActionPerformed",' + NEWLINE +
						'    "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'    "lineNumber" : 402,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  }, {' + NEWLINE +
						'    "className" : "javax.swing.DefaultButtonModel",' + NEWLINE +
						'    "methodName" : "setPressed",' + NEWLINE +
						'    "fileName" : "DefaultButtonModel.java",' + NEWLINE +
						'    "lineNumber" : 259,' + NEWLINE +
						'    "version" : "1.8.0_92",' + NEWLINE +
						'    "exact" : false' + NEWLINE +
						'  } ]' + NEWLINE +
						'}',

				'{' + NEWLINE +
						'  "threadInfo" : {' + NEWLINE +
						'    "priority" : 7' + NEWLINE +
						'  }' + NEWLINE +
						'}',
		]
	}

	def 'exploding formatter does simply return null.'() {
		setup:
		def corpus = EventWrapperCorpus.createCorpus()
		def formatter = createInstance()
		def exploding = corpus[88]

		expect:
		formatter.toString(exploding) == null
	}
}
