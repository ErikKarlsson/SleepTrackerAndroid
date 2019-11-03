package net.erikkarlsson.simplesleeptracker.feature.home

import android.os.Build
import androidx.lifecycle.MutableLiveData
import com.airbnb.mvrx.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.Observable
import io.reactivex.subjects.Subject
import net.erikkarlsson.simplesleeptracker.MvRxViewModel
import net.erikkarlsson.simplesleeptracker.base.Event
import net.erikkarlsson.simplesleeptracker.domain.BUBBLE_DURATION_MILLI
import net.erikkarlsson.simplesleeptracker.domain.HIDE_WIDGET_BUBBLE_AFTER_SLEEP_COUNT
import net.erikkarlsson.simplesleeptracker.domain.WidgetDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.entity.UserAccount
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import net.erikkarlsson.simplesleeptracker.domain.task.ObservableTask
import net.erikkarlsson.simplesleeptracker.domain.task.TaskScheduler
import net.erikkarlsson.simplesleeptracker.domain.task.sleep.ToggleSleepTask
import net.erikkarlsson.simplesleeptracker.feature.home.domain.GetHomeTask
import net.erikkarlsson.simplesleeptracker.feature.home.domain.LogoutTask
import java.util.concurrent.TimeUnit
import javax.inject.Named

sealed class HomeEvent // One time event
object PinWidgetEvent : HomeEvent()
object AddWidgetEvent : HomeEvent()

sealed class SleepEvent // One time event
object MinimumSleepEvent : SleepEvent()

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

class HomeViewModel @AssistedInject constructor(
        @Assisted state: HomeState,
        getHomeTask: GetHomeTask,
        private val toggleSleepTask: ToggleSleepTask,
        private val logoutTask: LogoutTask,
        private val widgetDataSource: WidgetDataSource,
        @Named("sleepEvents") private val sleepEvents: Subject<SleepEvent>,
        @Named("restoreBackupScheduler") private val restoreBackupScheduler: TaskScheduler,
        @Named("homeEvents") private val homeEvents: HomeEvents
) : MvRxViewModel<HomeState>(state) {

    init {
        getHomeTask.observable(ObservableTask.None())
                .execute {
                    when (it) {
                        is Success -> {
                            copy(lastBackupTimestamp = it().lastBackupTimestamp,
                                    sleep = it().currentSleep, isLoadingHome = false,
                                    sleepCount = it().sleepCount)
                        }
                        else -> copy()
                    }
                }

        sleepEvents.filter { it is MinimumSleepEvent }
                .doOnNext {
                    setState { copy(isShowingMinimalSleep = true) }
                }
                .switchMap {
                    Observable.timer(BUBBLE_DURATION_MILLI, TimeUnit.MILLISECONDS)
                }
                .execute {
                    when (it) {
                        is Success -> copy(isShowingMinimalSleep = false)
                        else -> copy()
                    }
                }
    }

    fun loadWidgetStatus() {
        widgetDataSource.isWidgetAdded()
                .execute {
                    when (it) {
                        is Success -> copy(isWidgetAdded = it(), isLoadingWidgetStatus = false)
                        else -> copy()
                    }
                }
    }

    fun onBubbleClick() {
        withState {
            when {
                it.bubbleState == BubbleState.PIN_WIDGET -> homeEvents.postValue(Event(PinWidgetEvent))
                it.bubbleState == BubbleState.ADD_WIDGET -> homeEvents.postValue(Event(AddWidgetEvent))
                else -> toggleSleep()
            }
        }
    }

    fun onSignOutComplete() {
        setState { copy(userAccount = null) }

        logoutTask.completable(CompletableTask.None())
                .execute { copy() }
    }

    fun onToggleSleepClick() {
        toggleSleep()
    }

    fun onSignInSuccess(userAccount: UserAccount) {
        setState { copy(userAccount = userAccount) }
        restoreBackupScheduler.schedule()
    }

    fun onSignInRestored(userAccount: UserAccount) {
        setState { copy(userAccount = userAccount) }
    }

    fun onSignInFailed() {
        setState { copy(userAccount = null) }
    }

    private fun toggleSleep() {
        toggleSleepTask.completable(CompletableTask.None())
                .execute { copy() }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(state: HomeState): HomeViewModel
    }

    companion object : MvRxViewModelFactory<HomeViewModel, HomeState> {
        override fun create(viewModelContext: ViewModelContext, state: HomeState): HomeViewModel? {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<HomeFragment>()
            return fragment.viewModelFactory.create(state)
        }
    }

}
