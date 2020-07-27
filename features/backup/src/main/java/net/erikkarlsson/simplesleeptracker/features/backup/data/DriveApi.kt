package net.erikkarlsson.simplesleeptracker.features.backup.data

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import dagger.hilt.android.qualifiers.ApplicationContext
import net.erikkarlsson.simplesleeptracker.features.backup.BACKUP_MIME_TYPE
import net.erikkarlsson.simplesleeptracker.features.backup.R
import java.io.InputStream
import java.util.Collections.singletonList
import javax.inject.Inject

/**
 * Wrapper for Google Drive REST API.
 */
class DriveApi @Inject constructor(@ApplicationContext private val context: Context) {

    /**
     * Query meta data for existing file.
     *
     * @param fileTitle the file title.
     */

    fun queryFileMeta(fileTitle: String): FileList =
            drive.files()
                    .list()
                    .setQ("name='$fileTitle'")
                    .setSpaces("drive")
                    .execute()

    /**
     * Query meta data for existing folder.
     *
     * @param folderTitle the folder title.
     */
    fun queryFolderMeta(folderTitle: String): FileList =
            drive.files()
                    .list()
                    .setQ("name='$folderTitle' and mimeType='application/vnd.google-apps.folder' and trashed=false")
                    .setSpaces("drive")
                    .execute()

    /**
     * Open existing file for reading.
     */
    fun openFile(fileId: String): InputStream =
            drive.files().get(fileId)
                    .executeMediaAsInputStream()

    /**
     * Create new folder on drive.
     *
     * @param folderTitle the folder title.
     */
    fun createFolder(folderTitle: String): File {
        val fileMetadata = File()
                .setName(folderTitle)
                .setMimeType("application/vnd.google-apps.folder")

        return drive.files().create(fileMetadata)
                .setFields("id")
                .execute()
    }

    /**
     * Create new file on drive.
     *
     * @param folder the folder to put the file in.
     * @param file the file to be created.
     * @param mimeType the type of file to be created.
     */
    fun createFile(folderId: String,
                   file: java.io.File,
                   mimeType: String) {
        val fileMetadata = File()
                .setName(file.name)
                .setParents(singletonList(folderId))

        val mediaContent = FileContent(mimeType, file)

        drive.files().create(fileMetadata, mediaContent)
                .setFields("id, parents")
                .execute()
    }

    /**
     * Overwrite content of existing drive file.
     *
     * @param driveFile the existing file on drive.
     * @param file the new file content.
     */
    fun commitContent(fileId: String,
                      file: java.io.File) {
        val metadata = File().setName(file.name)
        val content = FileContent(BACKUP_MIME_TYPE, file)

        drive.files()
                .update(fileId, metadata, content)
                .execute()
    }

    private val drive: Drive
        get() = Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                GsonFactory(),
                credential)
                .setApplicationName(context.getString(R.string.app_name))
                .build();

    private val credential: GoogleAccountCredential
        get() = GoogleAccountCredential.usingOAuth2(context, setOf(DriveScopes.DRIVE_FILE))
                .setSelectedAccount(googleAccount.account)

    private val googleAccount: GoogleSignInAccount
        get() {
            val account = GoogleSignIn.getLastSignedInAccount(context)

            if (account == null) {
                // Not logged in to Google account.
                throw IllegalStateException("Not logged in to Google Account")
            }

            return account
        }

}
