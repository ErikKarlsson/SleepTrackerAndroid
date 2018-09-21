package net.erikkarlsson.simplesleeptracker.feature.statistics

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import net.erikkarlsson.simplesleeptracker.feature.statistics.item.StatisticsItemFragment

class StatisticsAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

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

}