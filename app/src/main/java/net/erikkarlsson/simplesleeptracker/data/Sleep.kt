package net.erikkarlsson.simplesleeptracker.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "sleep")
data class Sleep(@ColumnInfo(name = "from_date")
                 var fromDate: OffsetDateTime? = null,
                 @ColumnInfo(name = "to_date")
                 var toDate: OffsetDateTime? = null,
                 @ColumnInfo(name = "hours")
                 var hours: Float = 0.0f,
                 @PrimaryKey(autoGenerate = true)
                 var id: Int? = null)