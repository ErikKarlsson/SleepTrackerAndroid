package net.erikkarlsson.simplesleeptracker.statistics

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.example.android.architecture.blueprints.todoapp.mvibase.MviView
import dagger.android.AndroidInjection
import io.reactivex.Observable
import kotterknife.bindView
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import timber.log.Timber
import javax.inject.Inject

class StatisticsActivity : AppCompatActivity(), MviView<StatisticsIntent, StatisticsViewState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val statisticsViewModel: StatisticsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(StatisticsViewModel::class.java)
    }

    private val averageSleepText: TextView by bindView(R.id.average_sleep_text)

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        statisticsViewModel.statesLiveData().observe(this, Observer { renderLiveData(it) })
        statisticsViewModel.processIntents(intents())
    }

    override fun intents(): Observable<StatisticsIntent> {
        return Observable.just(StatisticsIntent.InitialIntent)
    }

    fun renderLiveData(state: StatisticsViewState?) {
        state?.let {
            Timber.d("render %f", state.statistics.avgSleep)
            averageSleepText.setText(state.statistics.avgSleep.toString())
        }
    }

    override fun render(state: StatisticsViewState) {
    }

}