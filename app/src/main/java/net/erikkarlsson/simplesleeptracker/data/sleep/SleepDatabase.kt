package net.erikkarlsson.simplesleeptracker.data.sleep

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import net.erikkarlsson.simplesleeptracker.data.TiviTypeConverters
import net.erikkarlsson.simplesleeptracker.data.entity.SleepEntity
import net.erikkarlsson.simplesleeptracker.data.statistics.StatisticsDao

/**
 * The Room database that contains the Sleep table
 */
@Database(entities = arrayOf(SleepEntity::class), version = 1)
@TypeConverters(TiviTypeConverters::class)
abstract class SleepDatabase : RoomDatabase() {

    abstract fun sleepDao(): SleepDao

    abstract fun statisticsDao(): StatisticsDao

    companion object {

        @Volatile private var INSTANCE: SleepDatabase? = null

        fun getInstance(context: Context): SleepDatabase =
                INSTANCE
                        ?: synchronized(this) {
                    INSTANCE
                            ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                                     SleepDatabase::class.java, "Sleep.db")
                        .build()
    }
}
