package net.erikkarlsson.simplesleeptracker.features.appwidget

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.erikkarlsson.simplesleeptracker.core.DispatcherProvider
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.task.CoroutineTask
import net.erikkarlsson.simplesleeptracker.domain.task.ToggleSleepTask
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepAppWidgetController @Inject constructor(private val toggleSleepTask: ToggleSleepTask,
                                                   private val sleepWidgetView: SleepWidgetView,
                                                   private val sleepRepository: SleepDataSource,
                                                   private val dispatchers: DispatcherProvider
) {
    fun initialize() {
        GlobalScope.launch(dispatchers.io()) {
            sleepRepository.getCurrentFlow().collect {
                sleepWidgetView.render(it.isSleeping)
            }
        }
    }

    fun onToggleSleepClick() {
        GlobalScope.launch(dispatchers.io()) {
            toggleSleepTask.completable(CoroutineTask.None())
        }
    }

}
