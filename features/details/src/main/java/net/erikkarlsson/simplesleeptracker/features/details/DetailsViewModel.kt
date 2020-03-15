package net.erikkarlsson.simplesleeptracker.features.details

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch
import net.erikkarlsson.simplesleeptracker.core.MvRxViewModel
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.features.details.domain.DeleteSleepTask
import net.erikkarlsson.simplesleeptracker.features.details.domain.UpdateStartDateTask
import net.erikkarlsson.simplesleeptracker.features.details.domain.UpdateTimeAsleepTask
import net.erikkarlsson.simplesleeptracker.features.details.domain.UpdateTimeAwakeTask
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

class DetailsViewModel @AssistedInject constructor(
        @Assisted state: DetailsState,
        sleepRepository: SleepDataSource,
        private val updateStartDateTask: UpdateStartDateTask,
        private val updateTimeAsleepTask: UpdateTimeAsleepTask,
        private val updateTimeAwakeTask: UpdateTimeAwakeTask,
        private val deleteSleepTask: DeleteSleepTask
) : MvRxViewModel<DetailsState>(state) {

    init {
        viewModelScope.launch {
            sleepRepository.getSleepFlow(state.sleepId).execute {
                copy(sleep = it)
            }
        }
    }

    fun deleteClick() {
        withState { state ->
            viewModelScope.launch {
                deleteSleepTask.completable(DeleteSleepTask.Params(state.sleepId))
                setState { copy(isDeleted = true) }
            }
        }
    }

    fun pickedStartDate(startDate: LocalDate) {
        withState {
            viewModelScope.launch {
                updateStartDateTask.completable(UpdateStartDateTask.Params(it.sleepId, startDate))
            }
        }
    }

    fun pickedTimeAsleep(timeAsleep: LocalTime) {
        withState {
            viewModelScope.launch {
                updateTimeAsleepTask.completable(UpdateTimeAsleepTask.Params(it.sleepId, timeAsleep))
            }
        }
    }

    fun pickedTimeAwake(timeAwake: LocalTime) {
        withState {
            viewModelScope.launch {
                updateTimeAwakeTask.completable(UpdateTimeAwakeTask.Params(it.sleepId, timeAwake))
            }
        }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(state: DetailsState): DetailsViewModel
    }

    companion object : MvRxViewModelFactory<DetailsViewModel, DetailsState> {
        override fun create(viewModelContext: ViewModelContext, state: DetailsState): DetailsViewModel? {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<DetailFragment>()
            return fragment.viewModelFactory.create(state)
        }
    }

}
