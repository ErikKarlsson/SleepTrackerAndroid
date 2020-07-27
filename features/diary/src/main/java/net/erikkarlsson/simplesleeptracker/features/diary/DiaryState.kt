package net.erikkarlsson.simplesleeptracker.features.diary

import net.erikkarlsson.simplesleeptracker.domain.entity.SleepDiary

data class DiaryState(val sleepDiary: SleepDiary? = null) {
    val isItemsFound = sleepDiary != null && sleepDiary.pagedSleep.size > 0
}

