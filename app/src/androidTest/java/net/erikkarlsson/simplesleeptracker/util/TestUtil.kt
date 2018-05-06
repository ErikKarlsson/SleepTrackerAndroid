package net.erikkarlsson.simplesleeptracker.util

import android.support.annotation.StringRes
import android.support.test.InstrumentationRegistry
import net.erikkarlsson.simplesleeptracker.App

object TestUtil {

    fun app(): App {
        return InstrumentationRegistry.getTargetContext().applicationContext as App
    }

    fun getString(@StringRes id: Int): String {
        return app().getString(id)
    }
}