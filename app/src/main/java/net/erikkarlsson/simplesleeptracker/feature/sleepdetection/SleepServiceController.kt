package net.erikkarlsson.simplesleeptracker.feature.sleepdetection

import com.google.common.collect.ImmutableList
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import net.erikkarlsson.simplesleeptracker.di.scope.ServiceScope
import net.erikkarlsson.simplesleeptracker.domain.*
import net.erikkarlsson.simplesleeptracker.domain.entity.ActionType
import net.erikkarlsson.simplesleeptracker.domain.entity.DetectionAction
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import timber.log.Timber
import javax.inject.Inject

@ServiceScope
class SleepServiceController @Inject constructor(
        private val detectionActionDataSource: DetectionActionDataSource,
        private val dateTimeProvider: DateTimeProvider,
        private val detectionAnalyzer: DetectionAnalyzer,
        private val preferences: PreferencesDataSource,
        private val sleepRepository: SleepDataSource) {

    val actionSubject = PublishRelay.create<DetectionAction>()

    var disposables = CompositeDisposable()

    init {
        actionSubject
                .observeOn(Schedulers.io())
                .doOnNext { detectionActionDataSource.insert(it) }
                .publish()
                .connect()
                .addTo(disposables)

        detectionActionDataSource
                .getDetectionAction()
                .observeOn(Schedulers.io())
                .doOnNext { analyze(it) }
                .publish()
                .connect()
                .addTo(disposables)
    }

    fun onStartService() {
        detectionActionDataSource.deleteAllDetectionActions()
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                        onComplete = { Timber.d("Detection actions cleared") },
                        onError = { Timber.e(it) }
                )
                .addTo(disposables)
    }

    fun onAction(actionType: ActionType) {
        actionSubject.accept(DetectionAction(actionType, dateTimeProvider.now()))
    }

    fun onDestroy() {
        disposables.dispose()
    }

    private fun analyze(actionList: ImmutableList<DetectionAction>) {
        val stopDateTimestamp = preferences.getLong(PREFS_SLEEP_DETECTION_ALARM_STOP_DATE).blockingFirst()
        val stopDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(stopDateTimestamp), ZoneId.systemDefault())
        val result = detectionAnalyzer.analyze(actionList, stopDate)
        val bedTime = result.bedTime
        val wakeUp = result.wakeUp

        if (bedTime != null && wakeUp != null) {
            val sleep = Sleep(
                    fromDate = bedTime,
                    toDate = wakeUp)

            sleepRepository.getCurrentSingle()
                    .subscribeOn(Schedulers.io())
                    .subscribeBy(
                            onSuccess = {
                                if (it.toDate?.toLocalDate() == wakeUp.toLocalDate()) {
                                    sleepRepository.delete(it)
                                }
                                sleepRepository.insert(sleep)
                            },
                            onError = { Timber.e(it) }
                    )
                    .addTo(disposables)
        }
    }

}
