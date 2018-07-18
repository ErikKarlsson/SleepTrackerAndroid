package net.erikkarlsson.simplesleeptracker.feature.diary

import android.content.res.Resources
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.domain.entity.SleepDiary
import javax.inject.Inject

class RecyclerSectionItemDecorationFactory @Inject constructor(
        private val resources: Resources,
        private val recyclerSectionCallbackFactory: RecyclerSectionCallbackFactory) {

    fun create(sleepDiary: SleepDiary): RecyclerSectionItemDecoration {
        val callback = recyclerSectionCallbackFactory.create(sleepDiary)
        val height = resources.getDimensionPixelSize(R.dimen.recycler_section_header_height)
        return RecyclerSectionItemDecoration(height, true, callback)
    }

}