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

class LoggingThrowableFormatterSpec extends AbstractClipboardFormatterSpec {

	private static final String NEWLINE = System.properties['line.separator']

	@Override
	LoggingThrowableFormatter createInstance() {
		return new LoggingThrowableFormatter()
	}

	def Set<Integer> expectedIndices() {
		[25, 26, 27, 28, 29, 30, 89, 90, 91, 92, 93, 94, 95, 96, 115]
	}

	def List<String> expectedResults() {
		[
				'java.lang.RuntimeException' + NEWLINE,

				'java.lang.RuntimeException' + NEWLINE +
						'Caused by: java.lang.NullPointerException' + NEWLINE,

				'java.lang.RuntimeException' + NEWLINE +
						'Caused by: java.lang.NullPointerException' + NEWLINE +
						'Caused by: java.lang.FooException' + NEWLINE,

				'java.lang.RuntimeException' + NEWLINE +
						'\tSuppressed: java.lang.NullPointerException' + NEWLINE,

				'java.lang.RuntimeException' + NEWLINE +
						'\tSuppressed: java.lang.NullPointerException' + NEWLINE +
						'\tSuppressed: java.lang.FooException' + NEWLINE,

				'java.lang.RuntimeException' + NEWLINE +
						'\tSuppressed: java.lang.NullPointerException' + NEWLINE +
						'\tSuppressed: java.lang.FooException' + NEWLINE +
						'Caused by: java.lang.BarException' + NEWLINE,

				'exception1' + NEWLINE,

				'null' + NEWLINE +
						'Caused by: exception2' + NEWLINE,

				'null' + NEWLINE +
						'\tSuppressed: exception3' + NEWLINE,

				'null' + NEWLINE +
						'\tSuppressed: exception4' + NEWLINE +
						'\tSuppressed: exception5' + NEWLINE,

				'recursiveCause' + NEWLINE +
						'Caused by: recursiveCause[CIRCULAR REFERENCE]' + NEWLINE,

				'recursiveSuppressed' + NEWLINE +
						'\tSuppressed: recursiveSuppressed[CIRCULAR REFERENCE]' + NEWLINE,

				'null' + NEWLINE +
						'\tat javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022) ~[na:1.8.0_92]' + NEWLINE +
						'\tat javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348) ~[na:1.8.0_92]' + NEWLINE +
						'\tat javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402) ~[na:1.8.0_92]' + NEWLINE +
						'\tat javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259) ~[na:1.8.0_92]' + NEWLINE,

				'null' + NEWLINE +
						'Caused by: null' + NEWLINE +
						'\tat javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022) ~[na:1.8.0_92]' + NEWLINE +
						'\tat javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402) ~[na:1.8.0_92]' + NEWLINE +
						'\tat javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259) ~[na:1.8.0_92]' + NEWLINE,

				NEWLINE
		]
	}

	boolean expectedAcceleratorAvailability() {
		true
	}
}
