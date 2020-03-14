package net.erikkarlsson.simplesleeptracker.features.home

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import net.erikkarlsson.simplesleeptracker.core.MvRxViewModel
import net.erikkarlsson.simplesleeptracker.core.livedata.Event
import net.erikkarlsson.simplesleeptracker.domain.BUBBLE_DURATION_MILLI
import net.erikkarlsson.simplesleeptracker.domain.WidgetDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.MinimumSleepEvent
import net.erikkarlsson.simplesleeptracker.domain.entity.SleepEvent
import net.erikkarlsson.simplesleeptracker.domain.entity.UserAccount
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import net.erikkarlsson.simplesleeptracker.domain.task.CoroutineTask
import net.erikkarlsson.simplesleeptracker.domain.task.FlowTask.None
import net.erikkarlsson.simplesleeptracker.domain.task.TaskScheduler
import net.erikkarlsson.simplesleeptracker.domain.task.ToggleSleepTask
import net.erikkarlsson.simplesleeptracker.features.home.domain.GetHomeTask
import net.erikkarlsson.simplesleeptracker.features.home.domain.LogoutTask
import javax.inject.Named

class HomeViewModel @AssistedInject constructor(
        @Assisted state: HomeState,
        getHomeTask: GetHomeTask,
        private val toggleSleepTask: ToggleSleepTask,
        private val logoutTask: LogoutTask,
        private val widgetDataSource: WidgetDataSource,
        @Named("sleepEvents") private val sleepEvents: BroadcastChannel<SleepEvent>,
        @Named("restoreBackupScheduler") private val restoreBackupScheduler: TaskScheduler,
        @Named("homeEvents") private val homeEvents: HomeEvents
) : MvRxViewModel<HomeState>(state) {

    init {
        viewModelScope.launch {
            getHomeTask.flow(None())
                    .collect {
                        setState {
                            copy(lastBackupTimestamp = it.lastBackupTimestamp,
                                    sleep = it.currentSleep, isLoadingHome = false,
                                    sleepCount = it.sleepCount)
                        }
                    }
        }

        viewModelScope.launch {
            sleepEvents.asFlow()
                    .filter { it is MinimumSleepEvent }
                    .collect {
                        setState { copy(isShowingMinimalSleep = true) }
                        delay(BUBBLE_DURATION_MILLI)
                        setState { copy(isShowingMinimalSleep = false) }
                    }
        }
    }

    fun loadWidgetStatus() {
        val isWidgetAdded = widgetDataSource.isWidgetAdded()
        setState { copy(isWidgetAdded = isWidgetAdded, isLoadingWidgetStatus = false) }
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
        GlobalScope.launch(Dispatchers.IO) {
            toggleSleepTask.completable(CoroutineTask.None())
        }
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
