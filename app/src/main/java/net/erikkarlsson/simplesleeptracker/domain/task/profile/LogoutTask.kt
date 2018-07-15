package net.erikkarlsson.simplesleeptracker.domain.task.profile

import io.reactivex.Completable
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import javax.inject.Inject

class LogoutTask @Inject constructor(private val sleepRepository: SleepDataSource)
    : CompletableTask<CompletableTask.None>
{
    override fun execute(params: CompletableTask.None): Completable {
        return Completable.fromCallable { sleepRepository.deleteAll() }
    }

}