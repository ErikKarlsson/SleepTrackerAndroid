package net.erikkarlsson.simplesleeptracker.feature.home.domain

import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import net.erikkarlsson.simplesleeptracker.domain.FileBackupDataSource
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.task.ObservableTask
import net.erikkarlsson.simplesleeptracker.domain.task.ObservableTask.None
import javax.inject.Inject

data class HomeLoaded(val lastBackupTimestamp: Long,
                      val currentSleep: Sleep,
                      val sleepCount: Int)

class GetHomeTask @Inject constructor(private val backupDataSource: FileBackupDataSource,
                                      private val sleepRepository: SleepDataSource)
    : ObservableTask<HomeLoaded, None> {

    override fun observable(params: None): Observable<HomeLoaded> =
            Observables.combineLatest(
                    backupDataSource.getLastBackupTimestamp(),
                    sleepRepository.getCurrent(),
                    sleepRepository.getCount())
            { lastBackupTimestamp, currentSleep, sleepCount ->
                HomeLoaded(lastBackupTimestamp, currentSleep, sleepCount)
            }

}
