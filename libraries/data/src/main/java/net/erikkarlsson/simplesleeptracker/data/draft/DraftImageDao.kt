package net.erikkarlsson.simplesleeptracker.data.draft

import androidx.room.*

/**
 * Data Access Object for the sleep table.
 */
@Dao
interface DraftImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraftImage(draftImage: DraftImageEntity): Long
}
