package net.erikkarlsson.simplesleeptracker.feature.diary

import io.reactivex.Observable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.entity.SleepDiary
import net.erikkarlsson.simplesleeptracker.domain.task.ObservableTask
import net.erikkarlsson.simplesleeptracker.elm.Cmd
import net.erikkarlsson.simplesleeptracker.elm.Component
import net.erikkarlsson.simplesleeptracker.elm.Msg
import net.erikkarlsson.simplesleeptracker.elm.State
import net.erikkarlsson.simplesleeptracker.elm.StatelessSub
import net.erikkarlsson.simplesleeptracker.elm.Sub
import net.erikkarlsson.simplesleeptracker.feature.diary.domain.GetSleepDiaryTask
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
        is DiaryLoaded -> prevState.copy(sleepDiary = msg.sleepDiary).noCmd()
    }

}

// State
data class DiaryState(val sleepDiary: SleepDiary?) : State {

    val isEmptySleep = sleepDiary == null || sleepDiary.pagedSleep.size == 0

    companion object {
        fun empty() = DiaryState(null)
    }
}

// Subscription
class SleepSubscription @Inject constructor(private val getSleepDiaryTask: GetSleepDiaryTask) : StatelessSub<DiaryState, DiaryMsg>() {

    override fun invoke(): Observable<DiaryMsg> =
            getSleepDiaryTask.execute(ObservableTask.None())
                    .map { DiaryLoaded(it) }
}

// Msg
sealed class DiaryMsg : Msg

data class DiaryLoaded(val sleepDiary: SleepDiary) : DiaryMsg()
object NoOp : DiaryMsg()

// Cmd
sealed class DiaryCmd : Cmd
