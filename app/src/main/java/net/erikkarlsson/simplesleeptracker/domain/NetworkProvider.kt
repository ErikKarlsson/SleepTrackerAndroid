package net.erikkarlsson.simplesleeptracker.domain

import io.reactivex.Observable

interface NetworkProvider {
    fun isConnectedToNetworkStream(): Observable<Boolean>
}