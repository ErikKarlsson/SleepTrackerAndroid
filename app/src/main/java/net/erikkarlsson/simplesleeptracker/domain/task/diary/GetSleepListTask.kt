package net.erikkarlsson.simplesleeptracker.domain.task.diary

import android.arch.paging.PagedList
import io.reactivex.Observable
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.task.ObservableTask
import net.erikkarlsson.simplesleeptracker.domain.task.ObservableTask.None
import javax.inject.Inject

class GetSleepListTask @Inject constructor(private val sleepRepository: SleepDataSource)
    : ObservableTask<PagedList<Sleep>, None> {

    override fun execute(params: None): Observable<PagedList<Sleep>> =
        sleepRepository.getSleepPaged()
}