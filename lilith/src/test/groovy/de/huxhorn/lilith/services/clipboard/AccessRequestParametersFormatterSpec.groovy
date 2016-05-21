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

class AccessRequestParametersFormatterSpec extends AbstractClipboardFormatterSpec {

	@Override
	AccessRequestParametersFormatter createInstance() {
		return new AccessRequestParametersFormatter()
	}

	def Set<Integer> expectedIndices() {
		[109, 110, 111, 112, 113]
	}

	def List<String> expectedResults() {
		[
				'[\'nullRequestParameterValueKey\':null]',
				'[\'requestParameterKey\':[]]',
				'[\'requestParameterKey\':[\'requestParameterValue1\', \'requestParameterValue2\']]',
				'[\'requestParameterKey\':[\'requestParameterValue1\', null, \'requestParameterValue3\']]',
				'[null:[\'nullRequestHeaderKeyValue\']]',
		]
	}
}
