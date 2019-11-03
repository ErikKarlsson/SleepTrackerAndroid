package net.erikkarlsson.simplesleeptracker.feature.details

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import net.erikkarlsson.simplesleeptracker.MvRxViewModel
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.feature.details.domain.DeleteSleepTask
import net.erikkarlsson.simplesleeptracker.feature.details.domain.UpdateStartDateTask
import net.erikkarlsson.simplesleeptracker.feature.details.domain.UpdateTimeAsleepTask
import net.erikkarlsson.simplesleeptracker.feature.details.domain.UpdateTimeAwakeTask
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
        sleepRepository.getSleep(state.sleepId)
                .execute {
                    copy(sleep = it)
                }
    }

    fun deleteClick() {
        withState {
            deleteSleepTask.completable(DeleteSleepTask.Params(it.sleepId))
                    .execute { copy(isDeleted = true) }
        }
    }

    fun pickedStartDate(startDate: LocalDate) {
        withState {
            updateStartDateTask.completable(UpdateStartDateTask.Params(it.sleepId, startDate))
                    .execute { copy() }
        }
    }

    fun pickedTimeAsleep(timeAsleep: LocalTime) {
        withState {
            updateTimeAsleepTask.completable(UpdateTimeAsleepTask.Params(it.sleepId, timeAsleep))
                    .execute { copy() }
        }
    }

    fun pickedTimeAwake(timeAwake: LocalTime) {
        withState {
            updateTimeAwakeTask.completable(UpdateTimeAwakeTask.Params(it.sleepId, timeAwake))
                    .execute { copy() }
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
