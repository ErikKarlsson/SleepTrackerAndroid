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

    fun setMockNetwork(connected: Boolean) {

    }

    fun insertSleep(sleep: Sleep) {
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