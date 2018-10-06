package net.erikkarlsson.simplesleeptracker.feature.statistics

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import net.erikkarlsson.simplesleeptracker.domain.DateTimeProvider
import net.erikkarlsson.simplesleeptracker.feature.statistics.item.StatisticsItemFragment
import net.erikkarlsson.simplesleeptracker.util.formatDateShort
import net.erikkarlsson.simplesleeptracker.util.yearLastTwoDigits
import org.threeten.bp.LocalDate
import org.threeten.bp.format.TextStyle
import java.util.*

class StatisticsAdapter(fragmentManager: FragmentManager,
                        private val dateTimeProvider: DateTimeProvider) : FragmentStatePagerAdapter(fragmentManager) {

    var data: StatisticsItemData = StatisticsItemData.empty()

    override fun getItem(position: Int): Fragment {
        val dateRangePair = data.dataRanges[position]
        val filter = data.filter
        return StatisticsItemFragment.newInstance(dateRangePair, filter)
    }

    override fun getItemPosition(`object`: Any): Int {
        // https://stackoverflow.com/questions/7263291/viewpager-pageradapter-not-updating-the-view/8024557#8024557
        return POSITION_NONE
    }

    override fun getCount(): Int {
        return data.dataRanges.size
    }

    override fun getPageTitle(position: Int): CharSequence? =
            when (data.filter) {
                StatisticsFilter.OVERALL -> ""
                StatisticsFilter.WEEK -> getWeekTitle(position)
                StatisticsFilter.MONTH -> getMonthTitle(position)
                StatisticsFilter.YEAR -> getYearTitle(position)
                StatisticsFilter.NONE -> ""
            }

    private fun getWeekTitle(position: Int): CharSequence? {
        val from = getFromDate(position).formatDateShort
        val to = getToDate(position).formatDateShort
        return String.format("%s - %s", from, to)
    }

    private fun getMonthTitle(position: Int): CharSequence? {
        val date = getFromDate(position)
        val monthString = date.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val year = date.year
        val currentYear = dateTimeProvider.now().year

        return if (year == currentYear) {
            monthString
        } else {
            String.format("%s '%s",
                    monthString,
                    date.yearLastTwoDigits)
        }
    }

    private fun getYearTitle(position: Int): CharSequence? =
            getFromDate(position).year.toString()

    private fun getFromDate(position: Int): LocalDate =
            data.dataRanges.get(position).first.from

    private fun getToDate(position: Int): LocalDate =
            data.dataRanges.get(position).first.to
}