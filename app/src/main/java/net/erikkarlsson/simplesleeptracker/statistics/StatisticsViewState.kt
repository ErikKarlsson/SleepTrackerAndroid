package net.erikkarlsson.simplesleeptracker.statistics

import com.example.android.architecture.blueprints.todoapp.mvibase.MviViewState
import net.erikkarlsson.simplesleeptracker.domain.Statistics

data class StatisticsViewState(val statistics: Statistics) : MviViewState {
    companion object {
        fun idle() = StatisticsViewState(Statistics.empty())
    }
}