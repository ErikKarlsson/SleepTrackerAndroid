package net.erikkarlsson.simplesleeptracker.elm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 *  Implementation of ComponentRuntime based on RxJava and RxRelay
 */
internal class RxRuntime<STATE : State, in MSG : Msg, CMD : Cmd>(component: Component<STATE, MSG, CMD>, private val logLevel: LogLevel = LogLevel.NONE) : ComponentRuntime<STATE, MSG> {

    private val msgRelay: BehaviorRelay<MSG> = BehaviorRelay.create()
    private val stateRelay: BehaviorRelay<STATE> = BehaviorRelay.create()

    private val state: MutableLiveData<STATE> = MutableLiveData()

    private val compositeDisposable = CompositeDisposable()

    init {
        updateStateValue(component.initState())

        val sharedStateRelay = stateRelay.publish()
            .autoConnect(component.subscriptions().filter { it is StatefulSub<STATE, MSG> }.size + 1)

        // setup main app "loop" without subscriptions
        msgRelay.zipWith(sharedStateRelay)
            .map { (msg, prevState) -> component.update(msg, prevState) }
            .subscribeNewObserveMain()
            .doOnNext { (newState, _) -> updateStateValue(newState) }
            .compose(cmdToMsg(component))
            .subscribe { msg -> dispatch(msg) }
            .addTo(compositeDisposable)

        // handle subscriptions
        component.subscriptions().forEach { sub ->
            createSubObs(sub, sharedStateRelay)
                .subscribeNewObserveMain()
                .doOnSubscribe { log(LogLevel.FULL, "Subscribed sub: $sub") }
                .subscribe { msg -> dispatch(msg) }
                .addTo(compositeDisposable)
        }
    }

    override fun state(): LiveData<STATE> = state.getDistinct()

    override fun clear() {
        compositeDisposable.clear()
        log(LogLevel.BASIC, "Runtime cleared")
    }

    override fun dispatch(msg: MSG) {
        msgRelay.accept(msg)
        log(LogLevel.BASIC, "Msg dispatched: $msg")
    }

    private fun updateStateValue(stateVal: STATE) {
        state.postValue(stateVal)
        stateRelay.accept(stateVal)
        log(LogLevel.BASIC, "State updated to: $stateVal")
    }

    private fun cmdToMsg(component: Component<STATE, MSG, CMD>): ObservableTransformer<Pair<STATE, CMD?>, MSG> {
        return ObservableTransformer { obs ->
            obs.filter { (_, maybeCmd) -> maybeCmd != null }
                .map { (_, cmd) -> cmd }
                .observeOn(Schedulers.newThread())
                .doOnNext { cmd -> log(LogLevel.BASIC, "Calling cmd: $cmd") }
                .flatMap { cmd -> component.call(cmd).toObservable() }
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    private fun createSubObs(
            sub: Sub<STATE, MSG>,
            sharedStateRelay: Observable<STATE>
    ): Observable<MSG> {
        return when (sub) {
            is StatelessSub<STATE, MSG> -> sub()
            is StatefulSub<STATE, MSG> -> {
                sharedStateRelay
                    .distinctUntilChanged { s1, s2 -> !sub.isDistinct(s1, s2) }
                    .doOnNext { state -> log(LogLevel.FULL, "New state $state for StatefulSub $sub to handle") }
                    .switchMap { state -> sub(state) }
            }
        }
    }

    private fun log(
            minLogLevel: LogLevel,
            logMsg: String
    ) {
        if (minLogLevel.logLevelIndex <= logLevel.logLevelIndex) {
            Timber.d(logMsg)
        }
    }
}
