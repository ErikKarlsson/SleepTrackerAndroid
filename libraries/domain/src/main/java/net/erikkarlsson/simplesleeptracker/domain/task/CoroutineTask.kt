package net.erikkarlsson.simplesleeptracker.domain.task

interface CoroutineTask<Params> {
    suspend fun completable(params: Params)

    class None
}
