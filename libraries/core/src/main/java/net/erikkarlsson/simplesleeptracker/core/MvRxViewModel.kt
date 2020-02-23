package net.erikkarlsson.simplesleeptracker.core

import android.util.Log
import com.airbnb.mvrx.*
import com.airbnb.mvrx.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

abstract class MvRxViewModel<S : MvRxState>(initialState: S) :
    BaseMvRxViewModel<S>(initialState, debugMode = BuildConfig.DEBUG) {

    protected suspend inline fun <T> Flow<T>.execute(
            crossinline stateReducer: S.(Async<T>) -> S
    ) = execute({ it }, stateReducer)

    protected suspend inline fun <T, V> Flow<T>.execute(
            crossinline mapper: (T) -> V,
            crossinline stateReducer: S.(Async<V>) -> S
    ) {
        setState { stateReducer(Loading()) }

        @Suppress("USELESS_CAST")
        return map { Success(mapper(it)) as Async<V> }
                .catch {
                    if (BuildConfig.DEBUG) {
                        Log.e(this@MvRxViewModel::class.java.simpleName,
                                "Exception during observe", it)
                    }
                    emit(Fail(it))
                }
                .collect { setState { stateReducer(it) } }
    }
}
