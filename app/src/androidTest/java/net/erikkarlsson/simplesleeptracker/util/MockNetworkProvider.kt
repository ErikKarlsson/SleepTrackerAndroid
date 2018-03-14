package net.erikkarlsson.simplesleeptracker.util

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import net.erikkarlsson.simplesleeptracker.domain.NetworkProvider

class MockNetworkProvider : NetworkProvider {

    private val connectedRelay: BehaviorRelay<Boolean> = BehaviorRelay.create()

    fun setConnectedToNetwork(connected: Boolean) {
        connectedRelay.accept(connected)
    }

    override fun isConnectedToNetwork(): Observable<Boolean> = connectedRelay.startWith(true)
}