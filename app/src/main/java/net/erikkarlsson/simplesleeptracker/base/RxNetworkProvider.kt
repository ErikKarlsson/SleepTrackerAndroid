package net.erikkarlsson.simplesleeptracker.base

import android.content.Context
import android.net.NetworkInfo
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.Observable
import net.erikkarlsson.simplesleeptracker.domain.NetworkProvider
import javax.inject.Inject

class RxNetworkProvider @Inject constructor(private val context: Context) : NetworkProvider {

    override fun isConnectedToNetworkStream(): Observable<Boolean> {
        return ReactiveNetwork.observeNetworkConnectivity(context)
            .map { it.getState() == NetworkInfo.State.CONNECTED }
    }

}