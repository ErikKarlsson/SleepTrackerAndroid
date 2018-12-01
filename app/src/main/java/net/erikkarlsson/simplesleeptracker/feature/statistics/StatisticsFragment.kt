package net.erikkarlsson.simplesleeptracker.feature.statistics

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.rxbinding2.widget.itemSelections
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_statistics.*
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import net.erikkarlsson.simplesleeptracker.domain.DateTimeProvider
import net.erikkarlsson.simplesleeptracker.elm.ElmViewModel
import javax.inject.Inject

class StatisticsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var ctx: Context

    @Inject
    lateinit var dateTimeProvider: DateTimeProvider

    lateinit var statisticsAdapter: StatisticsAdapter

    private val viewModel: StatisticsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(StatisticsViewModel::class.java)
    }

    private val disposables = CompositeDisposable()

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
        spinner.setTag(0) // Avoid triggering selection on initialization.

        val compareSpinnerAdapter = ArrayAdapter.createFromResource(ctx, R.array.statistic_compare_array, android.R.layout.simple_spinner_item)
        compareSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        compareSpinner.adapter = compareSpinnerAdapter
        compareSpinner.setTag(0) // Avoid triggering selection on initialization.

        statisticsAdapter = StatisticsAdapter(requireFragmentManager(), dateTimeProvider)

        viewPager.adapter = statisticsAdapter

        slidingTabLayout.setViewPager(viewPager)

        viewModel.state().observe(this, Observer { render(it) })
    }

    private fun render(state: StatisticsState?) {
        state?.let {
            statisticsAdapter.data = StatisticsItemData(it.dateRanges, it.filter)
            statisticsAdapter.notifyDataSetChanged()

            viewPager.setCurrentItem(it.dateRanges.size - 1, false)
            viewPager.invalidate()

            slidingTabLayout.setViewPager(viewPager)
            slidingTabLayout.isVisible = it.shouldShowTabs

            spinnerContainer.isVisible = !it.isEmpty
            emptyState.isVisible = !it.isLoading && it.isEmpty

            spinner.setSelection(it.filterOrdinal)

            compareSpinner.isVisible = it.shouldShowCompareFilter
            compareSpinner.setSelection(it.compareFilterOrdinal)

            // TODO (erikkarlsson): Hack to fix scroll not working.
            Handler().postDelayed({
                slidingTabLayout.scrollToTab(viewPager.currentItem, 0)
            }, 100)

        }
    }

    override fun onStart() {
        super.onStart()

        spinner.itemSelections()
                .subscribe { onFilterSelected(it) }
                .addTo(disposables)

        compareSpinner.itemSelections()
                .subscribe { onCompareSelected(it) }
                .addTo(disposables)
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    private fun onFilterSelected(index: Int) {
        if (spinner.getTag() == index) {
            return
        }
        spinner.setTag(index)
        val statisticsFilter = StatisticsFilter.values()[index]
        viewModel.dispatch(StatisticsFilterSelected(statisticsFilter))
    }

    private fun onCompareSelected(index: Int) {
        if (compareSpinner.getTag() == index) {
            return
        }
        compareSpinner.setTag(index)
        val compareFilter = CompareFilter.values()[index]
        viewModel.dispatch(CompareFilterSelected(compareFilter))
    }
}

class StatisticsViewModel @Inject constructor(statisticsComponent: StatisticsComponent) :
        ElmViewModel<StatisticsState, StatisticsMsg, StatisticsCmd>(statisticsComponent)
