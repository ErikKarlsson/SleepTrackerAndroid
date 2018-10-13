package net.erikkarlsson.simplesleeptracker.feature.home.domain

import io.reactivex.Completable
import net.erikkarlsson.simplesleeptracker.domain.PreferencesDataSource
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import javax.inject.Inject

class LogoutTask @Inject constructor(private val sleepRepository: SleepDataSource,
                                     private val preferencesDataSource: PreferencesDataSource)
    : CompletableTask<CompletableTask.None>
{
    override fun execute(params: CompletableTask.None): Completable {
        return Completable.fromCallable {
            preferencesDataSource.clear()
            sleepRepository.deleteAll()
        }
    }

}
