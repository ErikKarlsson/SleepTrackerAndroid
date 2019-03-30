package net.erikkarlsson.simplesleeptracker.data.sleepdetection

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface DetectionDao {

    @Query("SELECT * FROM Detection")
    fun getAllDetectionActions(): Flowable<List<DetectionActionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDetectionAction(detectionAction: DetectionActionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDetectionActionCompletable(detectionAction: DetectionActionEntity): Completable

    @Query("DELETE FROM Detection")
    fun deleteAllDetectionActions()
}
