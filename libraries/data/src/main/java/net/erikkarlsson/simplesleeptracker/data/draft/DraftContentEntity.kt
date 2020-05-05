package net.erikkarlsson.simplesleeptracker.data.draft

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "draftContent")
data class DraftContentEntity(@PrimaryKey(autoGenerate = true)
                               var id: Long? = null,
                               @ColumnInfo(name = "listing_draft_id")
                               val listingDraftId: Long,
                               @ColumnInfo(name = "title")
                               val title: String,
                               @ColumnInfo(name = "description")
                               val description: String
)
