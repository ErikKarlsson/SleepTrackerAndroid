package net.erikkarlsson.simplesleeptracker.features.diary

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import net.erikkarlsson.simplesleeptracker.core.MvRxViewModel
import net.erikkarlsson.simplesleeptracker.domain.task.ObservableTask
import net.erikkarlsson.simplesleeptracker.features.diary.domain.GetSleepDiaryTask

class DiaryViewModel @AssistedInject constructor(
        @Assisted val initialState: DiaryState,
        getSleepDiaryTask: GetSleepDiaryTask)
    : MvRxViewModel<DiaryState>(initialState) {

    init {
        getSleepDiaryTask.observable(ObservableTask.None())
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
