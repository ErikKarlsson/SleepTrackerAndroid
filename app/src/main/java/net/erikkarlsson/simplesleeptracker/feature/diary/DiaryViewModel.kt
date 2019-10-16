package net.erikkarlsson.simplesleeptracker.feature.diary

import com.airbnb.mvrx.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import net.erikkarlsson.simplesleeptracker.MvRxViewModel
import net.erikkarlsson.simplesleeptracker.domain.entity.SleepDiary
import net.erikkarlsson.simplesleeptracker.domain.task.ObservableTask
import net.erikkarlsson.simplesleeptracker.feature.diary.domain.GetSleepDiaryTask


data class DiaryState(val sleepDiary: Async<SleepDiary> = Uninitialized) : MvRxState {

    val isItemsFound = sleepDiary is Success && sleepDiary.invoke().pagedSleep.size > 0
}

class DiaryViewModel @AssistedInject constructor(
        @Assisted val initialState: DiaryState,
        getSleepDiaryTask: GetSleepDiaryTask)
    : MvRxViewModel<DiaryState>(initialState) {

    init {
        getSleepDiaryTask.execute(ObservableTask.None())
                .execute {
                    copy(sleepDiary = it)
                }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: DiaryState): DiaryViewModel
    }

    companion object : MvRxViewModelFactory<DiaryViewModel, DiaryState> {
        override fun create(viewModelContext: ViewModelContext, state: DiaryState): DiaryViewModel? {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<DiaryFragment>()
            return fragment.viewModelFactory.create(state)
        }
    }

}
