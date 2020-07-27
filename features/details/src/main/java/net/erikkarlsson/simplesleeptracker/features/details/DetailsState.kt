package net.erikkarlsson.simplesleeptracker.features.details

import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep

data class DetailsState(
        val sleepId: Int,
        val sleep: Sleep? = null,
        val isDeleted: Boolean = false
) {
    val hoursSlept: Float get() = sleep?.hours ?: 0f

    companion object {
        fun empty() = DetailsState(0, null, false)
    }
}

