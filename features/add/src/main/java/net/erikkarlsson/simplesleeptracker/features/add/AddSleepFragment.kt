package net.erikkarlsson.simplesleeptracker.features.add

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_add.*
import net.easypark.dateutil.formatHHMM
import net.easypark.dateutil.formatHoursMinutes
import net.erikkarlsson.simplesleeptracker.core.util.clicksThrottle
import net.erikkarlsson.simplesleeptracker.core.util.formatDateDisplayName2
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import javax.inject.Inject

class AddSleepFragment : BaseMvRxFragment() {

    @Inject
    lateinit var viewModelFactory: AddSleepViewModel.Factory

    private val viewModel: AddSleepViewModel by fragmentViewModel()

    var timePickerDialog: TimePickerDialog? = null
    var datePickDialog: DatePickerDialog? = null

    private var state = AddSleepState.empty()

    private val disposables = CompositeDisposable()

    private lateinit var toolbar: Toolbar

    override fun invalidate() = withState(viewModel) { state ->
        this.state = state
        render(state)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add, container, false)
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
        startDateText.clicksThrottle(disposables) { onStartDateClick() }
        timeAsleepText.clicksThrottle(disposables) { onTimeAsleepClick() }
        timeAwakeText.clicksThrottle(disposables) { onTimeAwakeClick() }
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
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
        startDateText.text = state.startDate.formatDateDisplayName2
        timeAsleepText.text = state.startTime.formatHHMM
        timeAwakeText.text = state.endTime.formatHHMM
        sleptHoursText.text = String.format("%s %s",
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
