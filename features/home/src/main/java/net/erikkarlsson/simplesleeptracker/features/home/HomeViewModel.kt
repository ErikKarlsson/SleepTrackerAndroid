package net.erikkarlsson.simplesleeptracker.features.home

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import net.erikkarlsson.simplesleeptracker.core.ReduxViewModel
import net.erikkarlsson.simplesleeptracker.core.livedata.Event
import net.erikkarlsson.simplesleeptracker.domain.BUBBLE_DURATION_MILLI
import net.erikkarlsson.simplesleeptracker.domain.WidgetDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.MinimumSleepEvent
import net.erikkarlsson.simplesleeptracker.domain.entity.SleepEvent
import net.erikkarlsson.simplesleeptracker.domain.entity.UserAccount
import net.erikkarlsson.simplesleeptracker.domain.task.CoroutineTask
import net.erikkarlsson.simplesleeptracker.domain.task.FlowTask.None
import net.erikkarlsson.simplesleeptracker.domain.task.TaskScheduler
import net.erikkarlsson.simplesleeptracker.domain.task.ToggleSleepTask
import net.erikkarlsson.simplesleeptracker.features.home.domain.GetHomeTask
import net.erikkarlsson.simplesleeptracker.features.home.domain.LogoutTask
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class HomeViewModel @Inject constructor(
        getHomeTask: GetHomeTask,
        private val toggleSleepTask: ToggleSleepTask,
        private val logoutTask: LogoutTask,
        private val widgetDataSource: WidgetDataSource,
        @Named("sleepEvents") private val sleepEvents: BroadcastChannel<SleepEvent>,
        @Named("restoreBackupScheduler") private val restoreBackupScheduler: TaskScheduler,
        @Named("homeEvents") private val homeEvents: HomeEvents
) : ReduxViewModel<HomeState>(HomeState()) {

    init {
        viewModelScope.launch {
            getHomeTask.flow(None())
                    .collectAndSetState {
                        copy(lastBackupTimestamp = it.lastBackupTimestamp,
                                sleep = it.currentSleep, isLoadingHome = false,
                                sleepCount = it.sleepCount)
                    }
        }

        viewModelScope.launch {
            sleepEvents.asFlow()
                    .filter { it is MinimumSleepEvent }
                    .collect {
                        viewModelScope.launchSetState { copy(isShowingMinimalSleep = true) }
                        delay(BUBBLE_DURATION_MILLI)
                        viewModelScope.launchSetState { copy(isShowingMinimalSleep = false) }
                    }
        }
    }

    fun loadWidgetStatus() {
        viewModelScope.launchSetState {
            val isWidgetAdded = widgetDataSource.isWidgetAdded()
            copy(isWidgetAdded = isWidgetAdded, isLoadingWidgetStatus = false)
        }
    }

    fun onBubbleClick() {
        viewModelScope.withState {
            when {
                it.bubbleState == BubbleState.PIN_WIDGET -> homeEvents.postValue(Event(PinWidgetEvent))
                it.bubbleState == BubbleState.ADD_WIDGET -> homeEvents.postValue(Event(AddWidgetEvent))
                else -> toggleSleep()
            }
        }
    }

    fun onSignOutComplete() {
        viewModelScope.launchSetState {
            copy(userAccount = null)
        }

        viewModelScope.launch {
            logoutTask.completable(CoroutineTask.None())
        }
    }

    fun onToggleSleepClick() {
        toggleSleep()
    }

    fun onSignInSuccess(userAccount: UserAccount) {
        viewModelScope.launchSetState {
            copy(userAccount = userAccount)
        }
        restoreBackupScheduler.schedule()
    }

    fun onSignInRestored(userAccount: UserAccount) {
        viewModelScope.launchSetState {
            copy(userAccount = userAccount)
        }
    }

    fun onSignInFailed() {
        viewModelScope.launchSetState {
            copy(userAccount = null)
        }
    }

    private fun toggleSleep() {
        viewModelScope.launch {
            toggleSleepTask.completable(CoroutineTask.None())
        }
    }
}
