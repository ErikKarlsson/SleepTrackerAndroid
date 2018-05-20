package net.erikkarlsson.simplesleeptracker.feature.details

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.toolbar.*
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.elm.ElmViewModel
import net.erikkarlsson.simplesleeptracker.util.clicksThrottle
import net.erikkarlsson.simplesleeptracker.util.formatDateDisplayName2
import net.erikkarlsson.simplesleeptracker.util.formatHHMM
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

class DetailActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: DetailViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(DetailViewModel::class.java)
    }

    private var state = DetailState.empty()

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val sleepId = DetailActivityArgs.fromBundle(intent.extras).sleepId

        viewModel.state().observe(this, Observer { render(it) })
        viewModel.dispatch(LoadDetailIntent(sleepId))

        startDateText.clicksThrottle(disposables) { onStartDateClick() }
        timeAsleepText.clicksThrottle(disposables) { onTimeAsleepClick() }
        timeAwakeText.clicksThrottle(disposables) { onTimeAwakeClick() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.delete_sleep) {
            showConfirmDeleteDialog()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showConfirmDeleteDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.confirm_delete_sleep))
        builder.setPositiveButton(getString(R.string.delete)) { dialog, id ->
            onDeleteConfirmClick()
        }

        builder.setNegativeButton(getString(R.string.cancel)) { dialog, id ->
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun onDeleteConfirmClick() {
        viewModel.dispatch(DeleteClick)
        finish()
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    private fun onStartDateClick() {
        pickDate(state.sleep.fromDate, OnDateSetListener(this::onStartDateSet))
    }

    private fun onTimeAsleepClick() {
        pickTime(state.sleep.fromDate.toLocalTime(), OnTimeSetListener(this::onTimeAsleepSet))
    }

    private fun onTimeAwakeClick() {
        state.sleep.toDate?.let {
            pickTime(it.toLocalTime(), OnTimeSetListener(this::onTimeAwakeSet))
        }
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

    private fun pickDate(offsetDateTime: OffsetDateTime,
                         onDateSetListener: OnDateSetListener) {
        DatePickerDialog(
                this,
                onDateSetListener,
                offsetDateTime.year,
                offsetDateTime.monthValue - 1,
                offsetDateTime.dayOfMonth)
                .show()
    }

    private fun pickTime(localTime: LocalTime, onTimeSetListener: OnTimeSetListener) {
        TimePickerDialog(
                this,
                onTimeSetListener,
                localTime.hour,
                localTime.minute,
                true)
                .show()
    }

    private fun render(nextState: DetailState?) {
        nextState?.let {
            state = nextState
            if (state.sleep != Sleep.empty()) {
                val dateString = state.sleep.fromDate.formatDateDisplayName2
                toolbar.setTitle(dateString)
                startDateText.text = dateString
                timeAsleepText.text = state.sleep.fromDate.formatHHMM
                timeAwakeText.text = state.sleep.toDate?.formatHHMM
            }
        }
    }
}

class DetailViewModel @Inject constructor(detailComponent: DetailComponent) :
        ElmViewModel<DetailState, DetailMsg, DetailCmd>(detailComponent)