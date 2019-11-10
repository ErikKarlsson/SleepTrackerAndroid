package net.erikkarlsson.simplesleeptracker.domain

import com.google.common.collect.ImmutableList
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import java.io.File

/**
 * Write backup content to CSV file.
 */
interface BackupCsvFileWriter {
    /**
     * Writes list of [Sleep] to CSV file.
     *
     * @param sleep the list of sleep
     *
     * @return file path
     */
    fun write(sleepList: ImmutableList<Sleep>): File
}
