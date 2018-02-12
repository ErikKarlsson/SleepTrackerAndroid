package net.erikkarlsson.simplesleeptracker.persistence

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "sleep")
data class Sleep(@PrimaryKey(autoGenerate = true)
                 var id: Int? = null,
                 @ColumnInfo(name = "bed_time")
                 var bedTime: OffsetDateTime? = null,
                 @ColumnInfo(name = "wake_up_time")
                 var wakeUpTime: OffsetDateTime? = null)