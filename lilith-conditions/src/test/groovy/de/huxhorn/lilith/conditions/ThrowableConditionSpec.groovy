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
package de.huxhorn.lilith.conditions

import de.huxhorn.lilith.data.logging.ThrowableInfo
import spock.lang.Specification
import spock.lang.Unroll;


public class ThrowableConditionSpec extends Specification
{
	@Unroll
	def 'collectThrowableNames(#throwableInfo) works as expected.'() {
		when:
		def result = ThrowableCondition.collectThrowableNames(throwableInfo)

		then:
		result == expectedResult

		where:
		expectedResult                                                                                                              | throwableInfo
		[] as Set                                                                                                                   | null
		['java.lang.RuntimeException'] as Set                                                                                       | new ThrowableInfo(name: 'java.lang.RuntimeException')
		['java.lang.RuntimeException', 'java.lang.NullPointerException'] as Set                                                     | new ThrowableInfo(name: 'java.lang.RuntimeException', cause: new ThrowableInfo(name: 'java.lang.NullPointerException'))
		['java.lang.RuntimeException', 'java.lang.NullPointerException', 'java.lang.FooException'] as Set                           | new ThrowableInfo(name: 'java.lang.RuntimeException', cause: new ThrowableInfo(name: 'java.lang.NullPointerException', cause: new ThrowableInfo(name: 'java.lang.FooException')))
		['java.lang.RuntimeException', 'java.lang.NullPointerException'] as Set                                                     | new ThrowableInfo(name: 'java.lang.RuntimeException', suppressed: [new ThrowableInfo(name: 'java.lang.NullPointerException')])
		['java.lang.RuntimeException', 'java.lang.NullPointerException', 'java.lang.FooException'] as Set                           | new ThrowableInfo(name: 'java.lang.RuntimeException', suppressed: [new ThrowableInfo(name: 'java.lang.NullPointerException'), new ThrowableInfo(name: 'java.lang.FooException')])
		['java.lang.RuntimeException', 'java.lang.NullPointerException', 'java.lang.FooException', 'java.lang.BarException'] as Set | new ThrowableInfo(name: 'java.lang.RuntimeException', cause: new ThrowableInfo(name: 'java.lang.BarException'), suppressed: [new ThrowableInfo(name: 'java.lang.NullPointerException'), new ThrowableInfo(name: 'java.lang.FooException')])
	}
}
