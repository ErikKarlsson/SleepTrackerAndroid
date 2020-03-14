package net.erikkarlsson.simplesleeptracker.features.appwidget

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSourceCoroutines
import net.erikkarlsson.simplesleeptracker.domain.task.CoroutineTask
import net.erikkarlsson.simplesleeptracker.domain.task.ToggleSleepTask
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepAppWidgetController @Inject constructor(private val toggleSleepTask: ToggleSleepTask,
                                                   private val sleepWidgetView: SleepWidgetView,
                                                   private val sleepRepository: SleepDataSourceCoroutines) {
    fun initialize() {
        GlobalScope.launch(Dispatchers.IO) {
            sleepRepository.getCurrentFlow().collect {
                sleepWidgetView.render(it.isSleeping)
            }
        }
    }

    fun onToggleSleepClick() {
        GlobalScope.launch(Dispatchers.IO) {
            toggleSleepTask.completable(CoroutineTask.None())
        }
    }

}
