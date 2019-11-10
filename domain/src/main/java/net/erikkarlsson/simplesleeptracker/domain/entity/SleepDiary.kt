package net.erikkarlsson.simplesleeptracker.domain.entity

import androidx.paging.PagedList

data class SleepDiary(val pagedSleep: PagedList<Sleep>,
                      val sleepCountYearMonth: List<SleepCountYearMonth>) {

    fun getSleepCount(year: Int, month: Int): Int {
        for (sleepCountYearMonth in sleepCountYearMonth) {
            if (sleepCountYearMonth.year == year && sleepCountYearMonth.month == month) {
                return sleepCountYearMonth.sleepCount
            }
        }
        return 0
    }

}
