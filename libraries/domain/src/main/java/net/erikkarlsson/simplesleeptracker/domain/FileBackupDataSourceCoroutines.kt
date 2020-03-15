package net.erikkarlsson.simplesleeptracker.domain

import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * Backup and restore files.
 */
interface FileBackupDataSourceCoroutines {
    fun get(): File?
    fun put(file: File)
    fun getLastBackupTimestamp(): Flow<Long>
    fun updateLastBackupTimestamp()
}
