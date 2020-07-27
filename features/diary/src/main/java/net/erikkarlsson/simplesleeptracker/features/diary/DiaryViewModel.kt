package net.erikkarlsson.simplesleeptracker.features.diary

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.erikkarlsson.simplesleeptracker.core.ReduxViewModel
import net.erikkarlsson.simplesleeptracker.domain.task.FlowTask
import net.erikkarlsson.simplesleeptracker.features.diary.domain.GetSleepDiaryTask

class DiaryViewModel @ViewModelInject constructor(
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
