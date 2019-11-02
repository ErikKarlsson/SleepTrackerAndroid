package net.erikkarlsson.simplesleeptracker.feature.appwidget

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import net.erikkarlsson.simplesleeptracker.domain.task.sleep.ToggleSleepTask
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepAppWidgetController @Inject constructor(private val toggleSleepTask: ToggleSleepTask,
                                                   private val sleepWidgetView: SleepWidgetView) {

    val disposables = CompositeDisposable()

    fun onToggleSleepClick() {
        disposables.clear()

        toggleSleepTask.execute(CompletableTask.None())
                .subscribeBy(
                        onComplete = { Timber.d("Sleep toggled") },
                        onError = { Timber.e("Error toggling sleep") }
                )
                .addTo(disposables)
    }

    fun onWidgetUpdate() {
        sleepWidgetView.update()
    }

}
