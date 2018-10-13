package net.erikkarlsson.simplesleeptracker.domain.task.sleep

import io.reactivex.Observable
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.task.ObservableTask
import javax.inject.Inject

class GetCurrentSleepTask @Inject constructor(private val sleepRepository: SleepDataSource)
    : ObservableTask<Sleep, ObservableTask.None> {

    override fun execute(params: ObservableTask.None): Observable<Sleep> =
            sleepRepository.getCurrent()

}
