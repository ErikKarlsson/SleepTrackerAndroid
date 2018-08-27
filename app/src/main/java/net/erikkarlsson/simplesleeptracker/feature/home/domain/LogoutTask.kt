package net.erikkarlsson.simplesleeptracker.feature.home.domain

import com.f2prateek.rx.preferences2.RxSharedPreferences
import io.reactivex.Completable
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import javax.inject.Inject

class LogoutTask @Inject constructor(private val sleepRepository: SleepDataSource,
                                     private val rxSharedPreferences: RxSharedPreferences)
    : CompletableTask<CompletableTask.None>
{
    override fun execute(params: CompletableTask.None): Completable {
        return Completable.fromCallable {
            rxSharedPreferences.clear()
            sleepRepository.deleteAll()
        }
    }

}