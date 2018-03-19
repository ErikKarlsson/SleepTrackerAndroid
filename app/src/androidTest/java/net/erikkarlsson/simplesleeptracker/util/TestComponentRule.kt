package net.erikkarlsson.simplesleeptracker.util

import android.support.test.InstrumentationRegistry
import net.erikkarlsson.simplesleeptracker.TestableApplication
import net.erikkarlsson.simplesleeptracker.domain.Sleep
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