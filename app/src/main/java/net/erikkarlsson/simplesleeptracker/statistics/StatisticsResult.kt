package net.erikkarlsson.simplesleeptracker.sleepappwidget

import com.example.android.architecture.blueprints.todoapp.mvibase.MviResult
import net.erikkarlsson.simplesleeptracker.domain.Statistics

sealed class StatisticsResult : MviResult {

    sealed class LoadStatisticsResult() : StatisticsResult() {
        data class Success(val statistics: Statistics) : LoadStatisticsResult()
        data class Failure(val error: Throwable) : LoadStatisticsResult()
    }

}