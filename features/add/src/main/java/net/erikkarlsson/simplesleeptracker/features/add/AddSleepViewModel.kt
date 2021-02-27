package net.erikkarlsson.simplesleeptracker.features.add

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.launch
import net.erikkarlsson.simplesleeptracker.core.ReduxViewModel
import net.erikkarlsson.simplesleeptracker.core.livedata.Event
import net.erikkarlsson.simplesleeptracker.domain.DateTimeProvider
import net.erikkarlsson.simplesleeptracker.features.add.domain.AddSleepTask
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AddSleepViewModel @Inject constructor(
        private val addSleepTask: AddSleepTask,
        dateTimeProvider: DateTimeProvider,
        @Named("sleepAddedEvents") private val sleepAddedEvents: BroadcastChannel<Event<Unit>>
) : ReduxViewModel<AddSleepState>(AddSleepState()) {

    init {
        val startDate = dateTimeProvider.now().minusDays(1).toLocalDate()
        val startTime = LocalTime.of(DEFAULT_START_TIME_HOUR, DEFAULT_START_TIME_MINUTE)
        val endTime = LocalTime.of(DEFAULT_END_TIME_HOUR, DEFAULT_END_TIME_MINUTE)
        val zoneOffset = dateTimeProvider.now().offset

        viewModelScope.launchSetState {
            copy(startDate = startDate, startTime = startTime, endTime = endTime, zoneOffset = zoneOffset)
        }
    }

    fun saveClick() {
        viewModelScope.withState { state ->
            viewModelScope.launch {
                addSleepTask.completable(AddSleepTask.Params(state.sleep))
                sleepAddedEvents.send(Event(Unit))
            }

            viewModelScope.launchSetState {
                copy(isSaveSuccess = true)
            }
        }
    }

    fun pickStartDate(startDate: LocalDate) {
        viewModelScope.launchSetState {
            copy(startDate = startDate)
        }
    }

    fun pickTimeAsleep(timeAsleep: LocalTime) {
        viewModelScope.launchSetState {
            copy(startTime = timeAsleep)
        }
    }

    fun pickTimeAwake(timeAwake: LocalTime) {
        viewModelScope.launchSetState {
            copy(endTime = timeAwake)
        }
    }

}
