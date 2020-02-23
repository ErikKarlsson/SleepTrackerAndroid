package net.erikkarlsson.simplesleeptracker.data.sleep

import net.erikkarlsson.simplesleeptracker.data.MapperCoroutines
import net.erikkarlsson.simplesleeptracker.data.entity.SleepEntity
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import javax.inject.Inject

class SleepMapperCoroutines @Inject constructor() : MapperCoroutines<SleepEntity, Sleep> {

    override suspend fun mapFromEntity(type: SleepEntity): Sleep {
        return if (type == SleepEntity.empty()) {
            Sleep.empty()
        } else {
            Sleep(id = type.id ?: throw IllegalArgumentException("Requires non-null id"),
                    fromDate = type.fromDate ?: throw IllegalArgumentException("Requires non-null fromDate"),
                    toDate = type.toDate)
        }
    }

    override suspend fun mapToEntity(type: Sleep): SleepEntity {
        return SleepEntity(fromDate = type.fromDate,
                toDate = type.toDate,
                fromDateLocal = type.fromDate.toLocalDate(),
                toDateLocal = type.toDate?.toLocalDate(),
                toDateMidnightOffsetSeconds = type.toDateMidnightOffsetSeconds,
                fromDateMidnightOffsetSeconds = type.fromDateMidnightOffsetSeconds,
                hours = type.hours,
                id = type.id)
    }

}
