package net.erikkarlsson.simplesleeptracker.data.sleepdetection

import com.google.common.collect.ImmutableList
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import net.erikkarlsson.simplesleeptracker.domain.DetectionActionDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.DetectionAction
import net.erikkarlsson.simplesleeptracker.util.toImmutableList
import javax.inject.Inject

class DetectionActionRepository @Inject constructor(
        private val detectionDao: DetectionDao,
        private val detectionMapper: DetectionMapper) : DetectionActionDataSource {

    override fun getDetectionAction(): Observable<ImmutableList<DetectionAction>> =
            detectionDao.getAllDetectionActions()
                    .toObservable()
                    .switchMap { detectionList ->
                        Observable.fromIterable(detectionList)
                                .map { detectionMapper.mapFromEntity(it) }
                                .toImmutableList()
                                .toObservable()
                                .subscribeOn(Schedulers.computation())
                    }

    override fun insert(detectionAction: DetectionAction): Long {
        val detectionActionEntity = detectionMapper.mapToEntity(detectionAction)
        return detectionDao.insertDetectionAction(detectionActionEntity)
    }

    override fun deleteAllDetectionActions(): Completable =
            Completable.fromCallable { detectionDao.deleteAllDetectionActions()  }
}
