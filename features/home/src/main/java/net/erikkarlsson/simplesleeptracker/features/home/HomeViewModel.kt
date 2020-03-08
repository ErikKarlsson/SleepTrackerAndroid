package net.erikkarlsson.simplesleeptracker.features.home

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.ViewModelContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.Observable
import io.reactivex.subjects.Subject
import kotlinx.coroutines.launch
import net.erikkarlsson.simplesleeptracker.core.MvRxViewModel
import net.erikkarlsson.simplesleeptracker.core.livedata.Event
import net.erikkarlsson.simplesleeptracker.domain.BUBBLE_DURATION_MILLI
import net.erikkarlsson.simplesleeptracker.domain.WidgetDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.MinimumSleepEvent
import net.erikkarlsson.simplesleeptracker.domain.entity.SleepEvent
import net.erikkarlsson.simplesleeptracker.domain.entity.UserAccount
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import net.erikkarlsson.simplesleeptracker.domain.task.FlowTask.None
import net.erikkarlsson.simplesleeptracker.domain.task.TaskScheduler
import net.erikkarlsson.simplesleeptracker.domain.task.ToggleSleepTask
import net.erikkarlsson.simplesleeptracker.features.home.domain.GetHomeTask
import net.erikkarlsson.simplesleeptracker.features.home.domain.LogoutTask
import java.util.concurrent.TimeUnit
import javax.inject.Named

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
        viewModelScope.launch {
            getHomeTask.flow(None())
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
