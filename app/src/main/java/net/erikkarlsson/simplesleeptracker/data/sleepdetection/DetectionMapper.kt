package net.erikkarlsson.simplesleeptracker.data.sleepdetection

import net.erikkarlsson.simplesleeptracker.data.Mapper
import net.erikkarlsson.simplesleeptracker.domain.entity.DetectionAction
import javax.inject.Inject

class DetectionMapper @Inject constructor() : Mapper<DetectionActionEntity, DetectionAction> {

    override fun mapFromEntity(type: DetectionActionEntity): DetectionAction {
        return if (type == DetectionActionEntity.empty()) {
            DetectionAction.empty()
        } else {
            DetectionAction(
                    id = type.id ?: throw IllegalArgumentException("Requires non-null id"),
                    date = type.date,
                    actionType = type.actionType)
        }
    }

    override fun mapToEntity(type: DetectionAction): DetectionActionEntity {
        return DetectionActionEntity(
                id = type.id,
                date = type.date,
                actionType = type.actionType)
    }

}
