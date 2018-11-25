package net.erikkarlsson.simplesleeptracker.domain

import io.reactivex.Single

interface WidgetDataSource {
    fun isWidgetAdded(): Single<Boolean>
}
