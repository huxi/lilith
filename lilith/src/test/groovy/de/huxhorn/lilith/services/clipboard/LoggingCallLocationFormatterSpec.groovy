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

class LoggingCallLocationFormatterSpec extends AbstractClipboardFormatterSpec {

	@Override
	LoggingCallLocationFormatter createInstance() {
		return new LoggingCallLocationFormatter()
	}

	def Set<Integer> expectedIndices() {
		[44, 45, 46, 47, 48, 49, 50, 51, 66]
	}

	def List<String> expectedResults() {
		[
				'de.huxhorn.lilith.debug.DebugDialog$LogAllAction.actionPerformed(DebugDialog.java:358) ~[de.huxhorn.lilith-8.1.0-SNAPSHOT.jar:na]',

				'de.huxhorn.lilith.debug.DebugDialog$LogAllAction.actionPerformed(DebugDialog.java:358) ~[de.huxhorn.lilith-8.1.0-SNAPSHOT.jar:na]',

				'de.huxhorn.lilith.debug.DebugDialog$LogAllAction.actionPerformed(DebugDialog.java:358)',

				'de.huxhorn.lilith.debug.DebugDialog$LogAllAction.actionPerformed(DebugDialog.java:358)',

				'javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022) ~[na:1.8.0_92]',

				'javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022) ~[na:1.8.0_92]',

				'javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022)',

				'javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022)',

				'de.huxhorn.lilith.debug.DebugDialog$LogAllAction.actionPerformed(DebugDialog.java:358) ~[de.huxhorn.lilith-8.1.0-SNAPSHOT.jar:na]',
		]
	}

	boolean expectedAcceleratorAvailability() {
		true
	}
}
