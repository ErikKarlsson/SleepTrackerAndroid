package net.erikkarlsson.simplesleeptracker.domain.task

import io.reactivex.Completable

interface CompletableTask<Params> {
    fun execute(params: Params): Completable

    class None
}