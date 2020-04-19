package net.erikkarlsson.simplesleeptracker.util

import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.test.platform.app.InstrumentationRegistry
import net.erikkarlsson.simplesleeptracker.App

object TestUtil {

    fun app(): App {
        return InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as App
    }

    fun context(): Context {
        return InstrumentationRegistry.getInstrumentation().targetContext
    }

    fun getString(@StringRes id: Int): String {
        return app().getString(id)
    }

    fun getInflater(): LayoutInflater {
        return LayoutInflater.from(context())
    }
}
