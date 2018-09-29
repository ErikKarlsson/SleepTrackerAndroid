package net.erikkarlsson.simplesleeptracker.domain

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import java.io.File

/**
 * Backup and restore files.
 */
interface FileBackupDataSource {
    fun get(): Maybe<File>
    fun put(file: File): Completable
    fun getLastBackupTimestamp(): Observable<Long>
    fun updateLastBackupTimestamp(): Completable
}