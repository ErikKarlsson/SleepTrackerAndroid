package net.erikkarlsson.simplesleeptracker.util

import io.reactivex.Observable

/**
 * Whereas [Observable.scan] aggregates all items over time,
 * scanMap only cares about the previous and next item.
 *
 * @param initialValue the initial value with type of upstream observable item.
 * @param biFunc the function that is invoked with the previous and next item,
 * passing the result down the stream.
 *
 */
fun <T, R> Observable<T>.scanMap(initialValue: T, biFunc: (T, T) -> R): Observable<R> {
    return this.startWith(initialValue)
        .buffer(2, 1)
        .filter { it.size >= 2 }
        .map { biFunc.invoke(it[0], it[1]) }
}