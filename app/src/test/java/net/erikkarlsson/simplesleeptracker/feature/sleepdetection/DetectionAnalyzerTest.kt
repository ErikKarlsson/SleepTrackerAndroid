package net.erikkarlsson.simplesleeptracker.feature.sleepdetection

import com.google.common.collect.ImmutableList
import junit.framework.Assert.assertEquals
import net.erikkarlsson.simplesleeptracker.domain.entity.ActionType
import net.erikkarlsson.simplesleeptracker.domain.entity.DetectionAction
import net.erikkarlsson.simplesleeptracker.util.localDate
import net.erikkarlsson.simplesleeptracker.util.offsetDateTime
import org.junit.Test

class DetectionAnalyzerTest {

    val detectionAnalyzer = DetectionAnalyzer()

    @Test
    fun `slept all night wake up immediately`() {
        val actionList = ImmutableList.of(
                actionOf("2019-03-03T22:00:00+01:00", ActionType.SCREEN_OFF),
                actionOf("2019-03-04T07:00:00+01:00", ActionType.SCREEN_ON)
        )

        val result = detectionAnalyzer.analyze(
                actionList = actionList,
                detectionStopDate = "2019-03-04".localDate)

        val expectedResult = AnalyzerResult(
                bedTime = "2019-03-03T22:00:00+01:00".offsetDateTime,
                wakeUp = "2019-03-04T07:00:00+01:00".offsetDateTime
        )

        assertEquals(expectedResult, result)
    }

    @Test
    fun `screen on middle of the night`() {
        val actionList = ImmutableList.of(
                actionOf("2019-03-03T22:00:00+01:00", ActionType.SCREEN_OFF),
                actionOf("2019-03-04T02:00:00+01:00", ActionType.SCREEN_ON),
                actionOf("2019-03-04T02:01:00+01:00", ActionType.SCREEN_OFF),
                actionOf("2019-03-04T07:00:00+01:00", ActionType.SCREEN_ON)
        )

        val result = detectionAnalyzer.analyze(
                actionList = actionList,
                detectionStopDate = "2019-03-04".localDate)

        val expectedResult = AnalyzerResult(
                bedTime = "2019-03-03T22:00:00+01:00".offsetDateTime,
                wakeUp = "2019-03-04T07:00:00+01:00".offsetDateTime
        )

        assertEquals(expectedResult, result)
    }

    @Test
    fun `snooze a few times before wake up`() {
        val actionList = ImmutableList.of(
                actionOf("2019-03-03T22:00:00+01:00", ActionType.SCREEN_OFF),
                actionOf("2019-03-04T06:40:00+01:00", ActionType.SCREEN_ON),
                actionOf("2019-03-04T06:40:00+01:00", ActionType.SCREEN_OFF),
                actionOf("2019-03-04T06:50:00+01:00", ActionType.SCREEN_ON),
                actionOf("2019-03-04T06:50:00+01:00", ActionType.SCREEN_OFF),
                actionOf("2019-03-04T07:00:00+01:00", ActionType.SCREEN_ON)
        )

        val result = detectionAnalyzer.analyze(
                actionList = actionList,
                detectionStopDate = "2019-03-04".localDate)

        val expectedResult = AnalyzerResult(
                bedTime = "2019-03-03T22:00:00+01:00".offsetDateTime,
                wakeUp = "2019-03-04T07:00:00+01:00".offsetDateTime
        )

        assertEquals(expectedResult, result)
    }

    fun actionOf(date: String, actionType: ActionType): DetectionAction =
            DetectionAction(actionType, date.offsetDateTime)
}
