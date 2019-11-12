package net.erikkarlsson.simplesleeptracker.features.diary.recycler

import android.content.res.Resources
import net.erikkarlsson.simplesleeptracker.domain.DateTimeProvider
import net.erikkarlsson.simplesleeptracker.domain.entity.SleepDiary
import net.erikkarlsson.simplesleeptracker.features.diary.R
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

private val monthPattern = DateTimeFormatter.ofPattern("MMMM")

class RecyclerSectionCallbackFactory @Inject constructor(
        private val resources: Resources,
        private val dateTimeProvider: DateTimeProvider) {

    fun create(sleepDiary: SleepDiary): RecyclerSectionItemDecoration.SectionCallback {
        return object : RecyclerSectionItemDecoration.SectionCallback {

            val sleepList = sleepDiary.pagedSleep

            override fun isSection(position: Int): Boolean {
                try {
                    val isFirstPosition = position == 0
                    val isSameMonth = !isFirstPosition && sleepList[position]
                            ?.fromDate?.month == sleepList[position - 1]?.fromDate?.month
                    val isSameYear = !isFirstPosition && sleepList[position]
                            ?.fromDate?.year == sleepList[position - 1]?.fromDate?.year

                    return isFirstPosition || !isSameMonth || !isSameYear
                } catch (e: IndexOutOfBoundsException) {
                    return false
                }
            }

            override fun getMonthSectionHeader(position: Int): CharSequence {
                try {
                    val fromDate = sleepList[position]?.fromDate ?: OffsetDateTime.MIN

                    val year = fromDate.year
                    val currentYear = dateTimeProvider.now().year
                    val monthString = fromDate.format(monthPattern).toString()

                    return if (year == currentYear) {
                        monthString
                    } else {
                        String.format("%s %d", monthString, year)
                    }
                } catch (e: IndexOutOfBoundsException) {
                    return ""
                }
            }

            override fun getNightsSectionHeader(position: Int): CharSequence {
                try {
                    val fromDate = sleepList[position]?.fromDate

                    if (fromDate == null) {
                        return ""
                    }

                    val year = fromDate.year
                    val month = fromDate.monthValue
                    val sleepCount = sleepDiary.getSleepCount(year, month)

                    return resources.getQuantityString(R.plurals.nights, sleepCount, sleepCount)
                } catch (e: IndexOutOfBoundsException) {
                    return ""
                }
            }
        }
    }

}
