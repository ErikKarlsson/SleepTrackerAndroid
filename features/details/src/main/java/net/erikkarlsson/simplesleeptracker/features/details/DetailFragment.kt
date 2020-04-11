package net.erikkarlsson.simplesleeptracker.features.details

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.mvrx.*
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.erikkarlsson.simplesleeptracker.core.util.clicksDebounce
import net.erikkarlsson.simplesleeptracker.core.util.formatDateDisplayName2
import net.erikkarlsson.simplesleeptracker.core.util.formatHHMM
import net.erikkarlsson.simplesleeptracker.core.util.formatHoursMinutes
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.features.details.databinding.FragmentDetailsBinding
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime
import timber.log.Timber
import javax.inject.Inject

class DetailFragment : BaseMvRxFragment() {

    private var _state = DetailsState.empty()

    var timePickerDialog: TimePickerDialog? = null
    var datePickDialog: DatePickerDialog? = null

    @Inject
    lateinit var viewModelFactory: DetailsViewModel.Factory

    @Inject
    lateinit var sleepRepository: SleepDataSource

    private val viewModel: DetailsViewModel by fragmentViewModel()

    private lateinit var binding: FragmentDetailsBinding

    override fun invalidate() = withState(viewModel) { state ->
        _state = state
        
        when (state.sleep) {
            is Success -> {
                val sleep = state.sleep.invoke()
                val fromDateString = sleep.fromDate.formatDateDisplayName2
                val toDateString = sleep.toDate?.formatDateDisplayName2
                binding.toolbar.setTitle(toDateString)
                binding.startDateText.text = fromDateString
                binding.timeAsleepText.text = sleep.fromDate.formatHHMM
                binding.timeAwakeText.text = sleep.toDate?.formatHHMM
                binding.sleptHoursText.text = String.format("%s %s",
                        getString(R.string.you_have_slept_for),
                        state.hoursSlept.formatHoursMinutes)
            }
        }

        if (state.isDeleted) {
            requireActivity().finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as AppCompatActivity

        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        CoroutineScope(Dispatchers.Main).launch {
            val sleep = sleepRepository.getCurrent()
            Timber.d("sleep %s", sleep.toString())
        }
    }

    private fun showConfirmDeleteDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.confirm_delete_sleep))
        builder.setPositiveButton(getString(R.string.delete)) { _, _ ->
            onDeleteConfirmClick()
        }

        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun onDeleteConfirmClick() {
        viewModel.deleteClick()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete_sleep) {
            showConfirmDeleteDialog()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        binding.startDateText.clicksDebounce { onStartDateClick() }
        binding.timeAsleepText.clicksDebounce { onTimeAsleepClick() }
        binding.timeAwakeText.clicksDebounce { onTimeAwakeClick() }
    }

    override fun onDestroy() {
        super.onDestroy()
        datePickDialog?.dismiss()
        timePickerDialog?.dismiss()
    }

    private fun onStartDateClick() {
        pickDate(_state.sleep.invoke()!!.fromDate,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    onStartDateSet(year, month, dayOfMonth)
                })
    }

    private fun onTimeAsleepClick() {
        pickTime(_state.sleep.invoke()!!.fromDate.toLocalTime(),
                TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    onTimeAsleepSet(hour, minute)
                })
    }

    private fun onTimeAwakeClick() {
        _state.sleep.invoke()!!.toDate?.let {
            pickTime(it.toLocalTime(),
                    TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                        onTimeAwakeSet(hour, minute)
                    })
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
        fun newInstance(sleepId: Int): DetailFragment =
                DetailFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(
                                MvRx.KEY_ARG,
                                DetailsArgs(sleepId)
                        )
                    }
                }
    }

}
