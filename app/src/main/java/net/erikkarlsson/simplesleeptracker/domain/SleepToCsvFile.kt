package net.erikkarlsson.simplesleeptracker.domain

import com.google.common.collect.ImmutableList
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import java.io.File


interface SleepToCsvFile {
    /**
     * Converts list of [Sleep] to CSV file.
     *
     * @param sleep the list of sleep
     *
     * @return file path
     */
    fun write(sleepList: ImmutableList<Sleep>): File

    fun read(file: File): ImmutableList<Sleep>
}
