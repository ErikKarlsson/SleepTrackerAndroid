package net.erikkarlsson.simplesleeptracker.sleepappwidget

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.erikkarlsson.simplesleeptracker.DateTimeUtils
import net.erikkarlsson.simplesleeptracker.data.Sleep
import net.erikkarlsson.simplesleeptracker.data.SleepRepository
import org.threeten.bp.OffsetDateTime
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepAppWidgetController @Inject constructor(private val sleepRepository: SleepRepository) {

    val sleepRelay = PublishRelay.create<Sleep>()

    init {
        sleepRepository.getCurrentSleep()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ sleepRelay.accept(it) },
                        { Timber.d("Error toggling sleep: %s", it.message) },
                        { onNoSleepFound() })
    }

    fun toggleSleep() {
        sleepRepository.getCurrentSleep()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { updateDb(it) }
                .subscribe({ sleepRelay.accept(it) },
                        { Timber.d("Error toggling sleep: %s", it.message) },
                        { onNoSleepFound() })
    }

    fun getSleepStream(): Observable<Sleep> {
        return sleepRelay.distinctUntilChanged()
    }

    private fun updateSleepInDb(currentSleep: Sleep): Maybe<Sleep> {
        val fromDate = currentSleep.fromDate

        if (fromDate == null) {
            throw RuntimeException("currentSleep has invalid state, fromDate is null")
        }

        val toDate = OffsetDateTime.now()
        val hours = DateTimeUtils.calculateHoursBetweenDates(fromDate, toDate)
        val toDateMidnightOffset = DateTimeUtils.calculateOffsetFromMidnightInSeconds(toDate)
        val updatedSleep = currentSleep.copy(toDate = toDate, toDateMidnightOffsetSeconds = toDateMidnightOffset, hours = hours)

        return Completable.fromAction({ sleepRepository.updateSleep(updatedSleep) })
                .andThen(Maybe.just(updatedSleep))
                .subscribeOn(Schedulers.io())
    }

    private fun onNoSleepFound() {
        insertNewSleepInDb()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ sleepRelay.accept(it) },
                        { Timber.d("Error inserting sleep: %s", it.message) },
                        {})
    }

    private fun insertNewSleepInDb(): Maybe<Sleep> {
        val fromDate = OffsetDateTime.now()
        val fromDateMidnightOffset = DateTimeUtils.calculateOffsetFromMidnightInSeconds(fromDate)
        val newSleep = Sleep(fromDate = fromDate, fromDateMidnightOffsetSeconds = fromDateMidnightOffset)

        return Completable.fromAction({ sleepRepository.insertSleep(newSleep) })
                .andThen(sleepRepository.getCurrentSleep())
                .subscribeOn(Schedulers.io())
    }

    private fun updateDb(currentSleep: Sleep): Maybe<Sleep> {
        if (currentSleep.toDate == null) {
            return updateSleepInDb(currentSleep)
        } else {
            return insertNewSleepInDb()
        }
    }
}