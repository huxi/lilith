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

class IconsSpec extends Specification {

	def setupSpec() {
		System.setProperty("java.awt.headless", "true")
	}

	@Unroll
	'toolbar action #actionId has a toolbar icon.'() {
		expect:
		Icons.resolveToolbarIcon(actionId) != null

		where:
		actionId << [
				LilithActionId.TAIL,
				// TODO: LilithActionId.PAUSE,
				LilithActionId.CLEAR,
				LilithActionId.DISCONNECT,

				LilithActionId.FIND,
				LilithActionId.FIND_PREVIOUS,
				LilithActionId.FIND_NEXT,

				// TODO: LilithActionId.ATTACH,

				LilithActionId.PREFERENCES,

				LilithActionId.LOVE,
		]
	}

	@Unroll
	'action #actionId has a menu icon.'(LilithActionId actionId) {
		expect:
		Icons.resolveMenuIcon(actionId) != null

		where:
		actionId << LilithActionId.values()
	}

	def 'sanity check - frameIdForViewState mapping works and produces unique values.'() {
		setup:
		Set<LilithFrameId> resolved = new HashSet<>()

		when:
		LOGGING_VIEW_STATES.each {
			resolved.add(Icons.frameIdForViewState(it, true))
			resolved.add(Icons.frameIdForViewState(it, false))
		}

		then:
		resolved.size() == 2 * (LoggingViewState.values().length + 1)
	}

	def 'sanity-check - image counts available for each LilithFrameId value.'() {
		expect:
		FRAME_ID_IMAGE_COUNT.size() == LilithFrameId.values().length
	}

	@Unroll
	'resolveFrameIcon(#id) returns a frame icon.'(LilithFrameId id) {
		expect:
		Icons.resolveFrameIcon(id) != null

		where:
		id << LilithFrameId.values()
	}

	@Unroll
	'resolveFrameIconImages(#id) returns a list of expected size #expectedSize.'(LilithFrameId id, Integer expectedSize) {
		when:
		def images = Icons.resolveFrameIconImages(id)

		then: 'a list is returned'
		images != null

		and: 'it has the expected size'
		images.size() == expectedSize

		where:
		id << LilithFrameId.values()
		expectedSize = FRAME_ID_IMAGE_COUNT[id]
	}

	def 'resolveEmptyMenuIcon() returns an icon.'() {
		expect:
		Icons.resolveEmptyMenuIcon() != null
	}

	@Unroll
	'resolveFrameIcon(#viewState, false) returns an icon.'(LoggingViewState viewState) {
		when:
		def icon = Icons.resolveFrameIcon(viewState, false)

		then:
		icon != null

		where:
		viewState << LOGGING_VIEW_STATES
	}

	@Unroll
	'resolveFrameIcon(#viewState, true) returns an icon.'(LoggingViewState viewState) {
		when:
		def icon = Icons.resolveFrameIcon(viewState, true)

		then:
		icon != null

		where:
		viewState << LOGGING_VIEW_STATES
	}

	@Unroll
	'resolveFrameIconImages(#viewState, false) returns a non-empty list.'(LoggingViewState viewState) {
		when:
		def icons = Icons.resolveFrameIconImages(viewState, false)

		then:
		icons != null
		!icons.isEmpty()

		where:
		viewState << LOGGING_VIEW_STATES
	}

	@Unroll
	'resolveFrameIconImages(#viewState, true) returns a non-empty list.'(LoggingViewState viewState) {
		when:
		def icons = Icons.resolveFrameIconImages(viewState, true)

		then:
		icons != null
		!icons.isEmpty()

		where:
		viewState << LOGGING_VIEW_STATES
	}


	def 'resolveImageIcon(null) would throw expected exception.'() {
		when:
		Icons.resolveImageIcon(null)

		then:
		NullPointerException ex = thrown()
		ex.message == 'resourcePath must not be null!'
	}

	def 'resolveImageIcon("invalidResource") would throw expected exception.'() {
		when:
		Icons.resolveImageIcon("invalidResource")

		then:
		IllegalArgumentException ex = thrown()
		ex.message == 'Failed to create ImageIcon from resource \'invalidResource\'!'
	}

	private static final EnumMap<LilithFrameId, Integer> FRAME_ID_IMAGE_COUNT = new EnumMap<>(LilithFrameId.class)

	static {
		FRAME_ID_IMAGE_COUNT.put(LilithFrameId.HELP, 1)
		FRAME_ID_IMAGE_COUNT.put(LilithFrameId.MAIN, 1)
		FRAME_ID_IMAGE_COUNT.put(LilithFrameId.VIEW_STATE_GLOBAL, 1)
		FRAME_ID_IMAGE_COUNT.put(LilithFrameId.VIEW_STATE_GLOBAL_DISABLED, 1)
		FRAME_ID_IMAGE_COUNT.put(LilithFrameId.VIEW_STATE_ACTIVE, 1)
		FRAME_ID_IMAGE_COUNT.put(LilithFrameId.VIEW_STATE_ACTIVE_DISABLED, 1)
		FRAME_ID_IMAGE_COUNT.put(LilithFrameId.VIEW_STATE_INACTIVE, 1)
		FRAME_ID_IMAGE_COUNT.put(LilithFrameId.VIEW_STATE_INACTIVE_DISABLED, 1)
		FRAME_ID_IMAGE_COUNT.put(LilithFrameId.VIEW_STATE_UPDATING_FILE, 1)
		FRAME_ID_IMAGE_COUNT.put(LilithFrameId.VIEW_STATE_UPDATING_FILE_DISABLED, 1)
		FRAME_ID_IMAGE_COUNT.put(LilithFrameId.VIEW_STATE_STALE_FILE, 1)
		FRAME_ID_IMAGE_COUNT.put(LilithFrameId.VIEW_STATE_STALE_FILE_DISABLED, 1)
	}

	private static final List<LoggingViewState> LOGGING_VIEW_STATES = new ArrayList<>()
	static {
		LOGGING_VIEW_STATES.addAll(LoggingViewState.values())
		LOGGING_VIEW_STATES.add(null)
	}
}
