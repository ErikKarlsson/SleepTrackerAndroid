package net.erikkarlsson.simplesleeptracker.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "sleep")
data class SleepEntity(@ColumnInfo(name = "from_date")
                       var fromDate: OffsetDateTime? = null,
                       @ColumnInfo(name = "to_date")
                       var toDate: OffsetDateTime? = null,
                       @ColumnInfo(name = "from_date_local")
                       var fromDateLocal: LocalDate? = null,
                       @ColumnInfo(name = "to_date_local")
                       var toDateLocal: LocalDate? = null,
                       @ColumnInfo(name = "to_date_midnight_offset_seconds")
                       var toDateMidnightOffsetSeconds: Int = 0,
                       @ColumnInfo(name = "from_date_midnight_offset_seconds")
                       var fromDateMidnightOffsetSeconds: Int = 0,
                       @ColumnInfo(name = "hours")
                       var hours: Float = 0.0f,
                       @PrimaryKey(autoGenerate = true)
                       var id: Int? = null) {

    companion object {
        fun empty() = SleepEntity(id = -1,
                fromDate = OffsetDateTime.MIN,
                toDate = OffsetDateTime.MAX)
    }
}

