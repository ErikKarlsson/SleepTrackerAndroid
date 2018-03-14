package net.erikkarlsson.simplesleeptracker.statistics

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
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
import net.erikkarlsson.simplesleeptracker.details.DetailsActivity
import net.erikkarlsson.simplesleeptracker.details.EXTRA_SLEEP_ID
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import net.erikkarlsson.simplesleeptracker.domain.Sleep
import net.erikkarlsson.simplesleeptracker.util.scanMap
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
            with(state.statistics) {
                averageSleepText.text = String.format("Average sleep: %f\n" +
                        "Longest sleep: %f\n" +
                        "Shortest sleep: %f\n" +
                        "Average bed time: %s\n" +
                        "Average wake up time: %s",
                        avgSleep, longestSleep, shortestSleep, averageBedTime.toString(), averageWakeUpTime.toString())
            }
        }
    }

    private fun navigateToDetails(id: Int?) {
        id?.let {
            val intent = Intent(this, DetailsActivity::class.java)
            intent.putExtra(EXTRA_SLEEP_ID, id)
            startActivity(intent)
        }
    }

}