package net.erikkarlsson.simplesleeptracker.statistics

import com.example.android.architecture.blueprints.todoapp.mvibase.MviIntent

sealed class StatisticsIntent : MviIntent {
    object InitialIntent : StatisticsIntent()
}