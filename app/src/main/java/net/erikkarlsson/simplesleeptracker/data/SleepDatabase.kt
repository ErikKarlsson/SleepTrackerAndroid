package net.erikkarlsson.simplesleeptracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.erikkarlsson.simplesleeptracker.data.entity.SleepEntity
import net.erikkarlsson.simplesleeptracker.data.sleep.SleepDao
import net.erikkarlsson.simplesleeptracker.data.sleepdetection.DetectionActionEntity
import net.erikkarlsson.simplesleeptracker.data.sleepdetection.DetectionDao
import net.erikkarlsson.simplesleeptracker.data.statistics.StatisticsDao

/**
 * The Room database that contains the Sleep table
 */
@Database(
        entities = [SleepEntity::class, DetectionActionEntity::class],
        version = 1)
@TypeConverters(TiviTypeConverters::class)
abstract class SleepDatabase : RoomDatabase() {

    abstract fun sleepDao(): SleepDao

    abstract fun statisticsDao(): StatisticsDao

    abstract fun detectionDao(): DetectionDao

    companion object {

        @Volatile
        private var INSTANCE: SleepDatabase? = null

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
