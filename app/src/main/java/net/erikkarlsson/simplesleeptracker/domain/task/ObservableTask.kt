package net.erikkarlsson.simplesleeptracker.domain.task

import io.reactivex.Observable

interface ObservableTask<T, Params> {
    fun observable(params: Params): Observable<T>

    class None
}
