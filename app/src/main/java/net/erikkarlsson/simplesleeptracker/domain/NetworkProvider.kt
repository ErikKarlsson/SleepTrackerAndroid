package net.erikkarlsson.simplesleeptracker.domain

import io.reactivex.Observable

interface NetworkProvider {
    fun isConnectedToNetwork(): Observable<Boolean>
}