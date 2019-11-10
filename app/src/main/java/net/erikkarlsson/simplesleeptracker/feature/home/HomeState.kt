package net.erikkarlsson.simplesleeptracker.feature.home

import android.os.Build
import androidx.lifecycle.MutableLiveData
import com.airbnb.mvrx.MvRxState
import net.erikkarlsson.simplesleeptracker.base.Event
import net.erikkarlsson.simplesleeptracker.domain.HIDE_WIDGET_BUBBLE_AFTER_SLEEP_COUNT
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.entity.UserAccount

sealed class HomeEvent // One time event
object PinWidgetEvent : HomeEvent()
object AddWidgetEvent : HomeEvent()

typealias HomeEvents = MutableLiveData<Event<HomeEvent>>

enum class BubbleState {
    SLEEPING, SLEEPING_ONBOARDING, PIN_WIDGET, ADD_WIDGET, START_TRACKING,
    MINIMUM_SLEEP, SLEEP_DURATION, EMPTY
}

data class HomeState(
        val isLoadingHome: Boolean = true,
        val isLoadingWidgetStatus: Boolean = true,
        val isShowingMinimalSleep: Boolean = false,
        val sleep: Sleep = Sleep.empty(),
        val sleepCount: Int = -1,
        val userAccount: UserAccount? = null,
        val lastBackupTimestamp: Long = -1,
        val isWidgetAdded: Boolean = true
) : MvRxState {

    val isLoading = isLoadingHome || isLoadingWidgetStatus

    val isSleeping: Boolean
        get() = sleep.isSleeping

    val isLoggedIn: Boolean
        get() = userAccount != null

    val sleepDuration: Float
        get() = sleep.hours

    private val isPinWidgetSupported: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    val bubbleState: BubbleState
        get() =
            when {
                isSleeping && sleepCount < 2 -> BubbleState.SLEEPING_ONBOARDING
                isSleeping -> BubbleState.SLEEPING
                isShowingMinimalSleep -> BubbleState.MINIMUM_SLEEP
                !isSleeping && !isWidgetAdded && isPinWidgetSupported && sleepCount < HIDE_WIDGET_BUBBLE_AFTER_SLEEP_COUNT -> BubbleState.PIN_WIDGET
                !isSleeping && !isWidgetAdded && sleepCount < HIDE_WIDGET_BUBBLE_AFTER_SLEEP_COUNT -> BubbleState.ADD_WIDGET
                !isSleeping && sleep == Sleep.empty() -> BubbleState.START_TRACKING
                !isSleeping -> BubbleState.SLEEP_DURATION
                else -> BubbleState.EMPTY
            }
}
