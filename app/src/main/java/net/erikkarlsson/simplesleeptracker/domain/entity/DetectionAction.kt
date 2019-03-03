package net.erikkarlsson.simplesleeptracker.domain.entity

import org.threeten.bp.OffsetDateTime

data class DetectionAction(var actionType: ActionType,
                           val date: OffsetDateTime,
                           val id: Int? = null) {
    companion object {
        fun empty() = DetectionAction(ActionType.NONE, OffsetDateTime.MIN, null)
    }
}
