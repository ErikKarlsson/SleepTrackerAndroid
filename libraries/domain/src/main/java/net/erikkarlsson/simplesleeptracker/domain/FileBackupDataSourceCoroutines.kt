package net.erikkarlsson.simplesleeptracker.domain

import io.reactivex.Completable
import io.reactivex.Maybe
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * Backup and restore files.
 */
interface FileBackupDataSourceCoroutines {
    fun get(): Maybe<File>
    fun put(file: File): Completable
    fun getLastBackupTimestamp(): Flow<Long>
    fun updateLastBackupTimestamp()
}
