package net.erikkarlsson.simplesleeptracker.features.diary

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.erikkarlsson.simplesleeptracker.core.ReduxViewModel
import net.erikkarlsson.simplesleeptracker.domain.task.FlowTask
import net.erikkarlsson.simplesleeptracker.features.diary.domain.GetSleepDiaryTask
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
        getSleepDiaryTask: GetSleepDiaryTask)
    : ReduxViewModel<DiaryState>(DiaryState()) {

    init {
        viewModelScope.launch {
            getSleepDiaryTask.flow(FlowTask.None())
                    .collectAndSetState {
                        copy(sleepDiary = it)
                    }
        }
    }

}
