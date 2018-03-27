package net.erikkarlsson.simplesleeptracker.util

import net.erikkarlsson.simplesleeptracker.testutil.InstantTaskExecutorExtension
import net.erikkarlsson.simplesleeptracker.testutil.RxImmediateSchedulerExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantTaskExecutorExtension::class, RxImmediateSchedulerExtension::class)
class DateTimeExtKtTest {

    @Test
    fun `datetime midnight offset in seconds`() {
        val beforeTwelveInMidnight = "2018-03-05T23:59:10+01:00".offsetDateTime
        val afterTwelveInMidnight = "2018-03-05T00:00:50+01:00".offsetDateTime
        assertEquals(-50, beforeTwelveInMidnight.midnightOffsetInSeconds)
        assertEquals(50, afterTwelveInMidnight.midnightOffsetInSeconds)
    }

    @Test
    fun `hours between datetimes`() {
        val startDate = "2018-03-05T05:40:00+01:00".offsetDateTime
        val endDate = "2018-03-05T06:50:00+01:00".offsetDateTime
        val actual = startDate.hoursBetween(endDate)
        assertEquals(1.167f, actual)
    }

    @Test
    fun `localtime diff hours`() {
        val startTime = LocalTime.of(12, 0, 0)
        val endTime = LocalTime.of(12, 1, 0)
        val actualPositive = startTime.diffHours(endTime)
        val actualNegative = endTime.diffHours(startTime)
        assertEquals(0.0166f, actualPositive, 0.0001f)
        assertEquals(-0.0166f, actualNegative, 0.0001f)
    }

    @Test
    fun `midnight offset seconds to local time positive`() {
        val seconds = 60
        val actual = seconds.midnightOffsetToLocalTime
        val expected = LocalTime.of(0, 1, 0)
        assertEquals(expected, actual)
    }

    @Test
    fun `midnight offset seconds to local time negative`() {
        val seconds = -60
        val actual = seconds.midnightOffsetToLocalTime
        val expected = LocalTime.of(23, 59, 0)
        assertEquals(expected, actual)
    }

    @Test
    fun `string to offset date time`() {
        val actual = "2018-03-05T05:40:00+01:00".offsetDateTime
        val expected = OffsetDateTime.parse("2018-03-05T05:40:00+01:00")
        assertEquals(actual, expected)
    }

    @Test
    fun `hours to seconds`() {
        val actual = 1.hoursToSeconds
        assertEquals(3600, actual)
    }

    @Test
    fun `minutes to seconds`() {
        val actual = 1.minutesToSeconds
        assertEquals(60, actual)
    }
}