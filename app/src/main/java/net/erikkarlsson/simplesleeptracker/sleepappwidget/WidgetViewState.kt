package net.erikkarlsson.simplesleeptracker.sleepappwidget

import com.example.android.architecture.blueprints.todoapp.mvibase.MviViewState

data class WidgetViewState(val isSleeping: Boolean) : MviViewState {
    companion object {
        fun idle() = WidgetViewState(false)
    }
}