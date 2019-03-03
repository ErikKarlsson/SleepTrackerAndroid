package net.erikkarlsson.simplesleeptracker.feature.sleepdetection

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import net.erikkarlsson.simplesleeptracker.di.scope.ServiceScope
import net.erikkarlsson.simplesleeptracker.domain.DateTimeProvider
import net.erikkarlsson.simplesleeptracker.domain.DetectionActionDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.ActionType
import net.erikkarlsson.simplesleeptracker.domain.entity.DetectionAction
import javax.inject.Inject

@ServiceScope
class SleepServiceController @Inject constructor(
        private val detectionActionDataSource: DetectionActionDataSource,
        private val dateTimeProvider: DateTimeProvider,
        private val detectionAnalyzer: DetectionAnalyzer) {

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
                .doOnNext { detectionAnalyzer.analyze(it) }
                .publish()
                .connect()
                .addTo(disposables)
    }

    fun onAction(actionType: ActionType) {
        actionSubject.accept(DetectionAction(actionType, dateTimeProvider.now()))
    }

    fun onDestroy() {
        disposables.dispose()
    }

}
