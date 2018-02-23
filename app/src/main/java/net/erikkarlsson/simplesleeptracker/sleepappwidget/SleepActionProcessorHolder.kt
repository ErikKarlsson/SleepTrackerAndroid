package net.erikkarlsson.simplesleeptracker.sleepappwidget

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import net.erikkarlsson.simplesleeptracker.domain.Sleep
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.util.SchedulerProvider
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepActionProcessorHolder @Inject constructor(
        private val sleepRepository: SleepDataSource,
        private val schedulerProvider: SchedulerProvider) {

    internal val actionProcessor =
            ObservableTransformer<WidgetAction, WidgetResult> { actions ->
                actions.publish { shared ->
                    Observable.merge(
                            shared.ofType(WidgetAction.LoadCurrentSleepAction::class.java).compose(loadCurrentSleepProcessor),
                            shared.ofType(WidgetAction.ToggleSleepAction::class.java).compose(toggleSleepProcessor))

                }
            }

    private val loadCurrentSleepProcessor =
            ObservableTransformer<WidgetAction.LoadCurrentSleepAction, WidgetResult.LoadCurrentSleepResult> { actions ->
                actions.flatMap {
                    getCurrentSleep()
                        .map { WidgetResult.LoadCurrentSleepResult.Success(it) }
                        .cast(WidgetResult.LoadCurrentSleepResult::class.java)
                        .onErrorReturn(WidgetResult.LoadCurrentSleepResult::Failure)
                        .subscribeOn(schedulerProvider.io())
                }
            }

    private val toggleSleepProcessor =
            ObservableTransformer<WidgetAction.ToggleSleepAction, WidgetResult.ToggleSleepResult> { actions ->
                actions.flatMap {
                    getCurrentSleep()
                        .flatMap {
                            Completable.fromAction { this.toggleSleep(it) }
                                .andThen(Observable.defer {
                                    getCurrentSleep()
                                        .map { WidgetResult.ToggleSleepResult.Success(it) }
                                        .cast(WidgetResult.ToggleSleepResult::class.java)
                                })
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
}