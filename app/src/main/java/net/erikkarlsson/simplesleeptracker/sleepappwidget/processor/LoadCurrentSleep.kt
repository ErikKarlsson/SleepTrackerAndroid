package net.erikkarlsson.simplesleeptracker.sleepappwidget.processor

import io.reactivex.ObservableTransformer
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.sleepappwidget.WidgetAction
import net.erikkarlsson.simplesleeptracker.sleepappwidget.WidgetResult
import net.erikkarlsson.simplesleeptracker.util.SchedulerProvider
import javax.inject.Inject

class LoadCurrentSleep @Inject constructor(
        private val sleepRepository: SleepDataSource,
        private val schedulerProvider: SchedulerProvider) {

    internal val processor =
            ObservableTransformer<WidgetAction.LoadCurrentSleepAction, WidgetResult.LoadCurrentSleepResult> { actions ->
                actions.flatMap {
                    sleepRepository.getCurrent().toObservable()
                        .map { WidgetResult.LoadCurrentSleepResult.Success(it) }
                        .cast(WidgetResult.LoadCurrentSleepResult::class.java)
                        .onErrorReturn(WidgetResult.LoadCurrentSleepResult::Failure)
                        .subscribeOn(schedulerProvider.io())
                }
            }
}