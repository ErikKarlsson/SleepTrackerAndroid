package net.erikkarlsson.simplesleeptracker.data.draft

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.erikkarlsson.simplesleeptracker.data.TiviTypeConverters

/**
 * The Room database that contains the Sleep table
 */
@Database(entities = arrayOf(DraftContentEntity::class, ListingDraftEntity::class, DraftImageEntity::class), version = 1)
@TypeConverters(TiviTypeConverters::class)
abstract class TraderaDatabase : RoomDatabase() {

    abstract fun draftDao(): DraftContentDao

    abstract fun listingDao(): ListingDraftDao

    abstract fun imageDao(): DraftImageDao

    companion object {

        @Volatile private var INSTANCE: TraderaDatabase? = null

        fun getInstance(context: Context): TraderaDatabase =
                INSTANCE
                        ?: synchronized(this) {
                    INSTANCE
                            ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                                     TraderaDatabase::class.java, "Tradera.db")
                        .build()
    }
}
