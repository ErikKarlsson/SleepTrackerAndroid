package net.erikkarlsson.simplesleeptracker

import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.AppLifecycle
import javax.inject.Inject

class AndroidAppLifecycle @Inject constructor(): AppLifecycle {
    override fun isForegrounded(): Single<Boolean> =
            Single.just(App.isForegrounded)
}
