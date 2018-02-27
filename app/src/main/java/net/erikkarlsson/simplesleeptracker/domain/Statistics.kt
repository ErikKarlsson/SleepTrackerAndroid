package net.erikkarlsson.simplesleeptracker.domain

data class Statistics(val avgSleep: Float) {
    companion object {
        fun empty() = Statistics(-1.0f)
    }
}
