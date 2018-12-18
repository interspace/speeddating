package com.interspacehq.speeddating

import assertk.Assert
import assertk.assert
import assertk.assertions.isEqualTo
import org.junit.Before
import org.junit.Test
import java.text.DateFormat
import java.util.*

/**
 * @author dhleong
 */
class Rfc3339DateParserTest {

    private lateinit var parser: Rfc3339DateParser

    @Before fun setUp() {
        parser = Rfc3339DateParser()
    }

    @Test fun `No fractional seconds, GMT`() {
        assert(parser.parseToCalendar("2018-02-21T20:22:30Z")) {
            hasYear(2018)
            hasMonth(2)
            hasDate(21)

            hasHour(20)
            hasMinute(22)
            hasSeconds(30)
            hasMillis(0)

            hasTimeZoneOffset(0)
        }
    }

    @Test fun `Fractional seconds, GMT`() {
        assert(parser.parseToCalendar("2018-02-21T20:22:30.506Z")) {
            hasYear(2018)
            hasMonth(2)
            hasDate(21)

            hasHour(20)
            hasMinute(22)
            hasSeconds(30)
            hasMillis(506)

            hasTimeZoneOffset(0)
        }
    }

    @Test fun `GMT Timezone offset`() {
        assert(parser.parseToCalendar("2018-02-21T20:22:30.506+00:00")) {
            hasYear(2018)
            hasMonth(2)
            hasDate(21)

            hasHour(20)
            hasMinute(22)
            hasSeconds(30)
            hasMillis(506)

            hasTimeZoneOffset(0)
        }
    }

    @Test fun `Positive Timezone offset`() {
        assert(parser.parseToCalendar("2018-02-21T20:22:30.506+07:20")) {
            hasYear(2018)
            hasMonth(2)
            hasDate(21)

            hasHour(20)
            hasMinute(22)
            hasSeconds(30)
            hasMillis(506)

            hasTimeZoneOffset(7 * 3_600_000 + 20 * 60_000)
        }
    }

    @Test fun `Negative Timezone offset`() {
        assert(parser.parseToCalendar("2018-02-21T20:22:30.506-07:20")) {
            hasYear(2018)
            hasMonth(2)
            hasDate(21)

            hasHour(20)
            hasMinute(22)
            hasSeconds(30)
            hasMillis(506)

            hasTimeZoneOffset(-7 * 3_600_000 - 20 * 60_000)
        }
    }
}

fun Assert<Calendar>.hasYear(expected: Int) = hasValue(Calendar.YEAR, "year", expected)
fun Assert<Calendar>.hasMonth(expected: Int) = hasValue(Calendar.MONTH, "month", expected - 1)
fun Assert<Calendar>.hasDate(expected: Int) = hasValue(Calendar.DATE, "date", expected)
fun Assert<Calendar>.hasHour(expected: Int) = hasValue(Calendar.HOUR_OF_DAY, "hour", expected)
fun Assert<Calendar>.hasMinute(expected: Int) = hasValue(Calendar.MINUTE, "minute", expected)
fun Assert<Calendar>.hasSeconds(expected: Int) = hasValue(Calendar.SECOND, "seconds", expected)
fun Assert<Calendar>.hasMillis(expected: Int) = hasValue(Calendar.MILLISECOND, "milliseconds", expected)
fun Assert<Calendar>.hasTimeZoneOffset(expected: Int) = hasValue(
    Calendar.ZONE_OFFSET, "timezone offset", expected
)

fun Assert<Calendar>.hasValue(field: Int, label: String, expectedValue: Int) {
    val str = DateFormat.getDateTimeInstance().format(actual.time)
    assert(actual.get(field), "[$str].$label").isEqualTo(expectedValue)
}

