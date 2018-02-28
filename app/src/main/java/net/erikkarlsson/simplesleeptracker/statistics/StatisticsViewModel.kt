package net.erikkarlsson.simplesleeptracker.statistics

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.android.architecture.blueprints.todoapp.mvibase.MviViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import net.erikkarlsson.simplesleeptracker.domain.Statistics
import net.erikkarlsson.simplesleeptracker.util.SchedulerProvider
import timber.log.Timber
import javax.inject.Inject

class StatisticsViewModel @Inject constructor(private val actionProcessorHolder: StatisticsProcessorHolder,
                                              private val schedulerProvider: SchedulerProvider) :
        ViewModel(), MviViewModel<StatisticsIntent, StatisticsViewState> {

    private val disposables = CompositeDisposable()
    private val intentsSubject: Relay<StatisticsIntent> = BehaviorRelay.create()
    private val statesObservable: Observable<StatisticsViewState> = compose()
    private val statesLiveData: MutableLiveData<StatisticsViewState> = MutableLiveData()

    init {
        disposables.add(statesObservable.observeOn(schedulerProvider.ui())
            .subscribe { statesLiveData.value = it })
    }

    override fun processIntents(intents: Observable<StatisticsIntent>) {
        intents.subscribe(intentsSubject)
    }

    override fun states(): Observable<StatisticsViewState> = statesObservable

    fun statesLiveData(): LiveData<StatisticsViewState> = statesLiveData

    private fun compose(): Observable<StatisticsViewState> {
        return intentsSubject
            .map(this::actionFromIntent)
            .compose(actionProcessorHolder.actionProcessor)
            .scan(StatisticsViewState.idle(), StatisticsViewModel.reducer)
            .skip(1)
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

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}