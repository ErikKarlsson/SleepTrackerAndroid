package net.erikkarlsson.simplesleeptracker.sleepappwidget

import com.example.android.architecture.blueprints.todoapp.mvibase.MviIntent

sealed class WidgetIntent : MviIntent {
    object InitialIntent : WidgetIntent()
    object ToggleSleepIntent : WidgetIntent()
}