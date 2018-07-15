package net.erikkarlsson.simplesleeptracker.feature.profile

import io.reactivex.Observable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.entity.Profile
import net.erikkarlsson.simplesleeptracker.domain.entity.UserAccount
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import net.erikkarlsson.simplesleeptracker.domain.task.ObservableTask
import net.erikkarlsson.simplesleeptracker.domain.task.backup.ScheduleRestoreBackupTask
import net.erikkarlsson.simplesleeptracker.domain.task.profile.GetProfileTask
import net.erikkarlsson.simplesleeptracker.domain.task.profile.LogoutTask
import net.erikkarlsson.simplesleeptracker.elm.Cmd
import net.erikkarlsson.simplesleeptracker.elm.Component
import net.erikkarlsson.simplesleeptracker.elm.Msg
import net.erikkarlsson.simplesleeptracker.elm.State
import net.erikkarlsson.simplesleeptracker.elm.StatelessSub
import net.erikkarlsson.simplesleeptracker.elm.Sub
import javax.inject.Inject

class ProfileComponent @Inject constructor(
        private val scheduleRestoreBackupTask: ScheduleRestoreBackupTask,
        private val logoutTask: LogoutTask,
        private val profileSubscription: ProfileSubscription)
    : Component<ProfileState, ProfileMsg, ProfileCmd> {

    override fun call(cmd: ProfileCmd): Single<ProfileMsg> = when (cmd) {
        RestoreBackup -> executeRestoreBackupTask()
        Logout -> executeLogoutTask()
    }

    override fun initState(): ProfileState = ProfileState.empty()

    override fun subscriptions(): List<Sub<ProfileState, ProfileMsg>> = listOf(profileSubscription)

    override fun update(msg: ProfileMsg, prevState: ProfileState): Pair<ProfileState, ProfileCmd?> =
            when (msg) {
                NoOp -> prevState.noCmd()
                SignInCancelled -> prevState.copy(null).noCmd()
                SignInFailed -> prevState.copy(null).noCmd()
                SignOutComplete -> prevState.copy(null) withCmd Logout
                is SignInSuccess -> prevState.copy(userAccount = msg.userAccount) withCmd RestoreBackup
                is ProfileLoaded -> prevState.copy(profile = msg.profile).noCmd()
                is SignInRestored -> prevState.copy(userAccount = msg.userAccount).noCmd()
            }

    private fun executeLogoutTask(): Single<ProfileMsg> =
            logoutTask.execute(CompletableTask.None()).toSingleDefault(NoOp)

    private fun executeRestoreBackupTask(): Single<ProfileMsg> =
            scheduleRestoreBackupTask.execute(CompletableTask.None()).toSingleDefault(NoOp)
}

// State
data class ProfileState(val userAccount: UserAccount?, val profile: Profile) : State {

    val isLoggedIn: Boolean
        get() = userAccount != null

    companion object {
        fun empty() = ProfileState(null, Profile.empty())
    }
}

// Subscription
class ProfileSubscription @Inject constructor(private val getProfileTask: GetProfileTask)
    : StatelessSub<ProfileState, ProfileMsg>() {

    override fun invoke(): Observable<ProfileMsg> =
            getProfileTask.execute(ObservableTask.None())
                    .map { ProfileLoaded(it) }
}

// Msg
sealed class ProfileMsg : Msg

object NoOp : ProfileMsg()

data class ProfileLoaded(val profile: Profile) : ProfileMsg()
data class SignInSuccess(val userAccount: UserAccount) : ProfileMsg()
data class SignInRestored(val userAccount: UserAccount) : ProfileMsg()
object SignInCancelled : ProfileMsg()
object SignInFailed : ProfileMsg()
object SignOutComplete : ProfileMsg()

// Cmd
sealed class ProfileCmd : Cmd

object RestoreBackup : ProfileCmd()
object Logout : ProfileCmd()
