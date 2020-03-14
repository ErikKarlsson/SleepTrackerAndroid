package net.erikkarlsson.simplesleeptracker.features.diary.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.zip
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSourceCoroutines
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSourceCoroutines
import net.erikkarlsson.simplesleeptracker.domain.entity.SleepDiary
import net.erikkarlsson.simplesleeptracker.domain.task.FlowTask
import net.erikkarlsson.simplesleeptracker.domain.task.ObservableTask.None
import javax.inject.Inject

class GetSleepDiaryTask @Inject constructor(private val sleepRepository: SleepDataSourceCoroutines,
                                            private val statisticsRepository: StatisticsDataSourceCoroutines)
    : FlowTask<SleepDiary, None> {

    override fun flow(params: None): Flow<SleepDiary> =
            sleepRepository.getSleepPaged()
                    .zip(statisticsRepository.getSleepCountYearMonth()) { pagedSleep, sleepCountYearMonth ->
                        SleepDiary(pagedSleep = pagedSleep,
                                sleepCountYearMonth = sleepCountYearMonth)
                    }
}
