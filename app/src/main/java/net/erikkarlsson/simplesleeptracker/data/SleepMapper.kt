package net.erikkarlsson.simplesleeptracker.data

import net.erikkarlsson.simplesleeptracker.domain.Sleep
import javax.inject.Inject

class SleepMapper @Inject constructor() : Mapper<SleepEntity, Sleep> {

    override fun mapFromEntity(type: SleepEntity): Sleep {
        return Sleep(type.id ?: throw RuntimeException("SleepEntity.id was null"),
                type.fromDate ?: throw RuntimeException("SleepEntity.fromDate was null"),
                toDate = type.toDate)
    }

    override fun mapToEntity(type: Sleep): SleepEntity {
        return SleepEntity(type.fromDate, type.toDate, type.toDateMidnightOffsetSeconds,
                type.fromDateMidnightOffsetSeconds, type.hours, type.id)
    }

}