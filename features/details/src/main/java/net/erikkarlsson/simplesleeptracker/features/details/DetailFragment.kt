package net.erikkarlsson.simplesleeptracker.features.details

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.erikkarlsson.simplesleeptracker.core.util.*
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.features.details.databinding.FragmentDetailsBinding
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : Fragment() {

    @Inject
    @JvmField
    internal var vmFactory: DetailsViewModel.Factory? = null

    private var _state = DetailsState.empty()

    var timePickerDialog: TimePickerDialog? = null
    var datePickDialog: DatePickerDialog? = null

    @Inject
    lateinit var sleepRepository: SleepDataSource

    private val viewModel: DetailsViewModel by viewModels {
        viewModelProviderFactoryOf {
            vmFactory!!.create(requireArguments().getInt(ARG_KEY_ID))
        }
    }

    private lateinit var binding: FragmentDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

        viewModel.liveData.observe(viewLifecycleOwner, ::render)
    }

    fun render(state: DetailsState) {
        _state = state

        val sleep = state.sleep

        if (sleep != null) {
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

        if (state.isDeleted) {
            requireActivity().finish()
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
        pickDate(_state.sleep!!.fromDate,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    onStartDateSet(year, month, dayOfMonth)
                })
    }

    private fun onTimeAsleepClick() {
        pickTime(_state.sleep!!.fromDate.toLocalTime(),
                TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    onTimeAsleepSet(hour, minute)
                })
    }

    private fun onTimeAwakeClick() {
        _state.sleep!!.toDate?.let {
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
        private const val ARG_KEY_ID = "sleep_id"

        fun newInstance(sleepId: Int): DetailFragment =
                DetailFragment().apply {
                    arguments = bundleOf(ARG_KEY_ID to sleepId)
                }
    }

}
