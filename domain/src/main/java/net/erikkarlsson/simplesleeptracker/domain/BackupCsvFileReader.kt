package net.erikkarlsson.simplesleeptracker.domain

import com.google.common.collect.ImmutableList
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import java.io.File

/**
 * Read backup content from CSV file.
 */
interface BackupCsvFileReader {
    /**
     * Reads CSV file content and creates list of [Sleep].
     *
     * @param file the file to read from
     *
     * @return list a list of sleep
     */
    fun read(file: File): ImmutableList<Sleep>
}
