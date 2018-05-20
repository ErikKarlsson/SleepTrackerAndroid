package net.erikkarlsson.simplesleeptracker.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context

/**
 * The Room database that contains the Sleep table
 */
@Database(entities = arrayOf(SleepEntity::class), version = 1)
@TypeConverters(TiviTypeConverters::class)
abstract class SleepDatabase : RoomDatabase() {

    abstract fun sleepDao(): SleepDao

    companion object {

        @Volatile private var INSTANCE: SleepDatabase? = null

        fun getInstance(context: Context): SleepDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                                     SleepDatabase::class.java, "Sleep.db")
                        .build()
    }
}
