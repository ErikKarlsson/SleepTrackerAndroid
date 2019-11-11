package net.erikkarlsson.simplesleeptracker.data.backup

import com.google.api.services.drive.model.FileList
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.FileBackupDataSource
import net.erikkarlsson.simplesleeptracker.domain.PREFS_LAST_SYNC_TIMESTAMP
import net.erikkarlsson.simplesleeptracker.domain.PreferencesDataSource
import net.erikkarlsson.simplesleeptracker.features.backup.BACKUP_FILE_NAME
import net.erikkarlsson.simplesleeptracker.features.backup.BACKUP_FOLDER_NAME
import net.erikkarlsson.simplesleeptracker.features.backup.BACKUP_MIME_TYPE
import net.erikkarlsson.simplesleeptracker.features.backup.RESTORE_FILE_NAME
import net.erikkarlsson.simplesleeptracker.util.toFile
import java.io.File
import javax.inject.Inject
import javax.inject.Named

/**
 * Backup and restore files on Google Drive.
 *
 * Requires user to be logged in to Google Account.
 */
class DriveFileBackupRepository @Inject constructor(
        private val rxDrive: RxDrive,
        @Named("filePath") private val filePath: String,
        private val preferencesDataSource: PreferencesDataSource)
    : FileBackupDataSource {

    override fun get(): Maybe<File> =
            queryBackupFile().flatMapMaybe { fileList ->
                val files = fileList.files
                if (files.size > 0) {
                    openFile(files[0]).toMaybe()
                } else {
                    Maybe.empty<File>()
                }
            }

    override fun put(file: File): Completable =
            queryBackupFile().flatMapCompletable { fileList ->
                uploadToDrive(fileList, file)
            }

    override fun getLastBackupTimestamp(): Observable<Long> =
            preferencesDataSource.getLong(PREFS_LAST_SYNC_TIMESTAMP)

    override fun updateLastBackupTimestamp(): Completable =
            Completable.fromCallable {
                preferencesDataSource.set(PREFS_LAST_SYNC_TIMESTAMP, System.currentTimeMillis())
            }

    private fun openFile(file: com.google.api.services.drive.model.File): Single<File> =
            rxDrive.openFile(file.id)
                    .map { it.toFile("$filePath/$RESTORE_FILE_NAME") }

    private fun queryBackupFile(): Single<FileList> = rxDrive.queryFileMeta(BACKUP_FILE_NAME)

    private fun uploadToDrive(fileList: FileList,
                              file: File): Completable {
        val files = fileList.files

        return if (files.size > 0) {
            val fileId = files[0].id
            updateExistingFile(fileId, file)
        } else {
            createNewFile(file)
        }
    }

    private fun updateExistingFile(fileId: String,
                                   file: File): Completable =
            rxDrive.commitContent(fileId, file)

    private fun createNewFile(file: File): Completable =
            getFolder().flatMapCompletable { folder ->
                rxDrive.createFile(folder.id, file, BACKUP_MIME_TYPE)
            }

    private fun getFolder(): Single<com.google.api.services.drive.model.File> =
            rxDrive.queryFolderMeta(BACKUP_FOLDER_NAME).flatMap { fileList ->
                val files = fileList.files
                if (files.size > 0) {
                    Single.just(files[0])
                } else {
                    rxDrive.createFolder(BACKUP_FOLDER_NAME)
                }
            }
}
