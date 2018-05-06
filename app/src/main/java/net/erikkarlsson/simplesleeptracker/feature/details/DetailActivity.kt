package net.erikkarlsson.simplesleeptracker.feature.details

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_details.*
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

const val INTENT_SLEEP_ID = "INTENT_SLEEP_ID"

fun Context.DetailIntent(id: Int): Intent {
    return Intent(this, DetailActivity::class.java).apply {
        putExtra(INTENT_SLEEP_ID, id)
    }
}

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

        viewModel.state().observe(this, Observer { render(it) })
        viewModel.dispatch(LoadDetailIntent(getSleepId()))

        startDateText.clicksThrottle(disposables) { onStartDateClick() }
        timeAsleepText.clicksThrottle(disposables) { onTimeAsleepClick() }
        timeAwakeText.clicksThrottle(disposables) { onTimeAwakeClick() }
    }

    override fun onStop() {
        super.onStop()
        disposables.dispose()
    }

    fun getSleepId(): Int {
        return intent.getIntExtra(INTENT_SLEEP_ID, 0)
    }

    private fun onStartDateClick() {
        pickDate(state.sleep.fromDate,
                 OnDateSetListener { view, year, month, dayOfMonth ->
                     onStartDatePicked(year, month, dayOfMonth)
                 })
    }

    private fun onStartDatePicked(year: Int, month: Int, dayOfMonth: Int) {
        viewModel.dispatch(PickedStartDate(LocalDate.of(year, month + 1, dayOfMonth)))
    }

    private fun onTimeAsleepClick() {
        pickTime(state.sleep.fromDate.toLocalTime(),
                 OnTimeSetListener { view, hour, minute -> Log.d("Original", "Got clicked") })
    }

    private fun onTimeAwakeClick() {
        state.sleep.toDate?.let {
            pickTime(it.toLocalTime(),
                     OnTimeSetListener { view, hour, minute -> Log.d("Original", "Got clicked") })
        }
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
                startDateText.text = state.sleep.fromDate.formatDateDisplayName2
                timeAsleepText.text = state.sleep.fromDate.formatHHMM
                timeAwakeText.text = state.sleep.toDate?.formatHHMM
            }
        }
    }
}

class DetailViewModel @Inject constructor(detailComponent: DetailComponent) :
        ElmViewModel<DetailState, DetailMsg, DetailCmd>(detailComponent)