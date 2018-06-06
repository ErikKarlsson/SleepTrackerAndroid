package net.erikkarlsson.simplesleeptracker.domain

import io.reactivex.Completable
import io.reactivex.Maybe
import java.io.File

/**
 * Backup and restore files.
 */
interface FileBackupDataSource {
    fun get(): Maybe<File>
    fun put(file: File): Completable
}