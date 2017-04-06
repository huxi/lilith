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

import spock.lang.Specification
import spock.lang.Unroll

class LilithActionIdSpec extends Specification {
	@Unroll
	'#actionId has a text'(LilithActionId actionId) {
		expect:
		actionId.text != null

		where:
		actionId << LilithActionId.values()
	}

	@Unroll
	'#actionId has a mnemonic'(LilithActionId actionId) {
		expect:
		actionId.mnemonic != null

		where:
		actionId << LilithActionId.values()
	}

	@Unroll
	'mnemonic of #actionId is contained in name'(LilithActionId actionId) {

		when:
		def text = actionId.text
		Integer mnemonic = actionId.mnemonic

		String mnemonicChar = '' + (char) (mnemonic.intValue())


		then:
		text.toLowerCase(Locale.US).contains(mnemonicChar)

		where:
		actionId << LilithActionId.values()
	}

	def 'all actions are checked'() {
		setup:
		def combinedActions = combinedActions()
		Set<LilithActionId> allActions = new HashSet<>(Arrays.asList(LilithActionId.values()))

		when:
		allActions.removeAll(combinedActions)

		then:
		allActions.isEmpty()
	}

	@Unroll
	'text of #actionId does not equal description'(LilithActionId actionId) {
		expect:
		actionId.text == null || actionId.text != actionId.description

		where:
		actionId << LilithActionId.values()
	}

	@Unroll
	'#actionGroup has distinct mnemonics.'() {
		setup:
		Map<Integer, LilithActionId> mapping = new HashMap<>()
		Set<LilithActionId> collisions = new HashSet<>()

		when:
		actionGroup.each {
			Integer mnemonic = it.mnemonic
			if(mnemonic) {
				def previous = mapping.put(mnemonic, it)
				if(previous) {
					collisions.add(previous)
					collisions.add(it)
				}
			}
		}

		then:
		collisions.isEmpty()

		where:
		actionGroup << combinedActionGroups()
	}

	static List<LilithActionId> mainMenuActions() {
		[
				LilithActionId.FILE,
				LilithActionId.EDIT,
				LilithActionId.SEARCH,
				LilithActionId.VIEW,
				LilithActionId.WINDOW,
				LilithActionId.HELP,
		]
	}

	static List<LilithActionId> fileMenuActions() {
		[
		        LilithActionId.OPEN,
				LilithActionId.RECENT_FILES,
				LilithActionId.OPEN_INACTIVE,
				LilithActionId.CLEAN_ALL_INACTIVE_LOGS,
				LilithActionId.IMPORT,
				LilithActionId.EXPORT,
				LilithActionId.PREFERENCES,
				LilithActionId.EXIT,
		]
	}

	static List<LilithActionId> editMenuActions() {
		List<LilithActionId> result = editMenuBaseActions()
		result.addAll(editMenuLoggingBaseActions())
		result.addAll(editMenuAccessBaseActions())
		return result
	}

	static List<LilithActionId> recentFilesMenuActions() {
		[
				LilithActionId.CLEAR_RECENT_FILES,
		]
	}

	static List<LilithActionId> editMenuBaseActions() {
		[
				LilithActionId.COPY_SELECTION,
				LilithActionId.COPY_HTML,

				LilithActionId.CUSTOM_COPY,

				LilithActionId.GO_TO_SOURCE,
				LilithActionId.PASTE_STACK_TRACE_ELEMENT,
		]
	}

	static List<LilithActionId> editMenuLoggingBaseActions() {
		[
				LilithActionId.COPY_JSON,
				LilithActionId.COPY_XML,
				LilithActionId.COPY_MESSAGE,
				LilithActionId.COPY_MESSAGE_PATTERN,
				LilithActionId.COPY_LOGGER_NAME,
				LilithActionId.COPY_THROWABLE,
				LilithActionId.COPY_THROWABLE_NAME,
				LilithActionId.COPY_CALL_LOCATION,
				LilithActionId.COPY_CALL_STACK,
				LilithActionId.COPY_THREAD_NAME,
				LilithActionId.COPY_THREAD_GROUP_NAME,
				LilithActionId.COPY_MARKER,
				LilithActionId.COPY_MDC,
				LilithActionId.COPY_NDC,
		]
	}

	static List<LilithActionId> editMenuAccessBaseActions() {
		[
				LilithActionId.COPY_REQUEST_URI,
				LilithActionId.COPY_REQUEST_URL,
				LilithActionId.COPY_REQUEST_HEADERS,
				LilithActionId.COPY_REQUEST_PARAMETERS,
				LilithActionId.COPY_RESPONSE_HEADERS,
		]
	}

	static List<LilithActionId> editMenuLoggingActions() {
		List<LilithActionId> result = editMenuBaseActions()
		result.addAll(editMenuLoggingBaseActions())
		return result
	}

	static List<LilithActionId> editMenuAccessActions() {
		List<LilithActionId> result = editMenuBaseActions()
		result.addAll(editMenuAccessBaseActions())
		return result
	}

	static List<LilithActionId> searchMenuActions() {
		[
				LilithActionId.FIND,
				LilithActionId.RESET_FIND,
				LilithActionId.FIND_PREVIOUS,
				LilithActionId.FIND_NEXT,
				LilithActionId.FIND_PREVIOUS_ACTIVE,
				LilithActionId.FIND_NEXT_ACTIVE,

				LilithActionId.SAVE_CONDITION,

				LilithActionId.FOCUS,
				LilithActionId.EXCLUDE,

				LilithActionId.SHOW_UNFILTERED_EVENT,
		]
	}

	static List<LilithActionId> viewMenuActions() {
		[
				LilithActionId.TAIL,
				LilithActionId.PAUSE,
				LilithActionId.CLEAR,
				LilithActionId.ATTACH,
				LilithActionId.DISCONNECT,
				LilithActionId.FOCUS_EVENTS,
				LilithActionId.FOCUS_MESSAGE,
				LilithActionId.EDIT_SOURCE_NAME,

				LilithActionId.ZOOM_IN,
				LilithActionId.ZOOM_OUT,
				LilithActionId.RESET_ZOOM,

				LilithActionId.LAYOUT,

				LilithActionId.NEXT_VIEW,
				LilithActionId.PREVIOUS_VIEW,

				LilithActionId.CLOSE_FILTER,
				LilithActionId.CLOSE_OTHER_FILTERS,
				LilithActionId.CLOSE_ALL_FILTERS,
		]
	}

	static List<LilithActionId> layoutMenuActions() {
		[
				LilithActionId.COLUMNS,

				LilithActionId.SAVE_LAYOUT,
				LilithActionId.RESET_LAYOUT,
		]
	}

	static List<LilithActionId> windowMenuActions() {
		[
				LilithActionId.TASK_MANAGER,

				LilithActionId.CLOSE_ALL,
				LilithActionId.CLOSE_ALL_OTHER,
				LilithActionId.MINIMIZE_ALL,
				LilithActionId.MINIMIZE_OTHER,
				LilithActionId.REMOVE_INACTIVE,

				LilithActionId.VIEW_LILITH_LOGS,
				LilithActionId.VIEW_GLOBAL_CLASSIC_LOGS,
				LilithActionId.VIEW_GLOBAL_ACCESS_LOGS,
		]
	}

	static List<LilithActionId> helpMenuActions() {
		[
				LilithActionId.HELP_TOPICS,
				LilithActionId.LOVE,
				LilithActionId.TIP_OF_THE_DAY,
				LilithActionId.CHECK_FOR_UPDATE,
				LilithActionId.TROUBLESHOOTING,

				LilithActionId.DEBUG,

				LilithActionId.ABOUT,
		]
	}

	static List<LilithActionId> miscActions() {
		[
				LilithActionId.REPLACE_FILTER,
		]
	}

	static List<List<LilithActionId>> combinedActionGroups() {
		def result = []

		result.add(mainMenuActions())
		result.add(fileMenuActions())
		result.add(recentFilesMenuActions())
		result.add(editMenuLoggingActions())
		result.add(editMenuAccessActions())
		result.add(searchMenuActions())
		result.add(viewMenuActions())
		result.add(layoutMenuActions())
		result.add(windowMenuActions())
		result.add(helpMenuActions())
		result.add(miscActions())

		return result
	}

	static Set<LilithActionId> combinedActions() {
		def result = []

		combinedActionGroups().each {
			result.addAll(it)
		}

		return result
	}
}
