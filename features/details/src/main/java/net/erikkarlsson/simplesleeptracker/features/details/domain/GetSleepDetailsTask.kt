package net.erikkarlsson.simplesleeptracker.features.details.domain

import io.reactivex.Observable
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.task.ObservableTask
import javax.inject.Inject

class GetSleepDetailsTask @Inject constructor(private val sleepRepository: SleepDataSource)
    : ObservableTask<Sleep, GetSleepDetailsTask.Params> {

    override fun observable(params: Params): Observable<Sleep> =
        sleepRepository.getSleep(params.sleepId)

    data class Params(val sleepId: Int)

}
