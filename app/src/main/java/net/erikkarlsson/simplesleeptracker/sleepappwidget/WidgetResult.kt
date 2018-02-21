package net.erikkarlsson.simplesleeptracker.sleepappwidget

import com.example.android.architecture.blueprints.todoapp.mvibase.MviResult
import net.erikkarlsson.simplesleeptracker.domain.Sleep

sealed class WidgetResult : MviResult {

    sealed class LoadCurrentSleepResult() : WidgetResult() {
        data class Success(val sleep: Sleep) : LoadCurrentSleepResult()
        data class Failure(val error: Throwable) : LoadCurrentSleepResult()
    }

    sealed class ToggleSleepResult() : WidgetResult() {
        data class Success(val sleep: Sleep) : ToggleSleepResult()
        data class Failure(val error: Throwable) : ToggleSleepResult()
    }

}