package net.erikkarlsson.simplesleeptracker.feature.mvrx

import com.airbnb.mvrx.test.MvRxTestRule
import com.airbnb.mvrx.withState
import junit.framework.Assert.assertEquals
import org.junit.ClassRule
import org.junit.Test

class MvRxTest {

    companion object {
        @JvmField
        @ClassRule
        val mvrxTestRule = MvRxTestRule()
    }

    @Test
    fun name() {
        val initialState = HelloWorldState(userId = "123")
        val viewModel = HelloWorldViewModel(initialState)

        withState(viewModel) { state ->
            assertEquals(initialState, state)
        }

    }

    @Test
    fun nameCounter() {
        val initialState = HelloWorldState(userId = "123")
        val viewModel = HelloWorldViewModel(initialState)
        viewModel.incrementCount()

        withState(viewModel) { state ->
            assertEquals(1, state.count)
        }

    }
}
