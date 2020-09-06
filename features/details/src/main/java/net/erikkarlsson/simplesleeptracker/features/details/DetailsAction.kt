package net.erikkarlsson.simplesleeptracker.features.details

import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime

sealed class DetailsAction
data class PickStartDateAction(val date: OffsetDateTime) : DetailsAction()
data class PickTimeAsleepAction(val time: LocalTime) : DetailsAction()
data class PickTimeAwakeAction(val time: LocalTime?) : DetailsAction()
object DeleteAction : DetailsAction()
object NavigateUp : DetailsAction()
