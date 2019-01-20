package net.erikkarlsson.simplesleeptracker.feature.home

import android.os.Build
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.Subject
import net.erikkarlsson.simplesleeptracker.base.Event
import net.erikkarlsson.simplesleeptracker.domain.*
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.entity.UserAccount
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import net.erikkarlsson.simplesleeptracker.domain.task.sleep.ToggleSleepTask
import net.erikkarlsson.simplesleeptracker.elm.*
import net.erikkarlsson.simplesleeptracker.feature.backup.domain.ScheduleRestoreBackupTask
import net.erikkarlsson.simplesleeptracker.feature.home.domain.LogoutTask
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class HomeComponent @Inject constructor(
        private val toggleSleepTask: ToggleSleepTask,
        private val scheduleRestoreBackupTask: ScheduleRestoreBackupTask,
        private val logoutTask: LogoutTask,
        private val homeSubscription: HomeSubscription,
        private val eventSubscription: EventSubscription,
        private val widgetDataSource: WidgetDataSource,
        @Named("homeEvents") private val homeEvents: HomeEvents)
    : Component<HomeState, HomeMsg, HomeCmd> {

    override fun call(cmd: HomeCmd): Single<HomeMsg> = when (cmd) {
        RestoreBackup -> executeRestoreBackupTask()
        Logout -> executeLogoutTask()
        ToggleSleepCmd -> toggleSleepTask.execute(CompletableTask.None()).toSingleDefault(net.erikkarlsson.simplesleeptracker.feature.home.DoNothing)
        LoadWidgetStatusCmd -> widgetDataSource.isWidgetAdded().map { WidgetStatusLoaded(it) }
        PinWidgetCmd -> {
            homeEvents.postValue(Event(PinWidgetEvent))
            Single.just(DoNothing)
        }
        AddWidgetCmd -> {
            homeEvents.postValue(Event(AddWidgetEvent))
            Single.just(DoNothing)
        }
    }

    override fun initState(): HomeState = HomeState.empty()

    override fun subscriptions(): List<Sub<HomeState, HomeMsg>> = listOf(homeSubscription, eventSubscription)

    override fun update(msg: HomeMsg, prevState: HomeState): Pair<HomeState, HomeCmd?> =
            when (msg) {
                DoNothing -> prevState.noCmd()
                BubbleClick -> onBubbleClick(prevState)
                ToggleSleepClicked -> prevState withCmd ToggleSleepCmd
                is CurrentSleepLoaded -> prevState.copy(sleep = msg.sleep).noCmd()
                SignInCancelled -> prevState.copy(userAccount = null).noCmd()
                SignInFailed -> prevState.copy(userAccount = null).noCmd()
                SignOutComplete -> prevState.copy(userAccount = null) withCmd Logout
                is SignInSuccess -> prevState.copy(userAccount = msg.userAccount) withCmd RestoreBackup
                is HomeLoaded -> prevState.copy(lastBackupTimestamp = msg.lastBackupTimestamp,
                        sleep = msg.currentSleep, isLoadingHome = false, sleepCount = msg.sleepCount).noCmd()
                is SignInRestored -> prevState.copy(userAccount = msg.userAccount).noCmd()
                LoadWidgetStatus -> prevState.withCmd(LoadWidgetStatusCmd)
                is WidgetStatusLoaded -> prevState.copy(isWidgetAdded = msg.isWidgetAdded,
                        isLoadingWidgetStatus = false).noCmd()
                ShowMinimumSleep -> prevState.copy(isShowingMinimalSleep = true).noCmd()
                HideMinimumSleep -> prevState.copy(isShowingMinimalSleep = false).noCmd()
            }

    private fun onBubbleClick(prevState: HomeState): Pair<HomeState, HomeCmd?> =
            when {
                prevState.bubbleState == BubbleState.PIN_WIDGET -> prevState withCmd PinWidgetCmd
                prevState.bubbleState == BubbleState.ADD_WIDGET -> prevState withCmd AddWidgetCmd
                else -> prevState withCmd ToggleSleepCmd
            }

    private fun executeLogoutTask(): Single<HomeMsg> =
            logoutTask.execute(CompletableTask.None()).toSingleDefault(DoNothing)

    private fun executeRestoreBackupTask(): Single<HomeMsg> =
            scheduleRestoreBackupTask.execute(CompletableTask.None()).toSingleDefault(DoNothing)
}

// State
data class HomeState(val isLoadingHome: Boolean,
                     val isLoadingWidgetStatus: Boolean,
                     val isShowingMinimalSleep: Boolean,
                     val sleep: Sleep,
                     val sleepCount: Int,
                     val userAccount: UserAccount?,
                     val lastBackupTimestamp: Long,
                     val isWidgetAdded: Boolean) : State {

    val isLoading = isLoadingHome || isLoadingWidgetStatus

    val isSleeping: Boolean
        get() = sleep.isSleeping

    val isLoggedIn: Boolean
        get() = userAccount != null

    val sleepDuration: Float
        get() = sleep.hours

    val isPinWidgetSupported: Boolean
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

    companion object {
        fun empty() = HomeState(true, true, false,
                Sleep.empty(), -1, null, -1, true)
    }
}

enum class BubbleState { SLEEPING, SLEEPING_ONBOARDING, PIN_WIDGET, ADD_WIDGET, START_TRACKING,
    MINIMUM_SLEEP, SLEEP_DURATION, EMPTY
}

// Subscription
class HomeSubscription @Inject constructor(private val backupDataSource: FileBackupDataSource,
                                           private val sleepRepository: SleepDataSource)
    : StatelessSub<HomeState, HomeMsg>() {

    override fun invoke(): Observable<HomeMsg> {
        return Observables.combineLatest(
                backupDataSource.getLastBackupTimestamp(),
                sleepRepository.getCurrent(),
                sleepRepository.getCount())
        { lastBackupTimestamp, currentSleep, sleepCount ->
            HomeLoaded(lastBackupTimestamp, currentSleep, sleepCount)
        }
    }
}

class EventSubscription @Inject constructor(@Named("sleepEvents") private val sleepEvents: Subject<SleepEvent>)
    : StatelessSub<HomeState, HomeMsg>() {

    override fun invoke(): Observable<HomeMsg> =
            sleepEvents.filter { it is MinimumSleepEvent }
                    .switchMap { sleepEvent ->
                        Observable.concat(
                                Observable.just(ShowMinimumSleep),
                                Observable.timer(BUBBLE_DURATION_MILLI,
                                        TimeUnit.MILLISECONDS).map { HideMinimumSleep })
                    }
}

// Msg
sealed class HomeMsg : Msg

object ToggleSleepClicked : HomeMsg()
object BubbleClick : HomeMsg()
data class CurrentSleepLoaded(val sleep: Sleep) : HomeMsg()
data class HomeLoaded(val lastBackupTimestamp: Long,
                      val currentSleep: Sleep,
                      val sleepCount: Int) : HomeMsg()

data class SignInSuccess(val userAccount: UserAccount) : HomeMsg()
data class SignInRestored(val userAccount: UserAccount) : HomeMsg()
object SignInCancelled : HomeMsg()
object SignInFailed : HomeMsg()
object SignOutComplete : HomeMsg()
object LoadWidgetStatus : HomeMsg()
object ShowMinimumSleep : HomeMsg()
object HideMinimumSleep : HomeMsg()
data class WidgetStatusLoaded(val isWidgetAdded: Boolean) : HomeMsg()
object DoNothing : HomeMsg()

// One time events
sealed class HomeEvent

object PinWidgetEvent : HomeEvent()
object AddWidgetEvent : HomeEvent()

sealed class SleepEvent
object MinimumSleepEvent : SleepEvent()

// Cmd
sealed class HomeCmd : Cmd

object RestoreBackup : HomeCmd()
object Logout : HomeCmd()
object ToggleSleepCmd : HomeCmd()
object LoadWidgetStatusCmd : HomeCmd()
object PinWidgetCmd : HomeCmd()
object AddWidgetCmd : HomeCmd()

// Alias
typealias HomeEvents = MutableLiveData<Event<HomeEvent>>

