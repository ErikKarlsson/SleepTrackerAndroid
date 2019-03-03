package net.erikkarlsson.simplesleeptracker.data.sleepdetection

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.erikkarlsson.simplesleeptracker.domain.entity.ActionType
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "Detection")
data class DetectionActionEntity(@ColumnInfo(name = "actionType")
                                 var actionType: ActionType,
                                 @ColumnInfo(name = "date")
                                 var date: OffsetDateTime,
                                 @PrimaryKey(autoGenerate = true)
                                 var id: Int? = null) {

    companion object {
        fun empty() = DetectionActionEntity(ActionType.NONE, OffsetDateTime.MIN)
    }
}
