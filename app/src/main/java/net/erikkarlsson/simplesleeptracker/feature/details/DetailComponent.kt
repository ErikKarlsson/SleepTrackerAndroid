package net.erikkarlsson.simplesleeptracker.feature.details

import io.reactivex.Observable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.elm.*
import javax.inject.Inject

class DetailComponent @Inject constructor(private val sleepSubscription: SleepSubscription)
    : Component<DetailState, DetailMsg, DetailCmd> {

    override fun call(cmd: DetailCmd): Single<DetailMsg> {
        return Single.just(NoOp)
    }

    override fun initState(): DetailState = DetailState.empty()

    override fun subscriptions(): List<Sub<DetailState, DetailMsg>> = listOf(sleepSubscription)

    override fun update(msg: DetailMsg, prevState: DetailState): Pair<DetailState, DetailCmd?> = when (msg) {
        NoOp -> prevState.noCmd()
        is DetailLoaded -> prevState.copy(sleep = msg.sleep).noCmd()
        is LoadDetailIntent -> prevState.copy(sleepId = msg.sleepId).noCmd()
    }

}

// State
data class DetailState(val sleepId: Int, val sleep: Sleep) : State {

    companion object {
        fun empty() = DetailState(0, Sleep.empty())
    }
}

// Subscription
class SleepSubscription @Inject constructor(private val sleepRepository: SleepDataSource) : StatefulSub<DetailState, DetailMsg>() {
    override fun invoke(state: DetailState): Observable<DetailMsg> {
        return if (state.sleepId == 0) {
            Observable.empty()
        } else {
            sleepRepository.getSleep(state.sleepId).map { DetailLoaded(it) }
        }
    }

    override fun isDistinct(s1: DetailState, s2: DetailState): Boolean =
            s1.sleep.id == s2.sleep.id
}

// Msg
sealed class DetailMsg : Msg

data class LoadDetailIntent(val sleepId: Int) : DetailMsg()
data class DetailLoaded(val sleep: Sleep) : DetailMsg()
object NoOp : DetailMsg()

// Cmd
sealed class DetailCmd : Cmd
