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

	@Override
	LoggingThrowableFormatter createInstance() {
		return new LoggingThrowableFormatter()
	}

	def Set<Integer> expectedIndices() {
		[25, 26, 27, 28, 29, 30, 89, 90, 91, 92, 93, 94, 95, 96]
	}

	def List<String> expectedResults() {
		[
				'java.lang.RuntimeException\n',

				'java.lang.RuntimeException\n' +
						'Caused by: java.lang.NullPointerException\n',

				'java.lang.RuntimeException\n' +
						'Caused by: java.lang.NullPointerException\n' +
						'Caused by: java.lang.FooException\n',

				'java.lang.RuntimeException\n' +
						'\tSuppressed: java.lang.NullPointerException\n',

				'java.lang.RuntimeException\n' +
						'\tSuppressed: java.lang.NullPointerException\n' +
						'\tSuppressed: java.lang.FooException\n',

				'java.lang.RuntimeException\n' +
						'\tSuppressed: java.lang.NullPointerException\n' +
						'\tSuppressed: java.lang.FooException\n' +
						'Caused by: java.lang.BarException\n',

				'exception1\n',

				'null\n' +
						'Caused by: exception2\n',

				'null\n' +
						'\tSuppressed: exception3\n',

				'null\n' +
						'\tSuppressed: exception4\n' +
						'\tSuppressed: exception5\n',

				'recursiveCause\n' +
						'Caused by: recursiveCause[CIRCULAR REFERENCE]\n',

				'recursiveSuppressed\n' +
						'\tSuppressed: recursiveSuppressed[CIRCULAR REFERENCE]\n',

				'null\n' +
						'\tat javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259) ~[na:1.8.0_92]\n',

				'null\n' +
						'Caused by: null\n' +
						'\tat javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259) ~[na:1.8.0_92]\n'
		]
	}
}
