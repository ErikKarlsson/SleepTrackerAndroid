package net.erikkarlsson.simplesleeptracker.data.draft

import androidx.room.*

/**
 * Data Access Object for the sleep table.
 */
@Dao
interface ListingDraftDao {

    @Transaction
    @Query("SELECT * FROM listingDraft WHERE id=:id")
    suspend fun getListingDraft(id: Long): ListingDraftDetails

    @Insert
    suspend fun insertAll(listingDraftList: List<ListingDraftEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListingDraft(listingDraft: ListingDraftEntity): Long

    @Update
    suspend fun updateListingDraft(listingDraft: ListingDraftEntity): Int

    @Delete
    suspend fun deleteListingDraft(listingDraft: ListingDraftEntity)

    @Query("DELETE FROM listingDraft")
    suspend fun deleteAllListingDraft()
}
