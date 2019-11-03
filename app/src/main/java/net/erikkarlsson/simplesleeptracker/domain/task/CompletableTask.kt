package net.erikkarlsson.simplesleeptracker.domain.task

import io.reactivex.Completable

interface CompletableTask<Params> {
    fun completable(params: Params): Completable

    class None
}
