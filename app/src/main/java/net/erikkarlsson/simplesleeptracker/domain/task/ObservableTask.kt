package net.erikkarlsson.simplesleeptracker.domain.task

import io.reactivex.Observable

interface ObservableTask<T, Params> {
    fun execute(params: Params): Observable<T>

    class None
}
