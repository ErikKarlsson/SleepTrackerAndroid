package net.erikkarlsson.simplesleeptracker.features.details

import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch
import net.erikkarlsson.simplesleeptracker.core.ReduxViewModel
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.features.details.domain.DeleteSleepTask
import net.erikkarlsson.simplesleeptracker.features.details.domain.UpdateStartDateTask
import net.erikkarlsson.simplesleeptracker.features.details.domain.UpdateTimeAsleepTask
import net.erikkarlsson.simplesleeptracker.features.details.domain.UpdateTimeAwakeTask
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

class DetailsViewModel @AssistedInject constructor(
        @Assisted initialState: DetailsState,
        sleepRepository: SleepDataSource,
        private val updateStartDateTask: UpdateStartDateTask,
        private val updateTimeAsleepTask: UpdateTimeAsleepTask,
        private val updateTimeAwakeTask: UpdateTimeAwakeTask,
        private val deleteSleepTask: DeleteSleepTask
) : ReduxViewModel<DetailsState>(initialState) {

    init {
        viewModelScope.launch {
            sleepRepository.getSleepFlow(initialState.sleepId).collectAndSetState {
                copy(sleep = it)
            }
        }
    }

    fun deleteClick() {
        viewModelScope.withState { state ->
            viewModelScope.launch {
                deleteSleepTask.completable(DeleteSleepTask.Params(state.sleepId))
                setState { copy(isDeleted = true) }
            }
        }
    }

    fun pickedStartDate(startDate: LocalDate) {
        viewModelScope.withState {
            viewModelScope.launch {
                updateStartDateTask.completable(UpdateStartDateTask.Params(it.sleepId, startDate))
            }
        }
    }

    fun pickedTimeAsleep(timeAsleep: LocalTime) {
        viewModelScope.withState {
            viewModelScope.launch {
                updateTimeAsleepTask.completable(UpdateTimeAsleepTask.Params(it.sleepId, timeAsleep))
            }
        }
    }

    fun pickedTimeAwake(timeAwake: LocalTime) {
        viewModelScope.withState {
            viewModelScope.launch {
                updateTimeAwakeTask.completable(UpdateTimeAwakeTask.Params(it.sleepId, timeAwake))
            }
        }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: DetailsState): DetailsViewModel
    }
}

internal fun DetailsViewModel.Factory.create(
        sleepId: Int
): DetailsViewModel {
    return create(DetailsState(sleepId = sleepId))
}
