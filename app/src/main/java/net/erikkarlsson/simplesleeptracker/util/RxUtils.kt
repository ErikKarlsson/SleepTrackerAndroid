package net.erikkarlsson.simplesleeptracker.util

import io.reactivex.Observable


fun <T, R> Observable<T>.scanMap(func2: (T?, T) -> R): Observable<R> {
    return this.startWith(null as T?) //emit a null value first, otherwise the .buffer() below won't emit at first (needs 2 emissions to emit)
        .buffer(2, 1) //buffer the previous and current emission
        .filter { it.size >= 2 }
        .map { func2.invoke(it[0], it[1]) }
}

fun <T, R> Observable<T>.scanMap(initialValue: T, func2: (T, T) -> R): Observable<R> {
    return this.startWith(initialValue) //use initially provided value instead of null
        .buffer(2, 1)
        .filter { it.size >= 2 }
        .map { func2.invoke(it[0], it[1]) }
}