package net.erikkarlsson.simplesleeptracker.sleepappwidget.task

import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.sleepappwidget.WidgetMsg
import net.erikkarlsson.simplesleeptracker.util.SchedulerProvider
import javax.inject.Inject

class LoadCurrentSleep @Inject constructor(
        private val sleepRepository: SleepDataSource,
        private val schedulerProvider: SchedulerProvider) {

    internal fun task(): Single<WidgetMsg> =
            sleepRepository.getCurrent()
                .map { WidgetMsg.LoadCurrentSleepResult.Success(it) }
                .cast(WidgetMsg::class.java)
                .onErrorReturn(WidgetMsg.LoadCurrentSleepResult::Failure)
                .subscribeOn(schedulerProvider.io())
}