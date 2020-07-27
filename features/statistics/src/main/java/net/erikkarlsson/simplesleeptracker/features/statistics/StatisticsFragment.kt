package net.erikkarlsson.simplesleeptracker.features.statistics

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.erikkarlsson.simplesleeptracker.domain.DateTimeProvider
import net.erikkarlsson.simplesleeptracker.features.statistics.databinding.FragmentStatisticsBinding
import ru.ldralighieri.corbind.widget.itemSelections
import javax.inject.Inject

@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    @Inject
    lateinit var dateTimeProvider: DateTimeProvider

    lateinit var statisticsAdapter: StatisticsAdapter

    private val viewModel: StatisticsViewModel by viewModels()

    private var prevState: StatisticsState = StatisticsState.empty()

    private lateinit var binding: FragmentStatisticsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinnerAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.statistic_filter_array, android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = spinnerAdapter
        binding.spinner.setTag(0) // Avoid triggering selection on initialization.
        binding.spinner.isEnabled = false

        val compareSpinnerAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.statistic_compare_array, android.R.layout.simple_spinner_item)
        compareSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.compareSpinner.adapter = compareSpinnerAdapter
        binding.compareSpinner.setTag(0) // Avoid triggering selection on initialization.

        statisticsAdapter = StatisticsAdapter(parentFragmentManager, dateTimeProvider)

        binding.viewPager.adapter = statisticsAdapter

        binding.slidingTabLayout.setViewPager(binding.viewPager)

        viewModel.liveData.observe(viewLifecycleOwner, ::render)
    }

    fun render(state: StatisticsState) {
        val hasFilterChanged = state.filter != prevState.filter

        val selectedItem = if (hasFilterChanged) {
            state.dateRanges.size - 1
        } else {
            binding.viewPager.currentItem
        }

        val adapterData = when (state.shouldShowEmptyState) {
            true -> StatisticsItemData.emptyState()
            false -> StatisticsItemData(state.dateRanges, state.filter)
        }

        statisticsAdapter.data = adapterData
        statisticsAdapter.notifyDataSetChanged()

        binding.viewPager.setCurrentItem(selectedItem, false)
        binding.viewPager.invalidate()

        binding.slidingTabLayout.setViewPager(binding.viewPager)
        binding.slidingTabLayout.isVisible = state.shouldShowTabs

        binding.emptyGroup.isVisible = state.shouldShowEmptyState
        binding.spinner.isEnabled = !state.shouldShowEmptyState

        binding.spinner.setSelection(state.filterOrdinal)

        binding.compareSpinner.isVisible = state.shouldShowCompareFilter
        binding.compareSpinner.setSelection(state.compareFilterOrdinal)

        // TODO (erikkarlsson): Hack to fix scroll not working.
        Handler().postDelayed({
            if (isAdded) {
                binding.slidingTabLayout.scrollToTab(binding.viewPager.currentItem, 0)
            }
        }, 100)

        prevState = state
    }

    override fun onStart() {
        super.onStart()

        binding.spinner.itemSelections()
                .onEach { onFilterSelected(it) }
                .launchIn(lifecycleScope)

        binding.compareSpinner.itemSelections()
                .onEach { onCompareSelected(it) }
                .launchIn(lifecycleScope)
    }

    private fun onFilterSelected(index: Int) {
        if (binding.spinner.getTag() == index) {
            return
        }
        binding.spinner.setTag(index)
        val statisticsFilter = StatisticsFilter.values()[index]
        viewModel.statisticsFilterSelected(statisticsFilter)
    }

    private fun onCompareSelected(index: Int) {
        if (binding.compareSpinner.getTag() == index) {
            return
        }
        binding.compareSpinner.setTag(index)
        val compareFilter = CompareFilter.values()[index]
        viewModel.compareFilterSelected(compareFilter)
    }
}
