package net.erikkarlsson.simplesleeptracker.feature.profile

import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import net.erikkarlsson.simplesleeptracker.domain.task.backup.ScheduleBackupTask
import net.erikkarlsson.simplesleeptracker.domain.task.profile.LogoutTask
import net.erikkarlsson.simplesleeptracker.elm.Cmd
import net.erikkarlsson.simplesleeptracker.elm.Component
import net.erikkarlsson.simplesleeptracker.elm.Msg
import net.erikkarlsson.simplesleeptracker.elm.State
import net.erikkarlsson.simplesleeptracker.elm.Sub
import javax.inject.Inject

class ProfileComponent @Inject constructor(
        private val scheduleBackupTask: ScheduleBackupTask,
        private val logoutTask: LogoutTask)
    : Component<ProfileState, ProfileMsg, ProfileCmd> {

    override fun call(cmd: ProfileCmd): Single<ProfileMsg> = when (cmd) {
        RestoreBackup -> executeRestoreBackupTask()
        Logout -> executeLogoutTask()
    }

    override fun initState(): ProfileState = ProfileState.empty()

    override fun subscriptions(): List<Sub<ProfileState, ProfileMsg>> = listOf()

    override fun update(msg: ProfileMsg, prevState: ProfileState): Pair<ProfileState, ProfileCmd?> =
            when (msg) {
                NoOp -> prevState.noCmd()
                SignInCancelled -> prevState.copy(null).noCmd()
                SignInFailed -> prevState.copy(null).noCmd()
                is SignInSuccess -> prevState.copy(userAccount = msg.userAccount) withCmd RestoreBackup
                SignOutComplete -> prevState.copy(null) withCmd Logout
            }

    private fun executeLogoutTask(): Single<ProfileMsg> =
            logoutTask.execute(CompletableTask.None()).toSingleDefault(NoOp)

    private fun executeRestoreBackupTask(): Single<ProfileMsg> =
            scheduleBackupTask.execute(CompletableTask.None()).toSingleDefault(NoOp)
}

// State
data class ProfileState(val userAccount: UserAccount?) : State {

    val isLoggedIn: Boolean
        get() = userAccount != null

    companion object {
        fun empty() = ProfileState(null)
    }
}

// Subscription

// Msg
sealed class ProfileMsg : Msg

object NoOp : ProfileMsg()

data class SignInSuccess(val userAccount: UserAccount) : ProfileMsg()
object SignInCancelled : ProfileMsg()
object SignInFailed : ProfileMsg()
object SignOutComplete : ProfileMsg()

// Cmd
sealed class ProfileCmd : Cmd

object RestoreBackup : ProfileCmd()
object Logout : ProfileCmd()
