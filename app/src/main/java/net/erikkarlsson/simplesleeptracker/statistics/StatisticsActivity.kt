package net.erikkarlsson.simplesleeptracker.statistics

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxrelay2.PublishRelay
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_statistics.*
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.details.DetailIntent
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import net.erikkarlsson.simplesleeptracker.domain.Sleep
import net.erikkarlsson.simplesleeptracker.domain.Statistics
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
    private val sleepListRelay: PublishRelay<List<Sleep>> = PublishRelay.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        RxView.clicks(toggleSleepButton)
            .subscribe({ viewModel.dispatch(ToggleSleepClicked) })
            .addTo(disposables)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        sleepListRelay.scanMap(emptyList(), { old, new ->
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

            with(state.statistics.first) {
                averageSleepText.text = if (this == Statistics.empty()) {
                    "No sleep tracked yet"
                } else {
                    val bedTimeDaysOfWeek = averageBedTimeDayOfWeek.map { it.formatDisplayName }.joinToString(separator = "\n")
                    val wakeupTimeDaysOfWeek = averageWakeUpTimeDayOfWeek.map { it.formatDisplayName }.joinToString(separator = "\n")

                    String.format("Tracked Nights: %d\n" +
                            "Avg. Duration: %s (%s)\n" +
                            "Time Sleeping: %d%% (%s)\n" +
                            "Longest Night: %s %s\n" +
                            "Shortest Night: %s %s\n" +
                            "Average Bed Time: %s (%s)\n%s\n" +
                            "Average Wake Up Time: %s (%s)\n%s\n",
                            sleepCount,
                            avgSleepHours.formatHHMM,
                            state.statistics.avgSleepDiffHours.formatHHMM,
                            timeSleepingPercentage,
                            state.statistics.timeSleepingDiffPercentage.formatPercentage,
                            longestSleep.hours.formatHHMM,
                            longestSleep.toDate?.formatYYYYMMDD,
                            shortestSleep.hours.formatHHMM,
                            shortestSleep.toDate?.formatYYYYMMDD,
                            averageBedTime.formatHHMM,
                            state.statistics.avgBedTimeDiffHours.formatHHMM,
                            bedTimeDaysOfWeek,
                            averageWakeUpTime.formatHHMM,
                            state.statistics.avgWakeUpTimeDiffHours.formatHHMM,
                            wakeupTimeDaysOfWeek)
                }
            }
        }
    }

    private fun navigateToDetails(id: Int?) {
        id?.let { startActivity(DetailIntent(id)) }
    }

}