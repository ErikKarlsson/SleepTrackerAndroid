package net.erikkarlsson.simplesleeptracker.features.appwidget

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import net.erikkarlsson.simplesleeptracker.domain.task.ToggleSleepTask
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepAppWidgetController @Inject constructor(private val toggleSleepTask: ToggleSleepTask,
                                                   private val sleepWidgetView: SleepWidgetView,
                                                   private val sleepRepository: SleepDataSource) {

    val toggleDisposable = CompositeDisposable()
    val updateDisposable = CompositeDisposable()

    fun onToggleSleepClick() {
        toggleDisposable.clear()

        toggleSleepTask.completable(CompletableTask.None())
                .subscribeBy(
                        onComplete = { Timber.d("Sleep toggled") },
                        onError = { Timber.e("Error toggling sleep") }
                )
                .addTo(toggleDisposable)
    }

    fun updateWidget() {
        updateDisposable.clear()
        sleepRepository.getCurrent()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = { sleepWidgetView.render(it.isSleeping) },
                        onError = {
                            sleepWidgetView.render(false)
                            Timber.e(it)
                        }
                )
                .addTo(updateDisposable)
    }

}
