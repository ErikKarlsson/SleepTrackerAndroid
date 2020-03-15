package net.erikkarlsson.simplesleeptracker.features.home.domain

import net.erikkarlsson.simplesleeptracker.domain.PreferencesDataSource
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.task.CoroutineTask
import javax.inject.Inject

class LogoutTask @Inject constructor(private val sleepRepository: SleepDataSource,
                                     private val preferencesDataSource: PreferencesDataSource)
    : CoroutineTask<CoroutineTask.None> {

    override suspend fun completable(params: CoroutineTask.None) {
        preferencesDataSource.clear()
        sleepRepository.deleteAll()
    }

}
