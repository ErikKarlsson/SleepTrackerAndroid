package net.erikkarlsson.simplesleeptracker.domain.draft

import androidx.paging.PagedList
import com.google.common.collect.ImmutableList
import kotlinx.coroutines.flow.Flow
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep

interface DraftDataSource {
    fun getDraft(id: Int): ListingDraft

    suspend fun getActive(): ListingDraft

    suspend fun update(updatedSleep: Sleep): Int

    suspend fun delete(sleep: ListingDraft)

    suspend fun insert(newDraft: ListingDraft): Long
}
