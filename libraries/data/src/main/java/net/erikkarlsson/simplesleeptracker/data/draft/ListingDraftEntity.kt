package net.erikkarlsson.simplesleeptracker.data.draft

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "listingDraft")
data class ListingDraftEntity(@PrimaryKey(autoGenerate = true)
                              var id: Long? = null,
                              @ColumnInfo(name = "template_name")
                              val templateName: String,
                              @ColumnInfo(name = "date_created")
                              val dateCreated: String)
