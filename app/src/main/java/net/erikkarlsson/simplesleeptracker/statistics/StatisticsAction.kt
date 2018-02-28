package net.erikkarlsson.simplesleeptracker.statistics

import com.example.android.architecture.blueprints.todoapp.mvibase.MviAction

sealed class StatisticsAction : MviAction {
    object LoadStatisticsAction : StatisticsAction()
}