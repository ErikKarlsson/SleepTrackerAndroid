package net.erikkarlsson.simplesleeptracker.features.diary

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import net.erikkarlsson.simplesleeptracker.domain.entity.SleepDiary

data class DiaryState(val sleepDiary: Async<SleepDiary> = Uninitialized) : MvRxState {

    val isItemsFound = sleepDiary is Success && sleepDiary.invoke().pagedSleep.size > 0
}

