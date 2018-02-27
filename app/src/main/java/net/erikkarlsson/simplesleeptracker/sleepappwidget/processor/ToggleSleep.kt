package net.erikkarlsson.simplesleeptracker.sleepappwidget.processor

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import net.erikkarlsson.simplesleeptracker.domain.Sleep
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.sleepappwidget.WidgetAction
import net.erikkarlsson.simplesleeptracker.sleepappwidget.WidgetResult
import net.erikkarlsson.simplesleeptracker.util.SchedulerProvider
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

class ToggleSleep @Inject constructor(
        private val sleepRepository: SleepDataSource,
        private val schedulerProvider: SchedulerProvider) {

    internal val processor =
            ObservableTransformer<WidgetAction.ToggleSleepAction, WidgetResult.ToggleSleepResult> { actions ->
                actions.flatMap {
                    getCurrentSleep()
                        .flatMap {
                            Completable.fromAction { toggleSleep(it) }
                                .andThen(getToggleSleepResult())
                        }
                        .onErrorReturn { WidgetResult.ToggleSleepResult.Failure(it) }
                        .subscribeOn(schedulerProvider.io())
                }
            }

    private fun getCurrentSleep(): Observable<Sleep> {
        return sleepRepository.getCurrent().toObservable()
    }

    private fun toggleSleep(currentSleep: Sleep) {
        if (currentSleep.isSleeping) {
            stopSleeping(currentSleep)
        } else {
            startSleeping()
        }
    }

    private fun startSleeping() {
        val sleep = Sleep(fromDate = OffsetDateTime.now())
        sleepRepository.insert(sleep)
    }

    private fun stopSleeping(currentSleep: Sleep) {
        val sleep = currentSleep.copy(toDate = OffsetDateTime.now())
        sleepRepository.update(sleep)
    }

    private fun getToggleSleepResult(): Observable<WidgetResult.ToggleSleepResult> {
        return Observable.defer {
            getCurrentSleep()
                .map { WidgetResult.ToggleSleepResult.Success(it) }
                .cast(WidgetResult.ToggleSleepResult::class.java)
        }
    }
}