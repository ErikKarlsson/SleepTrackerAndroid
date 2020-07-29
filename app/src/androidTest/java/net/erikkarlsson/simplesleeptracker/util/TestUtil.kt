package net.erikkarlsson.simplesleeptracker.util

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.erikkarlsson.simplesleeptracker.App
import net.erikkarlsson.simplesleeptracker.data.sleep.SleepRepository
import net.erikkarlsson.simplesleeptracker.dateutil.offsetDateTime
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.testutil.MockDateTimeProvider
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

class TestUtil @Inject constructor(private val sleepRepository: SleepRepository,
                                   private val dateTimeProvider: MockDateTimeProvider) {

    fun initTest() {
        clearDataBetweenTests()
        initWorkManager()
    }

    private fun clearDataBetweenTests() {
        GlobalScope.launch {
            sleepRepository.deleteAll()
        }
    }

    private fun initWorkManager() {
        val config = Configuration.Builder()
                // Set log level to Log.DEBUG to make it easier to debug
                .setMinimumLoggingLevel(Log.DEBUG)
                // Use a SynchronousExecutor here to make it easier to write tests
                .setExecutor(SynchronousExecutor())
                .build()

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(context(), config)
    }

    fun insertSleep(fromDate: String, toDate: String) {
        val sleep = Sleep(fromDate = fromDate.offsetDateTime, toDate = toDate.offsetDateTime)
        insertSleep(sleep)
    }

    private fun insertSleep(sleep: Sleep) {
        GlobalScope.launch {
            sleepRepository.insert(sleep)
        }
    }

    fun mockDateTimeNow(dateTimeIso: String) {
        mockDateTimeNow(dateTimeIso.offsetDateTime)
    }

    private fun mockDateTimeNow(offsetDateTime: OffsetDateTime) {
        dateTimeProvider.nowValue = offsetDateTime
    }

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
