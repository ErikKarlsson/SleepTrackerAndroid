package net.erikkarlsson.simplesleeptracker.features.diary.domain

import io.reactivex.Observable
import io.reactivex.rxkotlin.zipWith
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.SleepDiary
import net.erikkarlsson.simplesleeptracker.domain.task.ObservableTask
import net.erikkarlsson.simplesleeptracker.domain.task.ObservableTask.None
import javax.inject.Inject

class GetSleepDiaryTask @Inject constructor(private val sleepRepository: SleepDataSource,
                                            private val statisticsRepository: StatisticsDataSource)
    : ObservableTask<SleepDiary, None> {

    override fun observable(params: None): Observable<SleepDiary> =
            sleepRepository.getSleepPaged()
                    .zipWith(statisticsRepository.getSleepCountYearMonth())
                    .map { SleepDiary(pagedSleep = it.first,
                                      sleepCountYearMonth = it.second) }

}
