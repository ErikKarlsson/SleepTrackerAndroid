package net.erikkarlsson.simplesleeptracker.domain

import io.reactivex.Observable

interface StatisticsDataSource {
    fun getStatistics(): Observable<Statistics>
}
