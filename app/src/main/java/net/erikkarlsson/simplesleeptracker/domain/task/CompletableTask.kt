package net.erikkarlsson.simplesleeptracker.domain.task

import io.reactivex.Completable

interface CompletableTask {
    fun execute(): Completable
}