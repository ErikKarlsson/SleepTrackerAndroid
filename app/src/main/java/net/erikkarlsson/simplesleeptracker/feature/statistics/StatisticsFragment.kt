package net.erikkarlsson.simplesleeptracker.feature.statistics

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.NavHostFragment
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.itemSelections
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_statistics.*
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import net.erikkarlsson.simplesleeptracker.elm.ElmViewModel
import net.erikkarlsson.simplesleeptracker.feature.diary.DiaryFragmentDirections
import net.erikkarlsson.simplesleeptracker.util.formatDateDisplayName
import net.erikkarlsson.simplesleeptracker.util.formatDisplayName
import net.erikkarlsson.simplesleeptracker.util.formatHHMM
import net.erikkarlsson.simplesleeptracker.util.formatHoursMinutes
import net.erikkarlsson.simplesleeptracker.util.formatHoursMinutesWithPrefix
import net.erikkarlsson.simplesleeptracker.util.formatPercentage
import timber.log.Timber
import javax.inject.Inject

class StatisticsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var ctx: Context

    private val viewModel: StatisticsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(StatisticsViewModel::class.java)
    }

    private val disposables = CompositeDisposable()

    private val adapter = SleepAdapter { navigateToDetails(it.id) }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinnerAdapter = ArrayAdapter.createFromResource(ctx, R.array.statistic_filter_array, android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        recyclerView.layoutManager = LinearLayoutManager(ctx)
        recyclerView.adapter = adapter

        viewModel.state().observe(this, Observer { render(it) })
    }

    override fun onStart() {
        super.onStart()

        spinner.itemSelections().subscribe({ onFilterSelected(it) }).addTo(disposables)

        Observable.merge(toggleSleepButton.clicks(), owlImage.clicks())
                .subscribe({ viewModel.dispatch(ToggleSleepClicked) },
                           { Timber.e(it, "Error merging clicks") })
                .addTo(disposables)
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    private fun render(state: StatisticsState?) {
        state?.let {
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

    private fun onFilterSelected(index: Int) {
        val statisticsFilter = StatisticsFilter.values()[index]
        viewModel.dispatch(StatisticsFilterSelected(statisticsFilter))
    }

    private fun navigateToDetails(id: Int?) {
        id?.let {
            val action = DiaryFragmentDirections.actionDiaryToDetail().setSleepId(id)
            NavHostFragment.findNavController(this).navigate(action)
        }
    }
}

class StatisticsViewModel @Inject constructor(statisticsComponent: StatisticsComponent) :
        ElmViewModel<StatisticsState, StatisticsMsg, StatisticsCmd>(statisticsComponent)
