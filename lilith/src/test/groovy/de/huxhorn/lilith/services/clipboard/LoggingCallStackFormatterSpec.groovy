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

class LoggingCallStackFormatterSpec extends AbstractClipboardFormatterSpec {

	@Override
	LoggingCallStackFormatter createInstance() {
		return new LoggingCallStackFormatter()
	}

	def Set<Integer> expectedIndices() {
		[44, 45, 46, 47, 48, 49, 50, 51, 65, 66, 116]
	}

	def List<String> expectedResults() {
		[
				'\tat de.huxhorn.lilith.debug.DebugDialog$LogAllAction.actionPerformed(DebugDialog.java:358) ~[de.huxhorn.lilith-8.1.0-SNAPSHOT.jar:na]\n' +
						'\tat javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259) ~[na:1.8.0_92]',

				'\tat de.huxhorn.lilith.debug.DebugDialog$LogAllAction.actionPerformed(DebugDialog.java:358) ~[de.huxhorn.lilith-8.1.0-SNAPSHOT.jar:na]\n' +
						'\tat javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.plaf.basic.BasicButtonListener.mouseReleased(BasicButtonListener.java:252) ~[na:1.8.0_92]',

				'\tat de.huxhorn.lilith.debug.DebugDialog$LogAllAction.actionPerformed(DebugDialog.java:358)\n' +
						'\tat javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022)\n' +
						'\tat javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348)\n' +
						'\tat javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402)\n' +
						'\tat javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259)',

				'\tat de.huxhorn.lilith.debug.DebugDialog$LogAllAction.actionPerformed(DebugDialog.java:358)\n' +
						'\tat javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022)\n' +
						'\tat javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348)\n' +
						'\tat javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402)\n' +
						'\tat javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259)\n' +
						'\tat javax.swing.plaf.basic.BasicButtonListener.mouseReleased(BasicButtonListener.java:252)',

				'\tat javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259) ~[na:1.8.0_92]',

				'\tat javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.plaf.basic.BasicButtonListener.mouseReleased(BasicButtonListener.java:252) ~[na:1.8.0_92]',

				'\tat javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022)\n' +
						'\tat javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348)\n' +
						'\tat javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402)\n' +
						'\tat javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259)',

				'\tat javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022)\n' +
						'\tat javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348)\n' +
						'\tat javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402)\n' +
						'\tat javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259)\n' +
						'\tat javax.swing.plaf.basic.BasicButtonListener.mouseReleased(BasicButtonListener.java:252)',

				'\tat null\n' +
						'\tat javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259) ~[na:1.8.0_92]',

				'\tat de.huxhorn.lilith.debug.DebugDialog$LogAllAction.actionPerformed(DebugDialog.java:358) ~[de.huxhorn.lilith-8.1.0-SNAPSHOT.jar:na]\n' +
						'\tat null\n' +
						'\tat javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259) ~[na:1.8.0_92]',

				'\tat null\n' +
						'\tat de.huxhorn.lilith.debug.DebugDialog$LogAllAction.actionPerformed(DebugDialog.java:358) ~[de.huxhorn.lilith-8.1.0-SNAPSHOT.jar:na]\n' +
						'\tat javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402) ~[na:1.8.0_92]\n' +
						'\tat javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259) ~[na:1.8.0_92]',
		]
	}

	boolean expectedAcceleratorAvailability() {
		true
	}
}
