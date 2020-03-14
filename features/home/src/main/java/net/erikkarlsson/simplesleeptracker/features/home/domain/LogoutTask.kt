package net.erikkarlsson.simplesleeptracker.features.home.domain

import net.erikkarlsson.simplesleeptracker.domain.PreferencesDataSourceFlow
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSourceCoroutines
import net.erikkarlsson.simplesleeptracker.domain.task.CoroutineTask
import javax.inject.Inject

class LogoutTask @Inject constructor(private val sleepRepository: SleepDataSourceCoroutines,
                                     private val preferencesDataSource: PreferencesDataSourceFlow)
    : CoroutineTask<CoroutineTask.None> {

    override suspend fun completable(params: CoroutineTask.None) {
        preferencesDataSource.clear()
        sleepRepository.deleteAll()
    }

}
