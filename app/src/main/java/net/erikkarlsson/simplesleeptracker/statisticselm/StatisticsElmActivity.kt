package net.erikkarlsson.simplesleeptracker.statisticselm

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import dagger.android.AndroidInjection
import kotterknife.bindView
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import timber.log.Timber
import javax.inject.Inject

class StatisticsElmActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val statisticsElmViewModel: StatisticsElmViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(StatisticsElmViewModel::class.java)
    }

    private val averageSleepText: TextView by bindView(R.id.average_sleep_text)

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        statisticsElmViewModel.state().observe(this, Observer { renderLiveData(it) })
    }

    override fun onStart() {
        super.onStart()
        statisticsElmViewModel.dispatch(StatisticsMsg.InitialIntent)
    }

    fun renderLiveData(state: StatisticsState?) {
        state?.let {
            Timber.d("render %f", state.statistics.avgSleep)
            averageSleepText.setText(String.format("%f : %b", state.statistics.avgSleep, state.isConnectedToInternet))
        }
    }

}