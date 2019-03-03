package net.erikkarlsson.simplesleeptracker.data

import androidx.room.TypeConverter
import net.erikkarlsson.simplesleeptracker.domain.entity.ActionType
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

object TiviTypeConverters {
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    private val localFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    @JvmStatic
    fun toOffsetDateTime(value: String?): OffsetDateTime? {
        return value?.let {
            return formatter.parse(value, OffsetDateTime::from)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromOffsetDateTime(date: OffsetDateTime?): String? {
        return date?.format(formatter)
    }

    @TypeConverter
    @JvmStatic
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let {
            return localFormatter.parse(value, LocalDate::from)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromLocalDate(localDate: LocalDate?): String? {
        return localDate?.format(localFormatter)
    }

    @TypeConverter
    @JvmStatic
    fun sleepDetectionActionTypeToTnt(value: ActionType) = value.toInt()

    @TypeConverter
    @JvmStatic
    fun intToSleepDetectionActionType(value: Int) = value.toEnum<ActionType>()
}

@Suppress("NOTHING_TO_INLINE")
private inline fun <T : Enum<T>> T.toInt(): Int = this.ordinal

private inline fun <reified T : Enum<T>> Int.toEnum(): T = enumValues<T>()[this]

