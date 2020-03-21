package net.erikkarlsson.simplesleeptracker.testutil

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import net.erikkarlsson.simplesleeptracker.core.DispatcherProvider
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class TestCoroutineRule : TestRule {
    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)

    val testDispatcherProvider = object : DispatcherProvider {
        override fun default(): CoroutineDispatcher = testCoroutineDispatcher
        override fun io(): CoroutineDispatcher = testCoroutineDispatcher
        override fun main(): CoroutineDispatcher = testCoroutineDispatcher
        override fun unconfined(): CoroutineDispatcher = testCoroutineDispatcher
    }

    override fun apply(base: Statement, description: Description?) = object : Statement() {
        @Throws(Throwable::class)
        override fun evaluate() {
            Dispatchers.setMain(testCoroutineDispatcher)

            base.evaluate()

            Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
            testCoroutineScope.cleanupTestCoroutines()
        }
    }

    fun runBlockingTest(block: suspend TestCoroutineScope.() -> Unit) =
        testCoroutineScope.runBlockingTest { block() }
}
