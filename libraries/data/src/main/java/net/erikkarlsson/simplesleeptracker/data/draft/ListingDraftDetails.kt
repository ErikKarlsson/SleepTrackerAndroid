package net.erikkarlsson.simplesleeptracker.data.draft

import androidx.room.Embedded
import androidx.room.Relation

data class ListingDraftDetails(
        @Embedded val listingDraft: ListingDraftEntity,
        @Relation(
                parentColumn = "id",
                entityColumn = "listing_draft_id",
                entity = DraftContentEntity::class
        )
        val draftContent: DraftContentDetails
)
