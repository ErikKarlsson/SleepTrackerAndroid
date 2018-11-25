package net.erikkarlsson.simplesleeptracker.domain

import io.reactivex.Single

interface AppLifecycle {
    fun isForegrounded(): Single<Boolean>
}
