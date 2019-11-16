package net.erikkarlsson.simplesleeptracker.features.statistics

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.jakewharton.rxbinding2.widget.itemSelections
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_statistics.*
import net.erikkarlsson.simplesleeptracker.domain.DateTimeProvider
import javax.inject.Inject

class StatisticsFragment : BaseMvRxFragment() {

    @Inject
    lateinit var ctx: Context

    @Inject
    lateinit var dateTimeProvider: DateTimeProvider

    @Inject
    lateinit var viewModelFactory: StatisticsViewModel.Factory

    lateinit var statisticsAdapter: StatisticsAdapter

    private val viewModel: StatisticsViewModel by fragmentViewModel()

    private val disposables = CompositeDisposable()

    private var prevState: StatisticsState = StatisticsState.empty()

    override fun onAttach(context: Context) {
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
        spinner.isEnabled = false

        val compareSpinnerAdapter = ArrayAdapter.createFromResource(ctx, R.array.statistic_compare_array, android.R.layout.simple_spinner_item)
        compareSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        compareSpinner.adapter = compareSpinnerAdapter
        compareSpinner.setTag(0) // Avoid triggering selection on initialization.

        statisticsAdapter = StatisticsAdapter(requireFragmentManager(), dateTimeProvider)

        viewPager.adapter = statisticsAdapter

        slidingTabLayout.setViewPager(viewPager)
    }

    override fun invalidate() = withState(viewModel) { state ->
        val hasFilterChanged = state.filter != prevState.filter

        val selectedItem = if (hasFilterChanged) {
            state.dateRanges.size - 1
        } else {
            viewPager.currentItem
        }

        val adapterData = when (state.shouldShowEmptyState) {
            true -> StatisticsItemData.emptyState()
            false -> StatisticsItemData(state.dateRanges, state.filter)
        }

        statisticsAdapter.data = adapterData
        statisticsAdapter.notifyDataSetChanged()

        viewPager.setCurrentItem(selectedItem, false)
        viewPager.invalidate()

        slidingTabLayout.setViewPager(viewPager)
        slidingTabLayout.isVisible = state.shouldShowTabs

        emptyGroup.isVisible = state.shouldShowEmptyState
        spinner.isEnabled = !state.shouldShowEmptyState

        spinner.setSelection(state.filterOrdinal)

        compareSpinner.isVisible = state.shouldShowCompareFilter
        compareSpinner.setSelection(state.compareFilterOrdinal)

        // TODO (erikkarlsson): Hack to fix scroll not working.
        Handler().postDelayed({
            if (isAdded) {
                slidingTabLayout.scrollToTab(viewPager.currentItem, 0)
            }
        }, 100)

        prevState = state
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
        viewModel.statisticsFilterSelected(statisticsFilter)
    }

    private fun onCompareSelected(index: Int) {
        if (compareSpinner.getTag() == index) {
            return
        }
        compareSpinner.setTag(index)
        val compareFilter = CompareFilter.values()[index]
        viewModel.compareFilterSelected(compareFilter)
    }
}
