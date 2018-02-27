package net.erikkarlsson.simplesleeptracker.domain

import io.reactivex.Single

interface StatisticsDataSource {
    fun getStatistics(): Single<Statistics>
}
