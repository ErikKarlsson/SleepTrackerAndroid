package net.erikkarlsson.simplesleeptracker.features.details

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import net.erikkarlsson.simplesleeptracker.core.util.viewModelProviderFactoryOf
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : Fragment() {

    @Inject
    internal lateinit var vmFactory: DetailsViewModel.Factory

    private val pendingActions = Channel<DetailsAction>(Channel.BUFFERED)

    var timePickerDialog: TimePickerDialog? = null
    var datePickDialog: DatePickerDialog? = null

    private val viewModel: DetailsViewModel by viewModels {
        viewModelProviderFactoryOf {
            vmFactory.create(requireArguments().getInt(ARG_KEY_ID))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                val viewState by viewModel.liveData.observeAsState()

                if (viewState != null) {
                    Details(viewState!!) {
                        pendingActions.offer(it)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.liveData.observe(this, ::render)

        lifecycleScope.launch {
            pendingActions.consumeAsFlow().collect { action ->
                when (action) {
                    is PickStartDateAction -> onStartDateClick(action.date)
                    is PickTimeAsleepAction -> onTimeAsleepClick(action.time)
                    is PickTimeAwakeAction -> onTimeAwakeClick(action.time)
                    DeleteAction -> viewModel.deleteClick()
                    NavigateUp -> requireActivity().finish()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        datePickDialog?.dismiss()
        timePickerDialog?.dismiss()
    }

    fun render(state: DetailsState) {
        if (state.isDeleted) {
            requireActivity().finish()
        }
    }

    private fun onStartDateClick(fromDate: OffsetDateTime) {
        pickDate(fromDate) { _, year, month, dayOfMonth ->
            onStartDateSet(year, month, dayOfMonth)
        }
    }

    private fun onTimeAsleepClick(time: LocalTime) {
        pickTime(time) { _, hour, minute ->
            onTimeAsleepSet(hour, minute)
        }
    }

    private fun onTimeAwakeClick(time: LocalTime?) {
        time?.let { localTime ->
            pickTime(localTime) { _, hour, minute ->
                onTimeAwakeSet(hour, minute)
            }
        }
    }

    private fun onStartDateSet(year: Int, month: Int, dayOfMonth: Int) {
        viewModel.pickedStartDate(LocalDate.of(year, month + 1, dayOfMonth))
    }

    private fun onTimeAsleepSet(hour: Int, minute: Int) {
        viewModel.pickedTimeAsleep(LocalTime.of(hour, minute))
    }

    private fun onTimeAwakeSet(hour: Int, minute: Int) {
        viewModel.pickedTimeAwake(LocalTime.of(hour, minute))
    }

    private fun pickDate(offsetDateTime: OffsetDateTime,
                         onDateSetListener: DatePickerDialog.OnDateSetListener) {
        datePickDialog = DatePickerDialog(
                requireContext(),
                onDateSetListener,
                offsetDateTime.year,
                offsetDateTime.monthValue - 1,
                offsetDateTime.dayOfMonth)
        datePickDialog?.show()
    }

    private fun pickTime(localTime: LocalTime, onTimeSetListener: TimePickerDialog.OnTimeSetListener) {
        timePickerDialog = TimePickerDialog(
                requireContext(),
                onTimeSetListener,
                localTime.hour,
                localTime.minute,
                true)
        timePickerDialog?.show()
    }

    companion object {
        private const val ARG_KEY_ID = "sleep_id"

        fun newInstance(sleepId: Int): DetailFragment =
                DetailFragment().apply {
                    arguments = bundleOf(ARG_KEY_ID to sleepId)
                }
    }

}
