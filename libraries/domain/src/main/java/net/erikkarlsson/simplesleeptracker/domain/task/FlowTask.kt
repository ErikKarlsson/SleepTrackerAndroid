package net.erikkarlsson.simplesleeptracker.domain.task

import kotlinx.coroutines.flow.Flow

interface FlowTask<T, Params> {
    fun flow(params: Params): Flow<T>

    class None
}
