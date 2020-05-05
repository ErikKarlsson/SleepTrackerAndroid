package net.erikkarlsson.simplesleeptracker.data.draft

import androidx.room.Embedded
import androidx.room.Relation

class DraftContentDetails(
        @Embedded val draftContent: DraftContentEntity,
        @Relation(
                parentColumn = "id",
                entityColumn = "draft_content_id",
                entity = DraftImageEntity::class)
        val images: List<DraftImageEntity>
)
