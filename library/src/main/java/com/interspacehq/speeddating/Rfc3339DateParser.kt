package com.interspacehq.speeddating

import java.text.ParseException
import java.util.*

/**
 * A special string-to-Date parser that specifically handles
 *  RFC3339-format dates. This implementation is threadsafe.
 *
 * @author dhleong
 */
class Rfc3339DateParser {

    fun parse(dateString: String): Date = parseToCalendar(dateString).time

    fun parseToCalendar(dateString: String): Calendar {
        val year = dateString.numberIn(0, 3)
        dateString.assertAt(4, isChar = '-')

        val month = dateString.numberIn(5, 6) - 1 // NOTE: month is 0-indexed
        dateString.assertAt(7, isChar = '-')

        val date = dateString.numberIn(8, 9)
        dateString.assertAt(10, isChar = 'T')

        val hours24 = dateString.numberIn(11, 12)
        dateString.assertAt(13, isChar = ':')

        val minutes = dateString.numberIn(14, 15)
        dateString.assertAt(16, isChar = ':')

        val seconds = dateString.numberIn(17, 18)

        var milliseconds = 0

        val tzIndex: Int = if (dateString[19] == '.') {
            var fractionalSecondsEnd = 20
            for (i in fractionalSecondsEnd..dateString.lastIndex) {
                fractionalSecondsEnd = i
                if (dateString[i] !in '0'..'9') {
                    break // found!
                }
            }

            milliseconds = (dateString.doubleIn(
                20, fractionalSecondsEnd - 1
            ) * 1000).toInt()

            fractionalSecondsEnd
        } else 19 // no fractional seconds

        val timeZone: TimeZone = if (dateString[tzIndex] == 'Z') {
            TimeZone.getTimeZone("GMT")
        } else {
            TimeZone.getTimeZone("GMT" + dateString.substring(tzIndex))
        }

        return Calendar.getInstance(timeZone).apply {
            set(year, month, date, hours24, minutes, seconds)
            set(Calendar.MILLISECOND, milliseconds)
        }
    }

    private fun String.assertAt(index: Int, isChar: Char) {
        val actual = this[index]
        if (actual != isChar) {
            throw ParseException(
                "Unparseable date: \"$this\"; expected '$isChar' but was '$actual'",
                index
            )
        }
    }

    /**
     * Parse the string representation of an integer at
     * `[startIndex, endIndex]` (inclusive)
     */
    private fun String.numberIn(startIndex: Int, endIndex: Int): Int {
        var value = 0
        for (i in startIndex..endIndex) {
            value *= 10
            value += (this[i] - '0')
        }
        return value
    }

    /**
     * Like [numberIn], but assumes the number is the fractional part
     *  of a number, where [startIndex] is the tenth's place digit,
     *  `[startIndex] + 1` is the hundreth's place, etc.
     */
    private fun String.doubleIn(startIndex: Int, endIndex: Int): Double {
        var value = 0.0
        for (i in endIndex downTo startIndex) {
            value /= 10.0
            value += (this[i] - '0')
        }
        return value / 10.0 // one last shift
    }

}

