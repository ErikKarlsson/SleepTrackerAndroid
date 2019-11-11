package net.erikkarlsson.simplesleeptracker.features.backup

import com.google.common.collect.ImmutableList
import de.siegmar.fastcsv.writer.CsvWriter
import net.erikkarlsson.simplesleeptracker.domain.BackupCsvFileWriter
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import java.io.File
import java.io.PrintWriter
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Named

class FastBackupCsvFileWriter
@Inject constructor(@Named("filePath") private val filePath: String)
    : BackupCsvFileWriter {

    override fun write(sleepList: ImmutableList<Sleep>): File {
        val file = File("$filePath/$BACKUP_FILE_NAME")

        // Clear file contents
        val writer = PrintWriter(file)
        writer.close()

        // Write file contents
        val csvWriter = CsvWriter()
        csvWriter.setAlwaysDelimitText(true)
        csvWriter.append(file, StandardCharsets.UTF_8)
                .use { csvAppender ->
                    // Header
                    csvAppender.appendLine("From", "To", "Hours")

                    for (sleep in sleepList) {
                        // Values
                        csvAppender.appendLine(sleep.fromDate.toString(),
                                               sleep.toDate.toString(),
                                               sleep.hours.toString())
                    }
                }

        return file
    }

}
