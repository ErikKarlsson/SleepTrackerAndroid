package net.erikkarlsson.simplesleeptracker.feature.diary

import android.arch.paging.PagedList
import io.reactivex.Observable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.elm.*
import javax.inject.Inject

class DiaryComponent @Inject constructor(private val sleepSubscription: SleepSubscription)
    : Component<DiaryState, DiaryMsg, DiaryCmd> {

    override fun call(cmd: DiaryCmd): Single<DiaryMsg> {
        return Single.just(NoOp)
    }

    override fun initState(): DiaryState = DiaryState.empty()

    override fun subscriptions(): List<Sub<DiaryState, DiaryMsg>> = listOf(sleepSubscription)

    override fun update(msg: DiaryMsg, prevState: DiaryState): Pair<DiaryState, DiaryCmd?> = when (msg) {
        NoOp -> prevState.noCmd()
        is SleepLoaded -> prevState.copy(sleepList = msg.sleepList).noCmd()
    }

}

// State
data class DiaryState(val sleepList: PagedList<Sleep>?) : State {

    val isListEmpty get(): Boolean = sleepList != null

    companion object {
        fun empty() = DiaryState(null)
    }
}

// Subscription
class SleepSubscription @Inject constructor(private val sleepRepository: SleepDataSource) : StatelessSub<DiaryState, DiaryMsg>() {

    override fun invoke(): Observable<DiaryMsg> =
            sleepRepository.getSleepPaged().map { SleepLoaded(it) }
}

// Msg
sealed class DiaryMsg : Msg

data class SleepLoaded(val sleepList: PagedList<Sleep>) : DiaryMsg()
object NoOp : DiaryMsg()

// Cmd
sealed class DiaryCmd : Cmd
