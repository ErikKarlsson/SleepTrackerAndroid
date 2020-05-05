package net.erikkarlsson.simplesleeptracker.data.draft

import net.erikkarlsson.simplesleeptracker.domain.draft.DraftDataSource
import net.erikkarlsson.simplesleeptracker.domain.draft.ListingDraft
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep

/*
class DraftRepository : DraftDataSource {

    override fun getDraft(id: Int): ListingDraft {
        val draftEntity = draftDao.getDraft(id)
        return draftMapper.mapFromToEntity(draftEntity)
    }

    override suspend fun getActive(): ListingDraft {

    }

    override suspend fun update(updatedSleep: Sleep): Int {

    }

    override suspend fun delete(sleep: ListingDraft) {

    }

    override suspend fun insert(newDraft: ListingDraft): Long {
        draftMapper.mapToEntity(newDraft)
    }

}
*/
