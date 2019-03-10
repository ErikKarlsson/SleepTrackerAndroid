package net.erikkarlsson.simplesleeptracker.feature.sleepdetection

import com.google.common.collect.ImmutableList
import net.erikkarlsson.simplesleeptracker.domain.entity.ActionType
import net.erikkarlsson.simplesleeptracker.domain.entity.DetectionAction
import net.erikkarlsson.simplesleeptracker.util.hoursTo
import org.threeten.bp.LocalDateTime
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

private const val MIN_HOURS_INBETWEEN_START_ACTION = 1.0

data class AnalyzerResult(val bedTime: OffsetDateTime?, val wakeUp: OffsetDateTime?) {

    companion object {
        fun empty() = AnalyzerResult(null, null)
    }
}

class DetectionAnalyzer @Inject constructor() {

    fun analyze(actionList: ImmutableList<DetectionAction>,
                detectionStopDate: LocalDateTime): AnalyzerResult {

        val atLeastTwoActions = actionList.size > 1

        val isActionOnStopDate =
                actionList.size > 0 &&
                        actionList.last().date.toLocalDate() == detectionStopDate.toLocalDate()

        val shouldAnalyze = atLeastTwoActions && isActionOnStopDate

        if (!shouldAnalyze) {
            return AnalyzerResult.empty()
        }

        var bedTime: OffsetDateTime? = null
        var wakeUp: OffsetDateTime? = null

        for (i in 0 until actionList.size - 1) {
            val action = actionList[i]
            val action2 = actionList[i + 1]
            val durationHours = action.date.hoursTo(action2.date)

            if (durationHours >= MIN_HOURS_INBETWEEN_START_ACTION) {
                if (bedTime == null && action.actionType == ActionType.SCREEN_OFF) {
                    bedTime = action.date
                }

                if (action2.actionType == ActionType.SCREEN_ON) {
                    wakeUp = action2.date
                }
            }
        }

        return AnalyzerResult(bedTime, wakeUp)
    }
}
