package net.erikkarlsson.simplesleeptracker.util

import io.reactivex.Observable

/**
 * The results of scanMap are only aggregations of the previous and next item,
 * whereas [Observable.scan] accumulates aggregations of all items over time.
 *
 * @param initialValue the initial value with type of upstream observable item.
 * @param biFunc the aggregation function that is invoked with the previous and next item.
 *
 */
fun <T, R> Observable<T>.scanMap(initialValue: T, biFunc: (T, T) -> R): Observable<R> {
    return this.startWith(initialValue)
        .buffer(2, 1)
        .filter { it.size >= 2 }
        .map { biFunc.invoke(it[0], it[1]) }
}