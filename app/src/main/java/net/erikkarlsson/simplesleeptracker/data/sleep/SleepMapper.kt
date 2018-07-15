package net.erikkarlsson.simplesleeptracker.data.sleep

import net.erikkarlsson.simplesleeptracker.data.Mapper
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import javax.inject.Inject

class SleepMapper @Inject constructor() : Mapper<SleepEntity, Sleep> {

    override fun mapFromEntity(type: SleepEntity): Sleep {
        return Sleep(id = type.id ?: throw RuntimeException("Requires non-null id"),
                fromDate = type.fromDate ?: throw RuntimeException("Requires non-null fromDate"),
                toDate = type.toDate)
    }

    override fun mapToEntity(type: Sleep): SleepEntity {
        return SleepEntity(fromDate = type.fromDate,
                                                                          toDate = type.toDate,
                                                                          toDateMidnightOffsetSeconds = type.toDateMidnightOffsetSeconds,
                                                                          fromDateMidnightOffsetSeconds = type.fromDateMidnightOffsetSeconds,
                                                                          hours = type.hours,
                                                                          id = type.id)
    }

}