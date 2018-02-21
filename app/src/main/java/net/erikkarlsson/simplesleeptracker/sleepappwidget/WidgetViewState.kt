package net.erikkarlsson.simplesleeptracker.sleepappwidget

import com.example.android.architecture.blueprints.todoapp.mvibase.MviViewState
import net.erikkarlsson.simplesleeptracker.domain.Sleep

data class WidgetViewState(val sleep: Sleep) : MviViewState {
    companion object {
        fun idle() = WidgetViewState(Sleep.empty())
    }
}