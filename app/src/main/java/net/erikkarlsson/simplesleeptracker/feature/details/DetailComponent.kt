package net.erikkarlsson.simplesleeptracker.feature.details

import com.google.common.collect.ImmutableList
import io.reactivex.Observable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.elm.*
import javax.inject.Inject

class DetailComponent @Inject constructor()
    : Component<DetailState, DetailMsg, DetailCmd> {

    override fun call(cmd: DetailCmd): Single<DetailMsg> {
        return Single.just(NoOp)
    }

    override fun initState(): DetailState = DetailState.empty()

    override fun subscriptions(): List<Sub<DetailState, DetailMsg>> = listOf()

    override fun update(msg: DetailMsg, prevState: DetailState): Pair<DetailState, DetailCmd?> = when (msg) {
        NoOp -> prevState.noCmd()
        is SleepLoaded -> prevState.copy(sleepList = msg.sleepList).noCmd()
    }

}

// State
data class DetailState(val sleepList: ImmutableList<Sleep>) : State {

    companion object {
        fun empty() = DetailState(ImmutableList.of())
    }
}

// Subscription
class SleepSubscription @Inject constructor(private val sleepRepository: SleepDataSource) : StatelessSub<DetailState, DetailMsg>() {

    override fun invoke(): Observable<DetailMsg> =
            sleepRepository.getSleep().map { SleepLoaded(it) }
}

// Msg
sealed class DetailMsg : Msg

data class SleepLoaded(val sleepList: ImmutableList<Sleep>) : DetailMsg()
object NoOp : DetailMsg()

// Cmd
sealed class DetailCmd : Cmd
