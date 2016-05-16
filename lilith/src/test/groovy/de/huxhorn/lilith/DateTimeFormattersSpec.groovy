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

package de.huxhorn.lilith

import spock.lang.Specification
import spock.lang.Unroll

import java.time.Instant
import java.time.ZoneId

class DateTimeFormattersSpec extends Specification {

    private static TimeZone defaultTimeZone

    def setupSpec() {
        defaultTimeZone = TimeZone.getDefault()
        ZoneId zoneId = ZoneId.of('Europe/Berlin')
        TimeZone timeZone = TimeZone.getTimeZone(zoneId)
        TimeZone.setDefault(timeZone)
    }

    def cleanupSpec() {
        TimeZone.setDefault(defaultTimeZone)
    }

    @Unroll
    def 'DATETIME_IN_SYSTEM_ZONE_SPACE with #millis'(long millis, String expectedResult) {
        when:
        def result = DateTimeFormatters.DATETIME_IN_SYSTEM_ZONE_SPACE.format(Instant.ofEpochMilli(millis))

        then:
        result == expectedResult

        where:
        millis        | expectedResult
        0             | '1970-01-01 01:00:00.000'
        1449658372097 | '2015-12-09 11:52:52.097'
    }

    @Unroll
    def 'TIME_IN_SYSTEM_ZONE with #millis'(long millis, String expectedResult) {
        when:
        def result = DateTimeFormatters.TIME_IN_SYSTEM_ZONE.format(Instant.ofEpochMilli(millis))

        then:
        result == expectedResult

        where:
        millis        | expectedResult
        0             | '01:00:00.000'
        1449658372097 | '11:52:52.097'
    }

    @Unroll
    def 'COMPACT_DATETIME_IN_SYSTEM_ZONE_T with #millis'(long millis, String expectedResult) {
        when:
        def result = DateTimeFormatters.COMPACT_DATETIME_IN_SYSTEM_ZONE_T.format(Instant.ofEpochMilli(millis))

        then:
        result == expectedResult

        where:
        millis        | expectedResult
        0             | '19700101T010000000'
        1449658372097 | '20151209T115252097'
    }
}
