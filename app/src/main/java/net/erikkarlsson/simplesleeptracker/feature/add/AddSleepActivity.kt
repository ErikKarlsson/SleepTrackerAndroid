package net.erikkarlsson.simplesleeptracker.feature.add

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.TimePicker
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_details.*
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
        setContentView(R.layout.activity_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val closeIcon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_baseline_clear_24px)
        closeIcon?.setColorFilter(ContextCompat.getColor(applicationContext, android.R.color.white),
                                  PorterDuff.Mode.SRC_ATOP)
        supportActionBar?.setHomeAsUpIndicator(closeIcon)

        toolbar.setTitle(getString(R.string.add_sleep_session))

        viewModel.state().observe(this, Observer {
            it?.let {
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
            finish()
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
        pickDate(state.startDate, OnDateSetListener(this::onStartDateSet))
    }

    private fun onTimeAsleepClick() {
        pickTime(state.startTime, OnTimeSetListener(this::onTimeAsleepSet))
    }

    private fun onTimeAwakeClick() {
        pickTime(state.endTime, OnTimeSetListener(this::onTimeAwakeSet))
    }

    private fun onStartDateSet(datePicker: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.dispatch(PickedStartDate(LocalDate.of(year, month + 1, dayOfMonth)))
    }

    private fun onTimeAsleepSet(timePicker: TimePicker, hour: Int, minute: Int) {
        viewModel.dispatch(PickedTimeAsleep(LocalTime.of(hour, minute)))
    }

    private fun onTimeAwakeSet(timePicker: TimePicker, hour: Int, minute: Int) {
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
    }

}

class AddSleepViewModel @Inject constructor(detailComponent: AddSleepComponent) :
        ElmViewModel<AddSleepState, AddSleepMsg, AddSleepCmd>(detailComponent)