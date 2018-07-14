package net.erikkarlsson.simplesleeptracker.feature.profile

import io.reactivex.Completable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.task.TaskScheduler
import net.erikkarlsson.simplesleeptracker.elm.Cmd
import net.erikkarlsson.simplesleeptracker.elm.Component
import net.erikkarlsson.simplesleeptracker.elm.Msg
import net.erikkarlsson.simplesleeptracker.elm.State
import net.erikkarlsson.simplesleeptracker.elm.Sub
import javax.inject.Inject
import javax.inject.Named

class ProfileComponent @Inject constructor(@Named("restoreBackupScheduler") private val restoreBackupScheduler: TaskScheduler)
    : Component<ProfileState, ProfileMsg, ProfileCmd> {

    override fun call(cmd: ProfileCmd): Single<ProfileMsg> = when (cmd) {
        RestoreBackup -> Completable.fromCallable(restoreBackupScheduler::schedule).toSingleDefault(NoOp)
    }

    override fun initState(): ProfileState = ProfileState.empty()

    override fun subscriptions(): List<Sub<ProfileState, ProfileMsg>> = listOf()

    override fun update(msg: ProfileMsg, prevState: ProfileState): Pair<ProfileState, ProfileCmd?> = when (msg) {
        NoOp -> prevState.noCmd()
        SignInCancelled -> prevState.copy(null).noCmd()
        SignInFailed -> prevState.copy(null).noCmd()
        is SignInSuccess -> prevState.copy(userAccount = msg.userAccount) withCmd RestoreBackup
        SignOutComplete -> prevState.copy(null).noCmd()
    }
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
object SignOutComplete: ProfileMsg()

// Cmd
sealed class ProfileCmd : Cmd

object RestoreBackup : ProfileCmd()
