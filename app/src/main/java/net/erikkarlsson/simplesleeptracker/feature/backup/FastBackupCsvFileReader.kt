package net.erikkarlsson.simplesleeptracker.feature.backup

import com.google.common.collect.ImmutableList
import de.siegmar.fastcsv.reader.CsvReader
import de.siegmar.fastcsv.reader.CsvRow
import net.erikkarlsson.simplesleeptracker.domain.BackupCsvFileReader
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import org.threeten.bp.OffsetDateTime
import java.io.File
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class FastBackupCsvFileReader @Inject constructor() : BackupCsvFileReader {

    override fun read(file: File): ImmutableList<Sleep> {
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
