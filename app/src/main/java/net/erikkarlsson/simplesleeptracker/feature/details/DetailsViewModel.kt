package net.erikkarlsson.simplesleeptracker.feature.details

import android.os.Parcelable
import com.airbnb.mvrx.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.android.parcel.Parcelize
import net.erikkarlsson.simplesleeptracker.base.MvRxViewModel
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.feature.details.domain.DeleteSleepTask
import net.erikkarlsson.simplesleeptracker.feature.details.domain.UpdateStartDateTask
import net.erikkarlsson.simplesleeptracker.feature.details.domain.UpdateTimeAsleepTask
import net.erikkarlsson.simplesleeptracker.feature.details.domain.UpdateTimeAwakeTask
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

@Parcelize
data class DetailsArgs(val userId: Int) : Parcelable

data class DetailsState(
        val sleepId: Int,
        val sleep: Async<Sleep> = Uninitialized,
        val isDeleted: Boolean = false
) : MvRxState {
    constructor(args: DetailsArgs) : this(sleepId = args.userId)

    val hoursSlept: Float get() = sleep.invoke()?.hours ?: 0f

    companion object {
        fun empty() = DetailsState(0, Uninitialized, false)
    }
}

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
            deleteSleepTask.execute(DeleteSleepTask.Params(it.sleepId))
                    .execute { copy(isDeleted = true) }
        }
    }

    fun pickedStartDate(startDate: LocalDate) {
        withState {
            updateStartDateTask.execute(UpdateStartDateTask.Params(it.sleepId, startDate))
                    .execute { copy() }
        }
    }

    fun pickedTimeAsleep(timeAsleep: LocalTime) {
        withState {
            updateTimeAsleepTask.execute(UpdateTimeAsleepTask.Params(it.sleepId, timeAsleep))
                    .execute { copy() }
        }
    }

    fun pickedTimeAwake(timeAwake: LocalTime) {
        withState {
            updateTimeAwakeTask.execute(UpdateTimeAwakeTask.Params(it.sleepId, timeAwake))
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
