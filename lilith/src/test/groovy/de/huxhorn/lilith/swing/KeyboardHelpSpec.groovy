/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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

package de.huxhorn.lilith.swing

import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringEscapeUtils
import spock.lang.Specification

import java.nio.charset.StandardCharsets

class KeyboardHelpSpec extends Specification {


	def 'keyboard.xhtml contains up-to-date information.'() {
		setup:
		def currentKeyboardHelpText = loadKeyboardHelp()

		when: 'creating keyboard help text'
		StringBuilder builder = new StringBuilder()
		appendNotations(builder)
		appendCategories(builder)
		def keyboardHelp = builder.toString()

		and: 'printing the help text as a copy-paste source'
		println keyboardHelp

		then: 'this should all actually work'
		noExceptionThrown()

		and: 'keyboard help should be up-to-date.'
		removeSomeWhitespace(currentKeyboardHelpText).contains(removeSomeWhitespace(keyboardHelp))
	}

	private static String loadKeyboardHelp() {
		IOUtils.toString(KeyboardHelpSpec.class.getResourceAsStream('/help/keyboard.xhtml'), StandardCharsets.UTF_8)
	}

	private static String removeSomeWhitespace(String input) {
		input.replaceAll(~/(?m)>\s+</, '><').trim()
	}

	private static void appendNotations(StringBuilder builder) {

		builder.append('\n<h2>Notation</h2>\n')
		builder.append('<dl>\n')
		for (def entry : notation()) {
			builder.append('\t<dt>').append(toXHtml(entry.text)).append('</dt>\n')
			builder.append('\t<dd>').append(toXHtml(entry.description)).append('</dd>\n')
		}
		builder.append('</dl>\n')
	}

	private static void appendCategories(StringBuilder builder) {
		def categories = allCategories()
		categories.each {
			appendCategory(builder, it)
		}
	}

	private static void appendCategory(StringBuilder builder, Category category) {
		def filteredActions = filterActionsWithKeystroke(category.entries)
		if (filteredActions.empty) {
			return
		}

		builder.append('\n<h2>').append(toXHtml(category.name)).append('</h2>\n')
		builder.append('<dl>\n')
		for (def entry : filteredActions) {
			def keyStrokeString = LilithKeyStrokes.getUnprocessedKeyStrokeString(entry.name())
			keyStrokeString = processKeystrokeString(keyStrokeString)
			if (ADDITIONAL_KEYSTROKE_INFO[entry]) {
				keyStrokeString = keyStrokeString + ' ' + ADDITIONAL_KEYSTROKE_INFO[entry]
			}

			def descriptionString = entry.text
			if (entry.description) {
				descriptionString = descriptionString + '\n' + entry.description
			}
			if (ADDITIONAL_INFO[entry]) {
				descriptionString = descriptionString + '\n' + ADDITIONAL_INFO[entry]
			}
			builder.append('\t<dt>').append(toXHtml(keyStrokeString)).append('</dt>\n')
			builder.append('\t<dd>').append(toXHtml(descriptionString)).append('</dd>\n')
		}
		builder.append('</dl>\n')
	}

	private static String processKeystrokeString(final String keystrokeString) {
		def result = keystrokeString
		REPLACEMENTS.each { key, value ->
			result = result.replace(key, value)
		}
		if (result.contains('++')) {
			throw new IllegalArgumentException('KeystrokeString \'' + keystrokeString + '\' contained multiple consecutive spaces.')
		}
		return result
	}

	private static final String COMMAND_REPLACEMENT = '^'
	private static final String SHIFT_REPLACEMENT = '\u21E7'
	private static final String ALT_REPLACEMENT = 'alt'
	private static final String ENTER_REPLACEMENT = '\u21B5'

	private static final Map<String, String> REPLACEMENTS = [
			'command': COMMAND_REPLACEMENT,
			'shift'  : SHIFT_REPLACEMENT,
			'alt'    : ALT_REPLACEMENT,
			'enter'  : ENTER_REPLACEMENT,
			' '      : '+'
	]

	private static final Map<LilithActionId, String> ADDITIONAL_INFO = [
			(LilithActionId.EXIT) : 'No, I won\'t ask you for permission. I will only ask for permission if you make me do so in Preferences, though. :p',
	]

	private static final Map<LilithActionId, String> ADDITIONAL_KEYSTROKE_INFO = [
			(LilithActionId.ZOOM_IN): '(also: '+COMMAND_REPLACEMENT+'+[mouse wheel up])',
			(LilithActionId.ZOOM_OUT): '(also: '+COMMAND_REPLACEMENT+'+[mouse wheel down])',
	]

	private static String toXHtml(String input) {
		return StringEscapeUtils.escapeXml10(input).replace('\n', '<br/>')
	}

	private static List<LilithActionId> filterActionsWithKeystroke(List<LilithActionId> input) {
		List<LilithActionId> result = []
		input.each {
			if (LilithKeyStrokes.getKeyStroke(it)) {
				result << it
			}
		}
		return result

	}

	private static List<Category> allCategories() {
		[
				new Category(name: LilithActionId.FILE.text, entries: LilithActionIdSpec.fileMenuActions()),
				new Category(name: LilithActionId.EDIT.text, entries: LilithActionIdSpec.editMenuActions()),
				new Category(name: LilithActionId.SEARCH.text, entries: LilithActionIdSpec.searchMenuActions()),
				new Category(name: LilithActionId.VIEW.text, entries: LilithActionIdSpec.viewMenuActions()),
				new Category(name: LilithActionId.WINDOW.text, entries: LilithActionIdSpec.windowMenuActions()),
				new Category(name: LilithActionId.HELP.text, entries: LilithActionIdSpec.helpMenuActions()),
		]
	}

	private static List<Notation> notation() {
		[
				new Notation(text: COMMAND_REPLACEMENT, description: 'represents the system dependent command key, e.g. "Ctrl" on Windows and "cmd \u2318" on macOS.'),
				new Notation(text: ALT_REPLACEMENT, description: 'represents "Alt" ("alt \u2325" on Mac).'),
				new Notation(text: SHIFT_REPLACEMENT, description: 'represents "Shift".'),
				new Notation(text: ENTER_REPLACEMENT, description: 'represents "Enter" or "Return".'),
		]
	}

	static class Category {
		String name
		List<LilithActionId> entries
	}

	static class Notation {
		String text
		String description
	}
}
