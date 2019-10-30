package net.erikkarlsson.simplesleeptracker.feature.add

import androidx.lifecycle.MutableLiveData
import com.airbnb.mvrx.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import net.erikkarlsson.simplesleeptracker.MvRxViewModel
import net.erikkarlsson.simplesleeptracker.base.Event
import net.erikkarlsson.simplesleeptracker.domain.DateTimeProvider
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.feature.add.domain.AddSleepTask
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneOffset
import javax.inject.Named

data class AddSleepState(val startDate: LocalDate = LocalDate.MIN,
                         val startTime: LocalTime = LocalTime.MIN,
                         val endTime: LocalTime = LocalTime.MIN,
                         val zoneOffset: ZoneOffset = ZoneOffset.MIN,
                         val isSaveSuccess: Boolean = false) : MvRxState {

    val hoursSlept: Float
        get() = sleep.hours

    val sleep: Sleep
        get() = Sleep.from(startDate = startDate,
                startTime = startTime,
                endTime = endTime,
                zoneOffset = zoneOffset)

    companion object {
        fun empty() = AddSleepState()
    }
}

class AddSleepViewModel @AssistedInject constructor(
        @Assisted state: AddSleepState,
        private val addSleepTask: AddSleepTask,
        dateTimeProvider: DateTimeProvider,
        @Named("sleepAddedEvents") private val sleepAddedEvents: MutableLiveData<Event<Unit>>
) : MvRxViewModel<AddSleepState>(state) {

    init {
        val startDate = dateTimeProvider.now().minusDays(1).toLocalDate()
        val startTime = LocalTime.of(DEFAULT_START_TIME_HOUR, DEFAULT_START_TIME_MINUTE)
        val endTime = LocalTime.of(DEFAULT_END_TIME_HOUR, DEFAULT_END_TIME_MINUTE)
        val zoneOffset = dateTimeProvider.now().offset

        setState {
            copy(startDate = startDate, startTime = startTime, endTime = endTime, zoneOffset = zoneOffset)
        }
    }

    fun saveClick() {
        withState { state ->
            addSleepTask.execute(AddSleepTask.Params(state.sleep))
                    .execute {
                        when (it) {
                            is Success -> {
                                sleepAddedEvents.postValue(Event(Unit))
                                copy(isSaveSuccess = true)
                            }
                            else -> copy()
                        }
                    }
        }
    }

    fun pickStartDate(startDate: LocalDate) {
        setState {
            copy(startDate = startDate)
        }
    }

    fun pickTimeAsleep(timeAsleep: LocalTime) {
        setState {
            copy(startTime = timeAsleep)
        }
    }

    fun pickTimeAwake(timeAwake: LocalTime) {
        setState {
            copy(endTime = timeAwake)
        }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(state: AddSleepState): AddSleepViewModel
    }

    companion object : MvRxViewModelFactory<AddSleepViewModel, AddSleepState> {
        override fun create(viewModelContext: ViewModelContext, state: AddSleepState): AddSleepViewModel? {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<AddSleepFragment>()
            return fragment.viewModelFactory.create(state)
        }
    }

}
