package net.erikkarlsson.simplesleeptracker.statistics

import android.arch.lifecycle.ViewModel
import com.example.android.architecture.blueprints.todoapp.mvibase.MviViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import net.erikkarlsson.simplesleeptracker.domain.Statistics
import net.erikkarlsson.simplesleeptracker.sleepappwidget.StatisticsAction
import net.erikkarlsson.simplesleeptracker.sleepappwidget.StatisticsIntent
import net.erikkarlsson.simplesleeptracker.sleepappwidget.StatisticsResult
import timber.log.Timber
import javax.inject.Inject

class StatisticsViewModel @Inject constructor(private val actionProcessorHolder: StatisticsProcessorHolder) :
        ViewModel(), MviViewModel<StatisticsIntent, StatisticsViewState> {

    private val intentsSubject: BehaviorRelay<StatisticsIntent> = BehaviorRelay.create()
    private val statesObservable: Observable<StatisticsViewState> = compose()

    override fun processIntents(intents: Observable<StatisticsIntent>) {
        intents.subscribe(intentsSubject)
    }

    override fun states(): Observable<StatisticsViewState> = statesObservable

    private fun compose(): Observable<StatisticsViewState> {
        return intentsSubject
            .map(this::actionFromIntent)
            .compose(actionProcessorHolder.actionProcessor)
            .scan(StatisticsViewState.idle(), StatisticsViewModel.reducer)
            .distinctUntilChanged()
            .replay(1)
            .autoConnect(0)
    }

    private fun actionFromIntent(intent: StatisticsIntent): StatisticsAction {
        Timber.d("actionFromIntent")
        return when (intent) {
            StatisticsIntent.InitialIntent -> StatisticsAction.LoadStatisticsAction
        }
    }

    companion object {
        private val reducer = BiFunction { previousState: StatisticsViewState, result: StatisticsResult ->
            when (result) {
                is StatisticsResult.LoadStatisticsResult.Success -> previousState.copy(result.statistics)
                is StatisticsResult.LoadStatisticsResult.Failure -> previousState.copy(Statistics.empty())
            }
        }
    }

}