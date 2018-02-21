package net.erikkarlsson.simplesleeptracker.sleepappwidget

import com.example.android.architecture.blueprints.todoapp.mvibase.MviAction

sealed class WidgetAction : MviAction {
    object LoadCurrentSleepAction : WidgetAction()
    object ToggleSleepAction : WidgetAction()
}