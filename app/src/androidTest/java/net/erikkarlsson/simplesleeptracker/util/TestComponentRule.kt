package net.erikkarlsson.simplesleeptracker.util

import android.app.Activity
import androidx.test.InstrumentationRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import net.erikkarlsson.simplesleeptracker.TestableApplication
import net.erikkarlsson.simplesleeptracker.dateutil.offsetDateTime
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class TestComponentRule() : TestRule {

    val application: TestableApplication

    init {
        application = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestableApplication
    }

    fun mockDateTimeNow(dateTimeIso: String) {
        application.mockDateTimeNow(dateTimeIso.offsetDateTime)
    }

    fun insertSleep(fromDate: String, toDate: String) {
        val sleep = Sleep(fromDate = fromDate.offsetDateTime, toDate = toDate.offsetDateTime)
        application.insertSleep(sleep)
    }

    fun getCurrentActivity(): Activity? {
        var currentActivity: Activity? = null
        getInstrumentation().runOnMainSync { run { currentActivity = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED).elementAtOrNull(0) } }
        return currentActivity
    }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                application.reInitDependencyInjection()
                application.clearDataBetweenTests()
                base.evaluate()
            }
        }
    }
}
