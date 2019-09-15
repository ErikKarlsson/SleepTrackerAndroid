package net.erikkarlsson.simplesleeptracker.feature.add

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.toolbar.*
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import net.erikkarlsson.simplesleeptracker.elm.ElmViewModel
import net.erikkarlsson.simplesleeptracker.util.clicksThrottle
import net.erikkarlsson.simplesleeptracker.util.formatDateDisplayName2
import net.erikkarlsson.simplesleeptracker.util.formatHHMM
import net.erikkarlsson.simplesleeptracker.util.formatHoursMinutes
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import javax.inject.Inject

class AddSleepActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    var timePickerDialog: TimePickerDialog? = null
    var datePickDialog: DatePickerDialog? = null

    private val viewModel: AddSleepViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(AddSleepViewModel::class.java)
    }

    private var state = AddSleepState.empty()

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.fragment_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val closeIcon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_baseline_clear_24px)
        closeIcon?.setColorFilter(ContextCompat.getColor(applicationContext, android.R.color.white),
                PorterDuff.Mode.SRC_ATOP)
        supportActionBar?.setHomeAsUpIndicator(closeIcon)

        toolbar.setTitle(getString(R.string.add_sleep_session))

        viewModel.state().observe(this, Observer { addSleepState ->
            addSleepState?.let {
                state = it
                render(it)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_sleep_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.save_sleep) {
            viewModel.dispatch(SaveClick)
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
        viewModel.dispatch(PickedStartDate(LocalDate.of(year, month + 1, dayOfMonth)))
    }

    private fun onTimeAsleepSet(hour: Int, minute: Int) {
        viewModel.dispatch(PickedTimeAsleep(LocalTime.of(hour, minute)))
    }

    private fun onTimeAwakeSet(hour: Int, minute: Int) {
        viewModel.dispatch(PickedTimeAwake(LocalTime.of(hour, minute)))
    }

    private fun pickDate(localDate: LocalDate,
                         onDateSetListener: OnDateSetListener) {
        datePickDialog = DatePickerDialog(
                this,
                onDateSetListener,
                localDate.year,
                localDate.monthValue - 1,
                localDate.dayOfMonth)
        datePickDialog?.show()
    }

    private fun pickTime(localTime: LocalTime, onTimeSetListener: OnTimeSetListener) {
        timePickerDialog = TimePickerDialog(
                this,
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
            finish()
        }
    }

}

class AddSleepViewModel @Inject constructor(detailComponent: AddSleepComponent) :
        ElmViewModel<AddSleepState, AddSleepMsg, AddSleepCmd>(detailComponent)
