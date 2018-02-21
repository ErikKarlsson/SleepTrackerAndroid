package net.erikkarlsson.simplesleeptracker.sleepappwidget

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.erikkarlsson.simplesleeptracker.data.SleepRepository
import net.erikkarlsson.simplesleeptracker.domain.Sleep
import org.threeten.bp.OffsetDateTime
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepActionProcessorHolder @Inject constructor(private val sleepRepository: SleepRepository) {

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
                    sleepRepository.getCurrentSleep()
                        .toObservable()
                        .doOnNext{ Timber.d("Load current sleep %s: ", it.toString() ) }
                        .map { WidgetResult.LoadCurrentSleepResult.Success(it) }
                        .cast(WidgetResult.LoadCurrentSleepResult::class.java)
                        .onErrorReturn(WidgetResult.LoadCurrentSleepResult::Failure)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                }
            }

    private val toggleSleepProcessor =
            ObservableTransformer<WidgetAction.ToggleSleepAction, WidgetResult.ToggleSleepResult> { actions ->
                actions.flatMap {
                    sleepRepository.getCurrentSleep()
                        .toObservable()
                        .flatMap { sleep -> toggleSleep(sleep) }
                        .onErrorReturn(WidgetResult.ToggleSleepResult::Failure)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                }
            }

    private fun toggleSleep(currentSleep: Sleep): Observable<WidgetResult.ToggleSleepResult> {
        if (currentSleep != Sleep.empty() && currentSleep.toDate == null) {
            updateSleepInDb(currentSleep)
        } else {
            insertNewSleepInDb()
        }

        return sleepRepository.getCurrentSleep()
            .toObservable()
            .subscribeOn(Schedulers.io())
            .map { WidgetResult.ToggleSleepResult.Success(it) }
    }

    private fun insertNewSleepInDb() {
        val sleep = Sleep(fromDate = OffsetDateTime.now())
        sleepRepository.insertSleep(sleep)
    }

    private fun updateSleepInDb(currentSleep: Sleep) {
        val updatedSleep = currentSleep.copy(toDate = OffsetDateTime.now())
        sleepRepository.updateSleep(updatedSleep)
    }
}