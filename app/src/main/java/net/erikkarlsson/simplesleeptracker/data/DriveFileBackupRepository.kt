package net.erikkarlsson.simplesleeptracker.data

import com.google.android.gms.drive.DriveFile
import com.google.android.gms.drive.DriveFolder
import com.google.android.gms.drive.MetadataBuffer
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.base.BACKUP_FILE_NAME
import net.erikkarlsson.simplesleeptracker.base.BACKUP_FOLDER_NAME
import net.erikkarlsson.simplesleeptracker.base.BACKUP_MIME_TYPE
import net.erikkarlsson.simplesleeptracker.base.RESTORE_FILE_NAME
import net.erikkarlsson.simplesleeptracker.domain.FileBackupDataSource
import net.erikkarlsson.simplesleeptracker.util.toFile
import java.io.File
import javax.inject.Inject
import javax.inject.Named

/**
 * Backup and restore files on Google Drive.
 *
 * Requires user to be logged in to Google Account.
 */
class DriveFileBackupRepository
@Inject constructor(private val rxDrive: RxDrive,
                    @Named("filePath") private val filePath: String) : FileBackupDataSource {

    override fun get(): Maybe<File> =
            queryBackupFile().flatMapMaybe { fileMeta ->
                if (fileMeta.count > 0) {
                    val driveFile = fileMeta[0].driveId.asDriveFile()
                    openFile(driveFile).toMaybe()
                } else {
                    Maybe.empty<File>()
                }
            }

    override fun put(file: File): Completable =
            queryBackupFile().flatMapCompletable { fileMeta ->
                uploadToDrive(fileMeta, file)
                        .andThen(syncFiles())
            }

    private fun openFile(driveFile: DriveFile): Single<File> =
            rxDrive.openFile(driveFile)
                    .map { it.inputStream.toFile("$filePath/$RESTORE_FILE_NAME") }

    private fun queryBackupFile(): Single<MetadataBuffer> = rxDrive.queryFileMeta(BACKUP_FILE_NAME)

    private fun uploadToDrive(fileMeta: MetadataBuffer,
                              file: File): Completable =
            if (fileMeta.count > 0) {
                val driveFile = fileMeta[0].driveId.asDriveFile()
                updateExistingFile(driveFile, file)
            } else {
                createNewFile(file)
            }

    private fun syncFiles(): Completable =
            rxDrive.requestSync()
                    // Sync request might fail because of API rate limit.
                    // Drive will eventually perform sync so consider the work successful.
                    .onErrorComplete()

    private fun updateExistingFile(driveFile: DriveFile,
                                   file: File): Completable =
            rxDrive.commitContent(driveFile, file)

    private fun createNewFile(file: File): Completable =
            getFolder().flatMapCompletable { folder ->
                rxDrive.createFile(folder, file, BACKUP_MIME_TYPE)
            }

    private fun getFolder(): Single<DriveFolder> =
            rxDrive.queryFolderMeta(BACKUP_FOLDER_NAME).flatMap { folderMeta ->
                if (folderMeta.count > 0) {
                    val driveFolder = folderMeta[0].driveId.asDriveFolder()
                    Single.just(driveFolder)
                } else {
                    rxDrive.createFolder(BACKUP_FOLDER_NAME)
                }
            }
}
