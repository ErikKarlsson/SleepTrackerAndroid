package net.erikkarlsson.simplesleeptracker.statistics

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ArrayAdapter
import com.google.common.collect.ImmutableList
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.itemSelections
import com.jakewharton.rxrelay2.PublishRelay
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_statistics.*
import kotlinx.android.synthetic.main.toolbar.*
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.details.DetailIntent
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.util.*
import timber.log.Timber
import javax.inject.Inject

class StatisticsActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: StatisticsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(StatisticsViewModel::class.java)
    }

    private val disposables = CompositeDisposable()
    private val adapter = SleepAdapter { navigateToDetails(it.id) }
    private val sleepListRelay: PublishRelay<ImmutableList<Sleep>> = PublishRelay.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        setSupportActionBar(toolbar)
        toolbar.setTitle(R.string.app_name)

        val spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.statistic_filter_array, android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
        spinner.itemSelections().subscribe({ selectFilter(it) }).addTo(disposables)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        Observable.merge(toggleSleepButton.clicks(), owlImage.clicks())
            .subscribe({ viewModel.dispatch(ToggleSleepClicked) },
                    { Timber.e(it, "Error merging clicks") })
            .addTo(disposables)

        sleepListRelay.scanMap(ImmutableList.of(), { old, new ->
            val callback = SleepAdapter.DiffCallback(old, new)
            val result = DiffUtil.calculateDiff(callback)
            Pair(new, result)
        })
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ adapter.swapData(it.first, it.second) },
                    { Timber.e(it, "Error calculating diff result") })
            .addTo(disposables)

        viewModel.state().observe(this, Observer { render(it) })
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    private fun render(state: StatisticsState?) {
        state?.let {
            sleepListRelay.accept(state.sleepList)

            val imageRes = if (state.isSleeping) R.drawable.owl_asleep else R.drawable.own_awake
            owlImage.setImageResource(imageRes)

            trackedNightsCard.visibility = if (state.isListEmpty) View.GONE else View.VISIBLE

            with(state.statistics.first) {
                statisticsText.text = if (this.isEmpty) {
                    getString(R.string.no_sleep_tracked)
                } else {
                    String.format("%s: %d\n" +
                            "%s: %s %s\n" +
                            "%s: %d%% %s\n" +
                            "%s: %s, %s\n" +
                            "%s: %s, %s\n" +
                            "%s: %s %s\n%s\n" +
                            "%s: %s %s\n%s",
                            getString(R.string.tracked_nights),
                            sleepCount,
                            getString(R.string.avg_duration),
                            avgSleepHours.formatHoursMinutes,
                            with(state.statistics.avgSleepDiffHours) {
                                if (this == 0f) "" else String.format("(%s)", formatHoursMinutesWithPrefix)
                            },
                            getString(R.string.time_sleeping),
                            timeSleepingPercentage,
                            with(state.statistics.timeSleepingDiffPercentage) {
                                if (this == 0) "" else String.format("(%s)", formatPercentage)
                            },
                            getString(R.string.longest_night),
                            longestSleep.hours.formatHoursMinutes,
                            longestSleep.toDate?.formatDateDisplayName,
                            getString(R.string.shortest_night),
                            shortestSleep.hours.formatHoursMinutes,
                            shortestSleep.toDate?.formatDateDisplayName,
                            getString(R.string.average_bed_time),
                            averageBedTime.formatHHMM,
                            with(state.statistics.avgBedTimeDiffHours) {
                                if (this == 0f) "" else String.format("(%s)", formatHoursMinutesWithPrefix)
                            },
                            averageBedTimeDayOfWeek.formatDisplayName,
                            getString(R.string.average_wake_up_time),
                            averageWakeUpTime.formatHHMM,
                            with(state.statistics.avgWakeUpTimeDiffHours) {
                                if (this == 0f) "" else String.format("(%s)", formatHoursMinutesWithPrefix)
                            },
                            averageWakeUpTimeDayOfWeek.formatDisplayName)
                }
            }
        }
    }

    private fun selectFilter(index: Int) {
        val statisticsFilter = StatisticsFilter.values()[index]
        viewModel.dispatch(StatisticsFilterSelected(statisticsFilter))
    }

    private fun navigateToDetails(id: Int?) {
        id?.let { startActivity(DetailIntent(id)) }
    }

}