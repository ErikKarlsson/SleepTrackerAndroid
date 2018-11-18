package net.erikkarlsson.simplesleeptracker.feature.home

import io.reactivex.Observable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.PREFS_HAS_TOGGLED_SLEEP
import net.erikkarlsson.simplesleeptracker.domain.PreferencesDataSource
import net.erikkarlsson.simplesleeptracker.domain.WidgetDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Profile
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.entity.UserAccount
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import net.erikkarlsson.simplesleeptracker.domain.task.ObservableTask
import net.erikkarlsson.simplesleeptracker.domain.task.sleep.GetCurrentSleepTask
import net.erikkarlsson.simplesleeptracker.domain.task.sleep.ToggleSleepTask
import net.erikkarlsson.simplesleeptracker.elm.*
import net.erikkarlsson.simplesleeptracker.feature.backup.domain.ScheduleRestoreBackupTask
import net.erikkarlsson.simplesleeptracker.feature.home.domain.GetProfileTask
import net.erikkarlsson.simplesleeptracker.feature.home.domain.LogoutTask
import javax.inject.Inject

class HomeComponent @Inject constructor(
        private val toggleSleepTask: ToggleSleepTask,
        private val scheduleRestoreBackupTask: ScheduleRestoreBackupTask,
        private val logoutTask: LogoutTask,
        private val profileSubscription: ProfileSubscription,
        private val sleepSubscription: SleepSubscription,
        private val preferenceSubscription: PreferenceSubscription,
        private val widgetDataSource: WidgetDataSource)
    : Component<HomeState, HomeMsg, HomeCmd> {

    override fun call(cmd: HomeCmd): Single<HomeMsg> = when (cmd) {
        RestoreBackup -> executeRestoreBackupTask()
        Logout -> executeLogoutTask()
        ToggleSleepCmd -> toggleSleepTask.execute(CompletableTask.None()).toSingleDefault(NoOp)
        LoadWidgetStatusCmd -> widgetDataSource.isWidgetAdded().map { WidgetStatusLoaded(it) }
    }

    override fun initState(): HomeState = HomeState.empty()

    override fun subscriptions(): List<Sub<HomeState, HomeMsg>> = listOf(profileSubscription,
            sleepSubscription, preferenceSubscription)

    override fun update(msg: HomeMsg, prevState: HomeState): Pair<HomeState, HomeCmd?> =
            when (msg) {
                NoOp -> prevState.noCmd()
                ToggleSleepClicked -> prevState withCmd ToggleSleepCmd
                is CurrentSleepLoaded -> prevState.copy(sleep = msg.sleep).noCmd()
                SignInCancelled -> prevState.copy(userAccount = null).noCmd()
                SignInFailed -> prevState.copy(userAccount = null).noCmd()
                SignOutComplete -> prevState.copy(userAccount = null) withCmd Logout
                is SignInSuccess -> prevState.copy(userAccount = msg.userAccount) withCmd RestoreBackup
                is ProfileLoaded -> prevState.copy(profile = msg.profile).noCmd()
                is SignInRestored -> prevState.copy(userAccount = msg.userAccount).noCmd()
                LoadWidgetStatus -> prevState.withCmd(LoadWidgetStatusCmd)
                is WidgetStatusLoaded -> prevState.copy(isWidgetAdded = msg.isWidgetAdded).noCmd()
                is PrefsLoaded -> prevState.copy(hasToggledSleep = msg.hasToggledSleep).noCmd()
            }

    private fun executeLogoutTask(): Single<HomeMsg> =
            logoutTask.execute(CompletableTask.None()).toSingleDefault(NoOp)

    private fun executeRestoreBackupTask(): Single<HomeMsg> =
            scheduleRestoreBackupTask.execute(CompletableTask.None()).toSingleDefault(NoOp)
}

// State
data class HomeState(val sleep: Sleep,
                     val hasToggledSleep: Boolean,
                     val userAccount: UserAccount?,
                     val profile: Profile,
                     val isWidgetAdded: Boolean) : State {

    val isSleeping: Boolean
        get() = sleep.isSleeping

    val isLoggedIn: Boolean
        get() = userAccount != null

    val sleepDuration: Float
        get() = sleep.hours

    val bubbleState: BubbleState
        get() =
            when {
                isSleeping -> BubbleState.SLEEPING
                !isSleeping && !isWidgetAdded -> BubbleState.ADD_TO_HOME
                !isSleeping && !hasToggledSleep -> BubbleState.START_TRACKING
                !isSleeping && sleep == Sleep.empty() -> BubbleState.MINIMUM_SLEEP
                !isSleeping -> BubbleState.SLEEP_DURATION
                else -> BubbleState.EMPTY
            }

    companion object {
        fun empty() = HomeState(Sleep.empty(), true, null,
                Profile.empty(), true)
    }
}

enum class BubbleState { SLEEPING, ADD_TO_HOME, START_TRACKING, MINIMUM_SLEEP, SLEEP_DURATION, EMPTY }

// Subscription
class ProfileSubscription @Inject constructor(private val getProfileTask: GetProfileTask)
    : StatelessSub<HomeState, HomeMsg>() {

    override fun invoke(): Observable<HomeMsg> =
            getProfileTask.execute(ObservableTask.None())
                    .map { ProfileLoaded(it) }
}

class SleepSubscription @Inject constructor(private val getCurrentSleepTask: GetCurrentSleepTask)
    : StatelessSub<HomeState, HomeMsg>() {

    override fun invoke(): Observable<HomeMsg> =
            getCurrentSleepTask.execute(ObservableTask.None())
                    .map { CurrentSleepLoaded(it) }
}

class PreferenceSubscription @Inject constructor(private val preferencesDataSource: PreferencesDataSource)
    : StatelessSub<HomeState, HomeMsg>() {

    override fun invoke(): Observable<HomeMsg> =
            preferencesDataSource.getBoolean(PREFS_HAS_TOGGLED_SLEEP)
                    .map { PrefsLoaded(it) }
}

// Msg
sealed class HomeMsg : Msg

object ToggleSleepClicked : HomeMsg()
data class CurrentSleepLoaded(val sleep: Sleep) : HomeMsg()
data class ProfileLoaded(val profile: Profile) : HomeMsg()
data class SignInSuccess(val userAccount: UserAccount) : HomeMsg()
data class SignInRestored(val userAccount: UserAccount) : HomeMsg()
object SignInCancelled : HomeMsg()
object SignInFailed : HomeMsg()
object SignOutComplete : HomeMsg()
object LoadWidgetStatus: HomeMsg()
data class WidgetStatusLoaded(val isWidgetAdded: Boolean): HomeMsg()
data class PrefsLoaded(val hasToggledSleep: Boolean): HomeMsg()
object NoOp : HomeMsg()

// Cmd
sealed class HomeCmd : Cmd

object RestoreBackup : HomeCmd()
object Logout : HomeCmd()
object ToggleSleepCmd : HomeCmd()
object LoadWidgetStatusCmd : HomeCmd()

