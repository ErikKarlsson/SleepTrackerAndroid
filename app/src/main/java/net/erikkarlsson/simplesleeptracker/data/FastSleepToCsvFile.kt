package net.erikkarlsson.simplesleeptracker.data

import com.google.common.collect.ImmutableList
import de.siegmar.fastcsv.reader.CsvReader
import de.siegmar.fastcsv.reader.CsvRow
import de.siegmar.fastcsv.writer.CsvWriter
import net.erikkarlsson.simplesleeptracker.base.BACKUP_FILE_NAME
import net.erikkarlsson.simplesleeptracker.domain.SleepToCsvFile
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import org.threeten.bp.OffsetDateTime
import java.io.File
import java.io.PrintWriter
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Named

class FastSleepToCsvFile @Inject constructor(@Named("filePath") private val filePath: String) : SleepToCsvFile {

    override fun write(sleepList: ImmutableList<Sleep>): File {
        val file = File("$filePath/$BACKUP_FILE_NAME")

        // Clear file contents
        val writer = PrintWriter(file)
        writer.close()

        // Write file contents
        val csvWriter = CsvWriter()
        csvWriter.setAlwaysDelimitText(true)
        csvWriter.append(file, StandardCharsets.UTF_8)
                .use({ csvAppender ->
                         // Header
                         csvAppender.appendLine("From", "To", "Hours")

                         for (sleep in sleepList) {
                             // Values
                             csvAppender.appendLine(sleep.fromDate.toString(),
                                                    sleep.toDate.toString(),
                                                    sleep.hours.toString())
                         }
                     })

        return file
    }

    override fun read(file: File): ImmutableList<Sleep> {
        val s = file.readText()
        val csvReader = CsvReader()
        csvReader.setContainsHeader(true)

        val sleepListBuilder = ImmutableList.builder<Sleep>()

        csvReader.parse(file, StandardCharsets.UTF_8).use { csvParser ->
            var row: CsvRow?

            do {
                row = csvParser.nextRow()

                if (row == null) {
                    break
                }

                val fromDate = OffsetDateTime.parse(row.getField("From"))
                val toDate = OffsetDateTime.parse(row.getField("To"))
                val sleep = Sleep(fromDate = fromDate, toDate = toDate)

                sleepListBuilder.add(sleep)
            } while (true)
        }

        return sleepListBuilder.build()
    }

}
