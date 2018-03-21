package net.erikkarlsson.simplesleeptracker.domain.task

import io.reactivex.Observable

interface ObservableTask<T> {
    fun execute(): Observable<T>
}