package net.erikkarlsson.simplesleeptracker.domain

import com.google.common.collect.ImmutableList
import io.reactivex.Completable
import io.reactivex.Observable
import net.erikkarlsson.simplesleeptracker.domain.entity.DetectionAction

interface DetectionActionDataSource {
    fun getDetectionAction(): Observable<ImmutableList<DetectionAction>>
    fun insert(detectionAction: DetectionAction): Long
    fun deleteAllDetectionActions(): Completable
}
