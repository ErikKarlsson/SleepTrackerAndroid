package net.erikkarlsson.simplesleeptracker.feature.home.domain

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import net.erikkarlsson.simplesleeptracker.domain.PreferencesDataSource
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import javax.inject.Inject

class LogoutTask @Inject constructor(private val sleepRepository: SleepDataSource,
                                     private val preferencesDataSource: PreferencesDataSource)
    : CompletableTask<CompletableTask.None> {
    override fun completable(params: CompletableTask.None): Completable {
        return Completable.fromCallable {
            preferencesDataSource.clear()
            sleepRepository.deleteAll()
        }
                .subscribeOn(Schedulers.io())
    }

}
