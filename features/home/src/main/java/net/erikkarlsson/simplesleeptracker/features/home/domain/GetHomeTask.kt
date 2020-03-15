package net.erikkarlsson.simplesleeptracker.features.home.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import net.erikkarlsson.simplesleeptracker.domain.FileBackupDataSourceCoroutines
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.task.FlowTask
import net.erikkarlsson.simplesleeptracker.domain.task.FlowTask.None
import javax.inject.Inject

data class HomeLoaded(val lastBackupTimestamp: Long,
                      val currentSleep: Sleep,
                      val sleepCount: Int)

class GetHomeTask @Inject constructor(private val backupDataSource: FileBackupDataSourceCoroutines,
                                      private val sleepRepository: SleepDataSource)
    : FlowTask<HomeLoaded, None> {

    override fun flow(params: None): Flow<HomeLoaded> =
            combine(listOf(
                    backupDataSource.getLastBackupTimestamp(),
                    sleepRepository.getCurrentFlow(),
                    sleepRepository.getCountFlow())) {
                HomeLoaded(it[0] as Long, it[1] as Sleep, it[2] as Int)
            }
}
