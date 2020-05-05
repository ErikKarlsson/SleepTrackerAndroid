package net.erikkarlsson.simplesleeptracker.data.draft

import androidx.room.*

/**
 * Data Access Object for the sleep table.
 */
@Dao
interface DraftContentDao {

    @Transaction
    @Query("SELECT * FROM draftContent")
    fun getDraftContent(): DraftContentDetails

    @Transaction
    @Query("SELECT * FROM draftContent WHERE id=:id")
    fun getDraftContent(id: Long): DraftContentDetails

    @Insert
    suspend fun insertAll(draftContentList: List<DraftContentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraftContent(draftContent: DraftContentEntity): Long

    @Update
    suspend fun updateDraftContent(draftContent: DraftContentEntity): Int

    @Delete
    suspend fun deleteDraftContent(draftContent: DraftContentEntity)

    @Query("DELETE FROM draftContent")
    suspend fun deleteAllDraftContent()
}
