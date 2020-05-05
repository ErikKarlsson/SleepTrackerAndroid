package net.erikkarlsson.simplesleeptracker.data.draft

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "draftImage")
data class DraftImageEntity(@PrimaryKey(autoGenerate = true)
                            var id: Long? = null,
                            @ColumnInfo(name = "draft_content_id")
                            val draftContentId: Long,
                            @ColumnInfo(name = "url")
                            val url: String
)
