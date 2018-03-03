package net.erikkarlsson.simplesleeptracker.sleepappwidget

import android.arch.lifecycle.LiveData
import net.erikkarlsson.simplesleeptracker.elm.RuntimeFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepAppWidgetViewModel @Inject constructor(appWidgetComponent: AppWidgetComponent)
{
    private val runtime = RuntimeFactory.create(appWidgetComponent)
    
    fun dispatch(msg: WidgetMsg) {
        runtime.dispatch(msg)
    }

    fun state(): LiveData<WidgetState> = runtime.state()

}