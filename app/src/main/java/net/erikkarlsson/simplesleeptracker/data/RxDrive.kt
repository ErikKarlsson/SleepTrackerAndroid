package net.erikkarlsson.simplesleeptracker.data

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.drive.Drive
import com.google.android.gms.drive.DriveClient
import com.google.android.gms.drive.DriveContents
import com.google.android.gms.drive.DriveFile
import com.google.android.gms.drive.DriveFolder
import com.google.android.gms.drive.DriveResourceClient
import com.google.android.gms.drive.MetadataBuffer
import com.google.android.gms.drive.MetadataChangeSet
import com.google.android.gms.drive.query.Filters
import com.google.android.gms.drive.query.Query
import com.google.android.gms.drive.query.SearchableField
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File
import java.util.*
import javax.inject.Inject

/**
 * RxJava wrapper for Google Drive API.
 */
class RxDrive @Inject constructor(private val context: Context) {

    /**
     * Query meta data for existing file.
     *
     * @param fileTitle the file title.
     */
    fun queryFileMeta(fileTitle: String): Single<MetadataBuffer> =
            Single.create { emitter ->
                val query = Query.Builder()
                        .addFilter(Filters.eq(SearchableField.TITLE, fileTitle))
                        .build()

                driveResourceClient.query(query)
                        .addOnSuccessListener(emitter::onSuccess)
                        .addOnFailureListener(emitter::onError)
            }

    /**
     * Query meta data for existing folder.
     *
     * @param folderTitle the folder title.
     */
    fun queryFolderMeta(folderTitle: String): Single<MetadataBuffer> =
            Single.create { emitter ->
                val query = Query.Builder().addFilter(Filters.and(
                        Filters.eq(SearchableField.TITLE, folderTitle),
                        Filters.eq(SearchableField.TRASHED, false)))
                        .build()

                driveResourceClient.query(query)
                        .addOnSuccessListener(emitter::onSuccess)
                        .addOnFailureListener(emitter::onError)
            }

    fun openFile(driveFile: DriveFile): Single<DriveContents> =
            Single.create { emitter ->
                driveResourceClient.openFile(driveFile, DriveFile.MODE_READ_ONLY)
                        .addOnSuccessListener(emitter::onSuccess)
                        .addOnFailureListener(emitter::onError)
            }

    /**
     * Create new folder on drive.
     *
     * @param folderTitle the folder title.
     */
    fun createFolder(folderTitle: String): Single<DriveFolder> =
            Single.create { emitter ->
                val rootFolderTask = driveResourceClient.getRootFolder()

                rootFolderTask.continueWithTask {
                    val parent = rootFolderTask.getResult()
                    val folderChangeSet = MetadataChangeSet.Builder()
                            .setTitle(folderTitle)
                            .setMimeType(DriveFolder.MIME_TYPE)
                            .build()

                    val createFolderTask = driveResourceClient.createFolder(parent, folderChangeSet)
                    createFolderTask
                }
                        .addOnSuccessListener { emitter.onSuccess(it) }
                        .addOnFailureListener(emitter::onError)
            }

    /**
     * Create new file on drive.
     *
     * @param folder the folder to put the file in.
     * @param file the file to be created.
     * @param mimeType the type of file to be created.
     */
    fun createFile(folder: DriveFolder,
                   file: File,
                   mimeType: String): Completable =
            Completable.create { emitter ->
                val createContentsTask = driveResourceClient.createContents()

                createContentsTask
                        .continueWithTask {
                            val contents = createContentsTask.getResult()
                            val outputStream = contents.outputStream

                            outputStream.write(file.readBytes())

                            val changeSet = MetadataChangeSet.Builder()
                                    .setTitle(file.name)
                                    .setMimeType(mimeType)
                                    .build()

                            driveResourceClient.createFile(folder, changeSet, contents)
                        }
                        .addOnSuccessListener { emitter.onComplete() }
                        .addOnFailureListener(emitter::onError)
            }

    /**
     * Overwrite content of existing drive file.
     *
     * @param driveFile the existing file on drive.
     * @param file the new file content.
     */
    fun commitContent(driveFile: DriveFile,
                      file: File): Completable =
            Completable.create { emitter ->
                val openTask = driveResourceClient.openFile(driveFile, DriveFile.MODE_WRITE_ONLY)

                openTask.continueWithTask {
                    val driveContents = openTask.getResult()
                    val outputStream = driveContents.outputStream
                    outputStream.write(file.readBytes())

                    val changeSet = MetadataChangeSet.Builder()
                            .setLastViewedByMeDate(Date())
                            .build()
                    driveResourceClient.commitContents(driveContents, changeSet)
                }
                        .addOnSuccessListener { emitter.onComplete() }
                        .addOnFailureListener(emitter::onError)
            }


    /**
     * Drive files are stored locally before being synced to cloud.
     * Request sync to happen immediately.
     */
    fun requestSync(): Completable = Completable.fromCallable { driveClient.requestSync() }

    private val driveClient: DriveClient
        get() = Drive.getDriveClient(context, googleAccount)

    private val driveResourceClient: DriveResourceClient
        get() = Drive.getDriveResourceClient(context, googleAccount)

    private val googleAccount: GoogleSignInAccount
        get() {
            val account = GoogleSignIn.getLastSignedInAccount(context)

            if (account == null) {
                // Not logged in to Google account.
                throw RuntimeException("Not logged in to Google Account")
            }

            return account
        }

}
