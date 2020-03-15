package net.erikkarlsson.simplesleeptracker.features.backup.data

import com.google.api.services.drive.model.FileList
import kotlinx.coroutines.flow.Flow
import net.erikkarlsson.simplesleeptracker.core.util.toFile
import net.erikkarlsson.simplesleeptracker.domain.FileBackupDataSource
import net.erikkarlsson.simplesleeptracker.domain.PREFS_LAST_SYNC_TIMESTAMP
import net.erikkarlsson.simplesleeptracker.domain.PreferencesDataSourceFlow
import net.erikkarlsson.simplesleeptracker.features.backup.BACKUP_FILE_NAME
import net.erikkarlsson.simplesleeptracker.features.backup.BACKUP_FOLDER_NAME
import net.erikkarlsson.simplesleeptracker.features.backup.BACKUP_MIME_TYPE
import net.erikkarlsson.simplesleeptracker.features.backup.RESTORE_FILE_NAME
import java.io.File
import javax.inject.Inject
import javax.inject.Named

/**
 * Backup and restore files on Google Drive.
 *
 * Requires user to be logged in to Google Account.
 */
class DriveFileBackupRepository @Inject constructor(
        private val drive: DriveApi,
        @Named("filePath") private val filePath: String,
        private val preferencesDataSourceFlow: PreferencesDataSourceFlow)
    : FileBackupDataSource {

    override fun get(): File? {
        val fileList = queryBackupFile()
        val files = fileList.files

        return when (files.size > 0) {
            true -> openFile(files[0])
            false -> null
        }
    }

    override fun put(file: File) {
        val fileList = queryBackupFile()
        uploadToDrive(fileList, file)
    }

    override fun getLastBackupTimestamp(): Flow<Long> =
            preferencesDataSourceFlow.getLong(PREFS_LAST_SYNC_TIMESTAMP)

    override fun updateLastBackupTimestamp() =
            preferencesDataSourceFlow.set(PREFS_LAST_SYNC_TIMESTAMP, System.currentTimeMillis())

    private fun openFile(file: com.google.api.services.drive.model.File): File {
        val inputStream = drive.openFile(file.id)
        return inputStream.toFile("$filePath/$RESTORE_FILE_NAME")
    }

    private fun queryBackupFile(): FileList = drive.queryFileMeta(BACKUP_FILE_NAME)

    private fun uploadToDrive(fileList: FileList, file: File) {
        val files = fileList.files

        return if (files.size > 0) {
            val fileId = files[0].id
            updateExistingFile(fileId, file)
        } else {
            createNewFile(file)
        }
    }

    private fun updateExistingFile(fileId: String, file: File) =
            drive.commitContent(fileId, file)

    private fun createNewFile(file: File) {
        val folder = getFolder()
        drive.createFile(folder.id, file, BACKUP_MIME_TYPE)
    }

    private fun getFolder(): com.google.api.services.drive.model.File {
        val fileList = drive.queryFolderMeta(BACKUP_FOLDER_NAME)
        val files = fileList.files

        return when (files.size > 0) {
            true -> files[0]
            false -> drive.createFolder(BACKUP_FOLDER_NAME)
        }
    }
}
