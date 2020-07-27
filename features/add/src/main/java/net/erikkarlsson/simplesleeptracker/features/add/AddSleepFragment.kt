package net.erikkarlsson.simplesleeptracker.features.add

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import dagger.hilt.android.AndroidEntryPoint
import net.erikkarlsson.simplesleeptracker.core.util.clicksDebounce
import net.erikkarlsson.simplesleeptracker.core.util.formatDateDisplayName2
import net.erikkarlsson.simplesleeptracker.core.util.formatHoursMinutes
import net.erikkarlsson.simplesleeptracker.dateutil.formatHHMM
import net.erikkarlsson.simplesleeptracker.features.add.databinding.FragmentAddSleepBinding
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

@AndroidEntryPoint
class AddSleepFragment : Fragment() {

    private val viewModel: AddSleepViewModel by viewModels()

    var timePickerDialog: TimePickerDialog? = null
    var datePickDialog: DatePickerDialog? = null

    private var state = AddSleepState.empty()

    private lateinit var toolbar: Toolbar

    private lateinit var binding: FragmentAddSleepBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAddSleepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolbar)

        val activity = requireActivity() as AppCompatActivity

        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val closeIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_clear_24px)
        closeIcon?.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.white),
                PorterDuff.Mode.SRC_ATOP)
        activity.supportActionBar?.setHomeAsUpIndicator(closeIcon)

        toolbar.setTitle(getString(R.string.add_sleep_session))

        viewModel.liveData.observe(viewLifecycleOwner, ::render)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.add_sleep_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.save_sleep) {
            viewModel.saveClick()
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
        pickDate(state.startDate,
                OnDateSetListener { _, year, month, dayOfMonth ->
                    onStartDateSet(year, month, dayOfMonth)
                })
    }

    private fun onTimeAsleepClick() {
        pickTime(state.startTime,
                OnTimeSetListener { _, hour, minute ->
                    onTimeAsleepSet(hour, minute)
                })
    }

    private fun onTimeAwakeClick() {
        pickTime(state.endTime,
                OnTimeSetListener { _, hour, minute ->
                    onTimeAwakeSet(hour, minute)
                })
    }

    private fun onStartDateSet(year: Int, month: Int, dayOfMonth: Int) {
        viewModel.pickStartDate(LocalDate.of(year, month + 1, dayOfMonth))
    }

    private fun onTimeAsleepSet(hour: Int, minute: Int) {
        viewModel.pickTimeAsleep(LocalTime.of(hour, minute))
    }

    private fun onTimeAwakeSet(hour: Int, minute: Int) {
        viewModel.pickTimeAwake(LocalTime.of(hour, minute))
    }

    private fun pickDate(localDate: LocalDate,
                         onDateSetListener: OnDateSetListener) {
        datePickDialog = DatePickerDialog(
                requireContext(),
                onDateSetListener,
                localDate.year,
                localDate.monthValue - 1,
                localDate.dayOfMonth)
        datePickDialog?.show()
    }

    private fun pickTime(localTime: LocalTime, onTimeSetListener: OnTimeSetListener) {
        timePickerDialog = TimePickerDialog(
                requireContext(),
                onTimeSetListener,
                localTime.hour,
                localTime.minute,
                true)
        timePickerDialog?.show()
    }

    private fun render(state: AddSleepState) {
        this.state = state

        binding.startDateText.text = state.startDate.formatDateDisplayName2
        binding.timeAsleepText.text = state.startTime.formatHHMM
        binding.timeAwakeText.text = state.endTime.formatHHMM
        binding.sleptHoursText.text = String.format("%s %s",
                getString(R.string.you_have_slept_for),
                state.hoursSlept.formatHoursMinutes)

        if (state.isSaveSuccess) {
            activity?.finish()
        }
    }

    companion object {
        fun newInstance(): AddSleepFragment = AddSleepFragment()
    }

}
