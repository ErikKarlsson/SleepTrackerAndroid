package net.erikkarlsson.simplesleeptracker

import net.erikkarlsson.simplesleeptracker.domain.AppLifecycle
import javax.inject.Inject

class AndroidAppLifecycle @Inject constructor(): AppLifecycle {
    override fun isForegrounded(): Boolean = App.isForegrounded
}
