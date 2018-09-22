package net.erikkarlsson.simplesleeptracker.feature.statistics

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.jakewharton.rxbinding2.widget.itemSelections
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_statistics.*
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import net.erikkarlsson.simplesleeptracker.elm.ElmViewModel
import javax.inject.Inject

class StatisticsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var ctx: Context

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
        spinner.setSelection(0)

        statisticsAdapter = StatisticsAdapter(requireFragmentManager())

        viewPager.adapter = statisticsAdapter

        viewModel.state().observe(this, Observer { render(it) })
    }

    private fun render(state: StatisticsState?) {
        state?.let {
            statisticsAdapter.data = StatisticsItemData(it.dateRanges, it.filter)
            statisticsAdapter.notifyDataSetChanged()
            viewPager.setCurrentItem(it.dateRanges.size - 1, false)
            viewPager.invalidate()
        }
    }

    override fun onStart() {
        super.onStart()

        spinner.itemSelections()
                .subscribe { onFilterSelected(it) }
                .addTo(disposables)
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    private fun onFilterSelected(index: Int) {
        val statisticsFilter = StatisticsFilter.values()[index]
        viewModel.dispatch(StatisticsFilterSelected(statisticsFilter))
    }
}

class StatisticsViewModel @Inject constructor(statisticsComponent: StatisticsComponent) :
        ElmViewModel<StatisticsState, StatisticsMsg, StatisticsCmd>(statisticsComponent)
