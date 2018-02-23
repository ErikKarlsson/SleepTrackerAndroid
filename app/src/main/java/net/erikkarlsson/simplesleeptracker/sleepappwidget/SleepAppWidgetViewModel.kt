package net.erikkarlsson.simplesleeptracker.sleepappwidget

import com.example.android.architecture.blueprints.todoapp.mvibase.MviViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepAppWidgetViewModel @Inject constructor(private val actionProcessorHolder: SleepActionProcessorHolder)
    : MviViewModel<WidgetIntent, WidgetViewState> {

    private val intentsSubject: BehaviorRelay<WidgetIntent> = BehaviorRelay.create()
    private val statesObservable: Observable<WidgetViewState> = compose()

    override fun processIntents(intents: Observable<WidgetIntent>) {
        intents.subscribe(intentsSubject)
    }

    override fun states(): Observable<WidgetViewState> = statesObservable

    private fun compose(): Observable<WidgetViewState> {
        return intentsSubject
            .map(this::actionFromIntent)
            .compose(actionProcessorHolder.actionProcessor)
            .scan(WidgetViewState.idle(), reducer)
            .skip(1)
            .replay(1)
            .autoConnect(0)
    }

    private fun actionFromIntent(intent: WidgetIntent): WidgetAction {
        Timber.d("actionFromIntent")
        return when (intent) {
            WidgetIntent.InitialIntent -> WidgetAction.LoadCurrentSleepAction
            WidgetIntent.ToggleSleepIntent -> WidgetAction.ToggleSleepAction
        }
    }

    companion object {
        private val reducer = BiFunction { previousState: WidgetViewState, result: WidgetResult ->
            Timber.d("reducer")
            when (result) {
                is WidgetResult.ToggleSleepResult.Success -> previousState.copy(isSleeping = result.sleep.isSleeping)
                is WidgetResult.LoadCurrentSleepResult.Success -> previousState.copy(isSleeping = result.sleep.isSleeping)
                is WidgetResult.LoadCurrentSleepResult.Failure -> previousState.copy(isSleeping = false)
                is WidgetResult.ToggleSleepResult.Failure -> previousState.copy(isSleeping = false)
            }
        }
    }

}