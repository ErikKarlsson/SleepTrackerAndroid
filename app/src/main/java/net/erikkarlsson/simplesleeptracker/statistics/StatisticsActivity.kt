package net.erikkarlsson.simplesleeptracker.statistics

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.example.android.architecture.blueprints.todoapp.mvibase.MviView
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotterknife.bindView
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import net.erikkarlsson.simplesleeptracker.sleepappwidget.StatisticsIntent
import timber.log.Timber
import javax.inject.Inject

class StatisticsActivity : AppCompatActivity(), MviView<StatisticsIntent, StatisticsViewState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val statisticsViewModel: StatisticsViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)
            .get(StatisticsViewModel::class.java)
    }

    private val averageSleepText: TextView by bindView(R.id.average_sleep_text)

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        statisticsViewModel.processIntents(intents())

        statisticsViewModel.states()
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(this::render,
                    { Timber.e(it, "Error receiving states") })
    }

    override fun intents(): Observable<StatisticsIntent> {
        return Observable.just(StatisticsIntent.InitialIntent)
    }

    override fun render(state: StatisticsViewState) {
        averageSleepText.setText(state.statistics.avgSleep.toString())
    }

}