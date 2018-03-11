package net.erikkarlsson.simplesleeptracker

import android.support.test.InstrumentationRegistry
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

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                try {
                    application.reInitDependencyInjection()
                    base.evaluate()
                } finally {
                    application.clearDataBetweenTests()
                }
            }
        }
    }
}